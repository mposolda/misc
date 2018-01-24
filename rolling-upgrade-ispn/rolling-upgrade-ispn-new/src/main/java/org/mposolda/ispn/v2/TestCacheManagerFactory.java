package org.mposolda.ispn.v2;

import org.infinispan.client.hotrod.ProtocolVersion;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.StoreConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.remote.configuration.ExhaustedAction;
import org.infinispan.persistence.remote.configuration.RemoteStoreConfigurationChildBuilder;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
class TestCacheManagerFactory {


    <T extends StoreConfigurationBuilder<?, T> & RemoteStoreConfigurationChildBuilder<T>> EmbeddedCacheManager createManager(int port, String cacheName, Class<T> builderClass) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("jgroups.tcp.port", "53715");
        GlobalConfigurationBuilder gcb = new GlobalConfigurationBuilder();

        boolean clustered = false;
        boolean async = false;
        boolean allowDuplicateJMXDomains = true;

        if (clustered) {
            gcb = gcb.clusteredDefault();
            gcb.transport().clusterName("test-clustering");
        }

        gcb.globalJmxStatistics().allowDuplicateDomains(allowDuplicateJMXDomains);

        EmbeddedCacheManager cacheManager = new DefaultCacheManager(gcb.build());

        Configuration invalidationCacheConfiguration = getCacheBackedByRemoteStore(port, cacheName, builderClass);

        cacheManager.defineConfiguration(cacheName, invalidationCacheConfiguration);
        return cacheManager;

    }


    private <T extends StoreConfigurationBuilder<?, T> & RemoteStoreConfigurationChildBuilder<T>> Configuration getCacheBackedByRemoteStore(int port, String cacheName, Class<T> builderClass) {
        ConfigurationBuilder cacheConfigBuilder = new ConfigurationBuilder();

        String host = "localhost";
        //int port = 11222;

        return cacheConfigBuilder.persistence().addStore(builderClass)
                .fetchPersistentState(false)
                .ignoreModifications(false)
                .purgeOnStartup(false)
                .preload(false)
                .shared(true)
                .remoteCacheName(cacheName)
                .rawValues(true)
                .forceReturnValues(false)
                .protocolVersion(ProtocolVersion.PROTOCOL_VERSION_25)
                .hotRodWrapping(true)
                //.marshaller(KeycloakHotRodMarshallerFactory.class.getName())
                .addServer()
                .host(host)
                .port(port)
                .connectionPool()
                .maxActive(20)
                .exhaustedAction(ExhaustedAction.CREATE_NEW)
                .async()
                .   enabled(false).build();
    }
}
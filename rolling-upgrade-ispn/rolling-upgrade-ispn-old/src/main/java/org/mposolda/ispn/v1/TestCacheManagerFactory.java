package org.mposolda.ispn.v1;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.StoreConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.remote.configuration.ExhaustedAction;
import org.infinispan.persistence.remote.configuration.RemoteStoreConfigurationChildBuilder;
import org.mposolda.ispn.entity.UserSessionEntity;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class TestCacheManagerFactory {

    // REMOTE STORE

    public <T extends StoreConfigurationBuilder<?, T> & RemoteStoreConfigurationChildBuilder<T>> EmbeddedCacheManager createManager(
            int port, String cacheName, Class<T> builderClass, boolean clustered, boolean remoteStore) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("jgroups.tcp.port", "53715");
        GlobalConfigurationBuilder gcb = new GlobalConfigurationBuilder();

        boolean async = false;
        boolean allowDuplicateJMXDomains = true;

        if (clustered) {
            gcb = gcb.clusteredDefault();
            gcb.transport().clusterName("test-clustering");
        }

        gcb.globalJmxStatistics().allowDuplicateDomains(allowDuplicateJMXDomains);

        EmbeddedCacheManager cacheManager = new DefaultCacheManager(gcb.build());

        if (remoteStore) {
            Configuration invalidationCacheConfiguration = getCacheBackedByRemoteStore(port, cacheName, builderClass);
            cacheManager.defineConfiguration(cacheName, invalidationCacheConfiguration);
        } else {
            Configuration invalidationCacheConfiguration = getClusteredCache();
            cacheManager.defineConfiguration(cacheName, invalidationCacheConfiguration);
        }

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


    public RemoteCache<String, UserSessionEntity> bootstrapRemoteCache(int port, String cacheName) {
        org.infinispan.client.hotrod.configuration.ConfigurationBuilder builder = new org.infinispan.client.hotrod.configuration.ConfigurationBuilder();

        builder.addServer()
                .host("localhost")
                .port(port);

        builder.connectionPool().maxActive(20)
                .forceReturnValues(false);

        RemoteCacheManager manager = new RemoteCacheManager(builder.build());
        return manager.getCache(cacheName);
    }


    private Configuration getClusteredCache() {
        ConfigurationBuilder cacheConfigBuilder = new ConfigurationBuilder();

        return cacheConfigBuilder
                .clustering().cacheMode(CacheMode.DIST_SYNC)
                .build();
    }

}

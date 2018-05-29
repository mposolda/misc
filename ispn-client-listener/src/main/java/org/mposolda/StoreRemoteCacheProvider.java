package org.mposolda;

import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.StoreConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.manager.PersistenceManager;
import org.infinispan.persistence.remote.RemoteStore;
import org.infinispan.persistence.remote.configuration.ExhaustedAction;
import org.infinispan.persistence.remote.configuration.RemoteStoreConfigurationBuilder;
import org.infinispan.persistence.remote.configuration.RemoteStoreConfigurationChildBuilder;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StoreRemoteCacheProvider implements ClientListenerFailoverTest.RemoteCacheProvider {


    @Override
    public RemoteCache getRemoteCache() {
        EmbeddedCacheManager mgr = createManager("default");
        Cache cache = mgr.getCache("default");

        RemoteStore remoteStore = cache.getAdvancedCache().getComponentRegistry().getComponent(PersistenceManager.class).getStores(RemoteStore.class).iterator().next();
        return remoteStore.getRemoteCache();
    }


    <T extends StoreConfigurationBuilder<?, T> & RemoteStoreConfigurationChildBuilder<T>> EmbeddedCacheManager createManager(String cacheName) {
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


        Configuration invalidationCacheConfiguration = getCacheBackedByRemoteStore(cacheName);

        cacheManager.defineConfiguration(cacheName, invalidationCacheConfiguration);
        return cacheManager;

    }


    private <T extends StoreConfigurationBuilder<?, T> & RemoteStoreConfigurationChildBuilder<T>> Configuration getCacheBackedByRemoteStore(String cacheName) {
        ConfigurationBuilder cacheConfigBuilder = new ConfigurationBuilder();

        String host = "localhost";
        //int port = threadId==1 ? 12232 : 13232;
        int port = 11222;

        return cacheConfigBuilder.persistence().addStore(RemoteStoreConfigurationBuilder.class)
                .fetchPersistentState(false)
                .ignoreModifications(false)
                .purgeOnStartup(false)
                .preload(false)
                .shared(true)
                .remoteCacheName(cacheName)
                .rawValues(true)
                .forceReturnValues(false)
                .addServer()
                    .host(host)
                    .port(port)
                .connectionPool()
                    .maxActive(20)
                    .exhaustedAction(ExhaustedAction.CREATE_NEW)
                .async()
                    .enabled(false).build();
    }
}

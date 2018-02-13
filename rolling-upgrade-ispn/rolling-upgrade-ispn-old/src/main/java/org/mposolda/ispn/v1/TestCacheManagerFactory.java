package org.mposolda.ispn.v1;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.util.FileLookup;
import org.infinispan.commons.util.FileLookupFactory;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.StoreConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.remote.configuration.ExhaustedAction;
import org.infinispan.persistence.remote.configuration.RemoteStoreConfigurationChildBuilder;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.jgroups.JChannel;
import org.mposolda.ispn.entity.UserSessionEntity;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class TestCacheManagerFactory {

    // REMOTE STORE

    public EmbeddedCacheManager createClusteredCacheManager(
            String cacheName, String nodeName, String jgroupsUdpAddr) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("jgroups.tcp.port", "53715");
        GlobalConfigurationBuilder gcb = new GlobalConfigurationBuilder();

        boolean allowDuplicateJMXDomains = true;


        gcb = gcb.clusteredDefault();
        gcb.transport().clusterName("test-clustering");
        configureTransport(gcb, nodeName, jgroupsUdpAddr);

        gcb.globalJmxStatistics().allowDuplicateDomains(allowDuplicateJMXDomains);

        EmbeddedCacheManager cacheManager = new DefaultCacheManager(gcb.build());

        Configuration invalidationCacheConfiguration = getClusteredCache();
        cacheManager.defineConfiguration(cacheName, invalidationCacheConfiguration);

        return cacheManager;
    }


    public <T extends StoreConfigurationBuilder<?, T> & RemoteStoreConfigurationChildBuilder<T>> EmbeddedCacheManager createCacheManagerWithRemoteCache(
            int port, String cacheName, Class<T> builderClass) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("jgroups.tcp.port", "53715");
        GlobalConfigurationBuilder gcb = new GlobalConfigurationBuilder();

        boolean allowDuplicateJMXDomains = true;

//        if (clustered) {
//            gcb = gcb.clusteredDefault();
//            gcb.transport().clusterName("test-clustering");
//        }

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

        builder.connectionPool()
                  .maxActive(20)
                  .exhaustedAction(org.infinispan.client.hotrod.configuration.ExhaustedAction.CREATE_NEW)
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



    private static final Object CHANNEL_INIT_SYNCHRONIZER = new Object();

    protected void configureTransport(GlobalConfigurationBuilder gcb, String nodeName, String jgroupsUdpMcastAddr) {

            FileLookup fileLookup = FileLookupFactory.newInstance();

            synchronized (CHANNEL_INIT_SYNCHRONIZER) {
                System.setProperty("jgroups.udp.mcast_addr", jgroupsUdpMcastAddr);

                try {
                    // Compatibility with Wildfly
                    JChannel channel = new JChannel(fileLookup.lookupFileLocation("default-configs/default-jgroups-udp.xml", this.getClass().getClassLoader()));
                    channel.setName(nodeName);
                    JGroupsTransport transport = new JGroupsTransport(channel);

                    gcb.transport()
                            .nodeName(nodeName)
                            .transport(transport)
                            .globalJmxStatistics()
                            .jmxDomain("jmx-" + nodeName)
                            .enable()
                    ;

                    System.out.println("Configured jgroups transport with the channel name: " + nodeName);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

    }

}

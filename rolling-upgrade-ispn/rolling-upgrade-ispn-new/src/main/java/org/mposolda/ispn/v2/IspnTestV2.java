package org.mposolda.ispn.v2;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.cli.configuration.CLInterfaceLoaderConfigurationBuilder;
import org.mposolda.ispn.entity.UserSessionEntity;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class IspnTestV2 {

    private static final String CACHE_NAME = "sessions";

    public static void main(String[] args) throws Exception {
        EmbeddedCacheManager node1 = createManager();
        Cache<String, UserSessionEntity> cache = node1.getCache(CACHE_NAME);

        TestsuiteCLI<String, UserSessionEntity> cli = new TestsuiteCLI<>(cache);
        cli.start();
    }


    public static EmbeddedCacheManager createManager() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("jgroups.tcp.port", "53715");

        // This is different than IspnTestV1 !!!
        System.setProperty("jgroups.udp.mcast_addr", "226.6.7.9");
        GlobalConfigurationBuilder gcb = new GlobalConfigurationBuilder();

        boolean clustered = true;

        boolean allowDuplicateJMXDomains = true;

        if (clustered) {
            gcb = gcb.clusteredDefault();
            gcb.transport().clusterName("test-clustering");
        }

        gcb.globalJmxStatistics()
                //.allowDuplicateDomains(allowDuplicateJMXDomains)
                .cacheManagerName("myCacheManager")
                .enable();

        EmbeddedCacheManager cacheManager = new DefaultCacheManager(gcb.build());


        ConfigurationBuilder cacheConfigBuilder = new ConfigurationBuilder();
        if (clustered) {
            cacheConfigBuilder.clustering().cacheMode(CacheMode.REPL_SYNC);
        }

        // CLI loader
        cacheConfigBuilder.persistence()
                .addStore(CLInterfaceLoaderConfigurationBuilder.class)
                .connectionString("jmx://localhost:1255/myCacheManager/sessions");

        Configuration cacheConfiguration = cacheConfigBuilder.build();

        cacheManager.defineConfiguration(CACHE_NAME, cacheConfiguration);
        return cacheManager;
    }


    public void testListener() throws Exception {
        EmbeddedCacheManager node1 = createManager();

        Cache<String, Object> node1Cache = node1.getCache(CACHE_NAME);

        int size = node1Cache.size();


    }
}

package org.mposolda.ispn;

import org.infinispan.client.hotrod.ProtocolVersion;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;


public class InfinispanRemote {

    public static void main(String[] args) {
        // Create a configuration for a locally-running server
        ConfigurationBuilder builder = new ConfigurationBuilder();

        // Adding protocol when
        builder.addServer()
                .host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
                .version(ProtocolVersion.PROTOCOL_VERSION_25);

        // Connect to the server
        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());
        // Obtain the remote cache
        RemoteCache<String, String> cache = cacheManager.getCache();
        /// Store a value
        cache.put("key", "value");
        // Retrieve the value and print it out
        System.out.printf("key = %s\n", cache.get("key"));
        // Stop the cache manager and release all resources

        System.out.println("Calling cache.keySet().iterator().hasNext()");

        // This throws error client infinispan version is 9.2.0.CR1 and infinispan server version is 8.2.6 (or JDG 7.1)
        boolean b = cache.keySet().iterator().hasNext();

        cacheManager.stop();
    }

}

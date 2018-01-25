package org.mposolda.ispn.v1;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.remote.configuration.RemoteStoreConfigurationBuilder;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RemoteIspnTestV1 {

    private static final String CACHE_NAME = "default";

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("jdg.port", "11222"));
        System.out.println("Using JDG on localhost:" + port);

        EmbeddedCacheManager manager1 = createManager(port);
        Cache<String, ?> cache = manager1.getCache(CACHE_NAME);

        TestsuiteCLI<String, ?> cli = new TestsuiteCLI<>(cache);
        cli.start();
    }

    private static EmbeddedCacheManager createManager(int port) {
        return new TestCacheManagerFactory().createManager(port, CACHE_NAME, RemoteStoreConfigurationBuilder.class);
    }
}

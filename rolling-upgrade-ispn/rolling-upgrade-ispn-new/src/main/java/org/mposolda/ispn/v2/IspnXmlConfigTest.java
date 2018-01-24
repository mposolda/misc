package org.mposolda.ispn.v2;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class IspnXmlConfigTest {

    public static void main(String[] args) throws Exception {
        EmbeddedCacheManager manager = new DefaultCacheManager("my-ispn-config.xml");
        try {
//            Cache<?, ?> cache1 = manager.getCache("foo");
//            System.out.println(cache1);

            Configuration cfg = manager.getCacheConfiguration("foo");
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.read(cfg);
            Configuration cfg2 = builder.build();

            manager.defineConfiguration("foo2", cfg2);
            Cache<?, ?> cache2 = manager.getCache("foo2");
            System.out.println(cache2);
        } finally {
            manager.stop();
        }
    }
}

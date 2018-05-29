package org.mposolda;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.jboss.logging.Logger;

/**
 * Registers the client listener and log the event always when listener is triggered.
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ClientListenerFailoverTest {

    protected static final Logger logger = Logger.getLogger(ClientListenerFailoverTest.class);

    public static void main(String[] args) throws Exception {
        RemoteCache<String, String> remoteCache = getRemoteCache();
        remoteCache.clear();

        logger.info("RemoteCache created");

        // Add listener
        remoteCache.addClientListener(new HotRodListener());

        // Start worker
        int counter = 0;


        while (true) {
            Thread.sleep(1000);

            try {
                String key = "key-" + counter++;
                logger.infof("Adding key: %s", key);
                remoteCache.put(key, key);
            } catch (Exception e) {
                logger.error("Exception when adding key: " + e.getMessage());
            }
        }

    }

    private static RemoteCache getRemoteCache() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder
            .addServer()
                .host("127.0.0.1")
                .port(11222);

        return new RemoteCacheManager(builder.build()).getCache("default");
    }



    @ClientListener
    public static class HotRodListener {

        public HotRodListener() {
        }

        @ClientCacheEntryCreated
        public void created(ClientCacheEntryCreatedEvent event) {
            String cacheKey = (String) event.getKey();
            logger.infof("Listener: created: %s", cacheKey);
        }

    }

//
//    public interface RemoteCacheProvider {
//
//        RemoteCache getRemoteCache();
//
//    }
}

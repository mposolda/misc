package org.mposolda.ispn.concurrent;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.VersionedValue;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryModified;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryModifiedEvent;
import org.infinispan.client.hotrod.event.ClientEvent;
import org.infinispan.context.Flag;
import org.jboss.logging.Logger;
import org.mposolda.ispn.entity.UserSessionEntity;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@ClientListener
public class RemoteCacheSessionListener {

    protected static final Logger logger = Logger.getLogger(RemoteCacheSessionListener.class);

    private Cache<String, UserSessionEntity> cache;
    private RemoteCache<String, UserSessionEntity> remoteCache;
    private String myAddress;
    private ExecutorService executor;


    protected RemoteCacheSessionListener() {
    }


    protected void init(Cache<String, UserSessionEntity> cache, RemoteCache<String, UserSessionEntity> remoteCache, String myNodeName) {
        this.cache = cache;
        this.remoteCache = remoteCache;

        this.myAddress = myNodeName;

        this.executor = Executors.newCachedThreadPool();
    }


    @ClientCacheEntryCreated
    public void created(ClientCacheEntryCreatedEvent<String> event) {
        String key = event.getKey();

        if (shouldUpdateLocalCache(event.getType(), key, event.isCommandRetried())) {
            this.executor.submit(() -> {

                // TODO: Do this rather with remoteStore... So newCache needs to be configured with remoteStore
                UserSessionEntity value = remoteCache.get(key);
                cache.put(key, value);

            });
        }
    }


    @ClientCacheEntryModified
    public void updated(ClientCacheEntryModifiedEvent<String> event) {
        String key = event.getKey();

        if (shouldUpdateLocalCache(event.getType(), key, event.isCommandRetried())) {

            this.executor.submit(() -> {

                replaceRemoteEntityInCache(key, event.getVersion());

            });
        }
    }

    private static final int MAXIMUM_REPLACE_RETRIES = 10;

    private void replaceRemoteEntityInCache(String key, long eventVersion) {
        // TODO can be optimized and remoteSession sent in the event itself?
        boolean replaced = false;
        int replaceRetries = 0;
        int sleepInterval = 25;
        do {
            replaceRetries++;

            UserSessionEntity localSession = cache.get(key);
            VersionedValue<UserSessionEntity> remoteSessionVersioned = remoteCache.getVersioned(key);

            // Probably already removed
            if (remoteSessionVersioned == null || remoteSessionVersioned.getValue() == null) {
                logger.debugf("Entity '%s' not present in remoteCache. Ignoring replace",
                        key.toString());
                return;
            }

            if (remoteSessionVersioned.getVersion() < eventVersion) {
                try {
                    logger.debugf("Got replace remote entity event prematurely for entity '%s', will try again. Event version: %d, got: %d",
                            key.toString(), eventVersion, remoteSessionVersioned.getVersion());
                    Thread.sleep(new Random().nextInt(sleepInterval));  // using exponential backoff
                    continue;
                } catch (InterruptedException ex) {
                    continue;
                } finally {
                    sleepInterval = sleepInterval << 1;
                }
            }
            UserSessionEntity remoteSession = remoteSessionVersioned.getValue();

            logger.debugf("Read session entity from the remote cache: %s . replaceRetries=%d", remoteSession.toString(), replaceRetries);

            //SessionEntityWrapper<V> sessionWrapper = remoteSession.mergeRemoteEntityWithLocalEntity(localEntityWrapper);

            // We received event from remoteCache, so we won't update it back
            if (localSession == null) {
                UserSessionEntity existing = cache
                        .getAdvancedCache().withFlags(Flag.SKIP_CACHE_STORE, Flag.SKIP_CACHE_LOAD, Flag.IGNORE_RETURN_VALUES)
                        .putIfAbsent(key, remoteSession);
                replaced = existing==null;
            } else {
                replaced = cache
                        .getAdvancedCache().withFlags(Flag.SKIP_CACHE_STORE, Flag.SKIP_CACHE_LOAD, Flag.IGNORE_RETURN_VALUES)
                        .replace(key, localSession, remoteSession);
            }

            if (! replaced) {
                logger.debugf("Did not succeed in merging sessions, will try again: %s", remoteSession.toString());
            }
        } while (replaceRetries < MAXIMUM_REPLACE_RETRIES && ! replaced);
    }


    // For distributed caches, ensure that local modification is executed just on owner OR if event.isCommandRetried
    protected boolean shouldUpdateLocalCache(ClientEvent.Type type, String key, boolean commandRetried) {
        boolean result;

        // Case when cache is stopping or stopped already
        if (!cache.getStatus().allowInvocations()) {
            return false;
        }

        String keyAddress = cache.getAdvancedCache().getDistributionManager().getPrimaryLocation(key).toString();
        result = myAddress.equals(keyAddress);

        logger.debugf("Received event from remote store. Event '%s', key '%s', skip '%b'", type.toString(), key, !result);

        return result;
    }



    public static RemoteCacheSessionListener createListener(Cache<String, UserSessionEntity> cache, RemoteCache<String, UserSessionEntity> remoteCache, String myNodeName) {
        /*boolean isCoordinator = InfinispanUtil.isCoordinator(cache);

        // Just cluster coordinator will fetch userSessions from remote cache.
        // In case that coordinator is failover during state fetch, there is slight risk that not all userSessions will be fetched to local cluster. Assume acceptable for now
        RemoteCacheSessionListener listener;
        if (isCoordinator) {
            logger.infof("Will fetch initial state from remote cache for cache '%s'", cache.getName());
            listener = new FetchInitialStateCacheListener();
        } else {
            logger.infof("Won't fetch initial state from remote cache for cache '%s'", cache.getName());
            listener = new DontFetchInitialStateCacheListener();
        }*/

        RemoteCacheSessionListener listener = new RemoteCacheSessionListener();
        listener.init(cache, remoteCache, myNodeName);

        return listener;
    }


}

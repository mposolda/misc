package org.mposolda.ispn.concurrent;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import jdk.nashorn.internal.runtime.regexp.joni.SearchAlgorithm;
import org.infinispan.Cache;
import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.VersionedValue;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.remote.configuration.RemoteStoreConfigurationBuilder;
import org.mposolda.ispn.entity.UserSessionEntity;
import org.mposolda.ispn.v1.TestCacheManagerFactory;

/**
 * Test rolling upgrade for cluster (no cross-dc) environment.
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ClusterRollingUpgradeTest {

    private static final String CACHE_NAME = "default";

    private static final int TASK_SLEEP_MS = 10;

    private static final int REMOTE_CACHE_PORT = 11222;

    private static final int SLEEP_WITHOUT_REMOTE_CACHE = 30000;

    private static final int SLEEP_WITH_REMOTE_CACHE = 30000;


    private static RemoteCache<String, UserSessionEntity> remoteCache;
    private static AtomicInteger counter = new AtomicInteger();
    private static AtomicBoolean shouldUseRemoteCache = new AtomicBoolean(false);


    public static void main(String[] args) {
        EmbeddedCacheManager node1 = createManager();
        Cache<String, UserSessionEntity> cache = node1.getCache(CACHE_NAME);

        InserterWorker inserter = new InserterWorker(cache);
        UpdaterWorker updater = new UpdaterWorker(cache);
        new Thread(inserter).start();
        new Thread(updater).start();
        System.out.println("Workers started");

        sleep(SLEEP_WITHOUT_REMOTE_CACHE);

        System.out.println("Adding remote cache");
        remoteCache = new TestCacheManagerFactory().bootstrapRemoteCache(REMOTE_CACHE_PORT, CACHE_NAME);

        // Just doublecheck that there is no some remaining state
        if (remoteCache.size() != 0) {
            throw new RuntimeException("Remote cache already had " + remoteCache.size() + " existing items in it!!!");
        }
        System.out.println("Remote cache successfully started. Counter: " + counter.get());


        addExistingItemsToRemoteCache(cache);
        System.out.println("Inserted items to remote cache. Counter: " + counter.get());

        shouldUseRemoteCache.set(true);
        System.out.println("Switched shouldUseRemoteCache to true. Counter: " + counter.get());

        sleep(SLEEP_WITH_REMOTE_CACHE);

        System.out.println("Stop threads");
        inserter.stop();
        updater.stop();
        System.out.println("Checking contents");

        // Just some syncs sleep before test the content
        sleep(2000);

        if (testCache(cache)) {
            System.out.println("Test cache PASSED");
        }
    }


    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }


    private static EmbeddedCacheManager createManager() {
        return new TestCacheManagerFactory().createManager(1111, CACHE_NAME, RemoteStoreConfigurationBuilder.class, true, false);
    }



    private static abstract class AbstractWorker implements Runnable {

        Cache<String, UserSessionEntity> cache;

        private AtomicBoolean stopped = new AtomicBoolean(false);

        private AbstractWorker(Cache<String, UserSessionEntity> cache) {
            this.cache = cache;
        }

        @Override
        public void run() {
            while (!stopped.get()) {
                try {
                    Thread.sleep(TASK_SLEEP_MS);
                } catch (Exception e) {
                    throw new RuntimeException("INTERRUPT" , e);
                }

                runTask();
            }
        }

        protected abstract void runTask();

        public void stop() {
            this.stopped.set(true);
        }
    }


    private static void addExistingItemsToRemoteCache(Cache<String, UserSessionEntity> cache) {
        cache.entrySet().stream().forEach(new Consumer<Map.Entry<String, UserSessionEntity>>() {

            @Override
            public void accept(Map.Entry<String, UserSessionEntity> entry) {
                // Sync entry to remoteCache
                remoteCache.put(entry.getKey(), entry.getValue());
            }

        });
    }


    private static class InserterWorker extends AbstractWorker {

        private InserterWorker(Cache<String, UserSessionEntity> cache) {
            super(cache);
        }

        @Override
        protected void runTask() {
            UserSessionEntity ent = new UserSessionEntity();
            int counterr = counter.incrementAndGet();
            String id = String.valueOf(counterr);
            ent.setId(id);
            ent.setRealmId("foo");
            ent.setLastSessionRefresh(new Random().nextInt(2000));
            cache.put(id, ent);

            if (counterr % 1000 == 0) {
                System.out.println(counterr + " values inserted");
            }

            if (shouldUseRemoteCache.get()) {
                remoteCache.put(id, ent);
            }
        }

    }


    private static class UpdaterWorker extends AbstractWorker {

        private int updateCounter = 0;

        private UpdaterWorker(Cache<String, UserSessionEntity> cache) {
            super(cache);
        }


        @Override
        protected void runTask() {
            updateCounter++;
            int currentCounter = counter.get();
            if (currentCounter <= 1) {
                return;
            }

            String randomId = String.valueOf(new Random().nextInt(currentCounter - 1) + 1);

            boolean replaced = false;
            UserSessionEntity neww = null;
            while (!replaced) {
                UserSessionEntity old = cache.get(randomId);
                neww = new UserSessionEntity();
                neww.setId(old.getId());
                neww.setRealmId(old.getRealmId());
                neww.setLastSessionRefresh(new Random().nextInt(2000));

                replaced = cache.replace(randomId, old, neww);
            }

//            System.out.println(updateCounter + " values updated");
            if (updateCounter % 1000 == 0) {
                System.out.println(updateCounter + " values updated");
            }

            if (shouldUseRemoteCache.get()) {
                replaced = false;
                while (!replaced) {
                    VersionedValue<UserSessionEntity> oldVersioned = remoteCache.getVersioned(randomId);

                    if (oldVersioned == null) {
                        System.out.println("Not present entity with key " + randomId + " in remote cache. Use putIfAbsent");

                        // Try to putIfAbsent our entity
                        UserSessionEntity existing = remoteCache
                                .withFlags(Flag.FORCE_RETURN_VALUE)
                                .putIfAbsent(randomId, neww);
                        replaced = existing==null;
                    } else {
                        replaced = remoteCache.replaceWithVersion(randomId, neww, oldVersioned.getVersion());
                        if (!replaced) {
                            System.out.println("REMOTE CACHE REPLACE FAILED FOR KEY " + randomId + ". Will retry.");
                        }
                    }
                }
            }
        }
    }


    private static boolean testCache(Cache<String, UserSessionEntity> cache) {
        int size1 = cache.size();
        int size2 = remoteCache.size();
        System.out.println("Cache size: " + size1 + ", Remote Cache size: " + size2);
        if (size1 != size2) {
            System.err.println("Sizes not match.");
            return false;
        }

        AtomicBoolean passed = new AtomicBoolean(true);

        cache.entrySet().stream().forEach(new Consumer<Map.Entry<String, UserSessionEntity>>() {

            @Override
            public void accept(Map.Entry<String, UserSessionEntity> entry) {
                String key = entry.getKey();
                UserSessionEntity value = entry.getValue();

                UserSessionEntity remoteCacheVal = remoteCache.get(key);
                if (remoteCacheVal == null) {
                    System.err.println("FAILED. No entity for key " + key);
                    passed.set(false);
                }

                // Sync entry to remoteCache
                if (!value.equals(remoteCacheVal)) {
                    System.err.println("NO EQUALS FOR KEY: " + key);
                    passed.set(false);
                }
            }

        });

        return passed.get();
    }
}

package org.mposolda.ispn.concurrent;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.infinispan.Cache;
import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.VersionedValue;
import org.infinispan.manager.EmbeddedCacheManager;
import org.mposolda.ispn.entity.UserSessionEntity;
import org.mposolda.ispn.v1.TestCacheManagerFactory;

/**
 * Test rolling upgrade for cluster (no cross-dc) environment.
 *
 * Requires JDG (or Infinispan server) to be running locally simply via "./standalone.sh"
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ClusterRollingUpgradeTest {

    private static final String CACHE_NAME = "default";

    private static final int TASK_SLEEP_MS = 1;

    private static final int REMOTE_CACHE_PORT = 11222;

    private static final int SLEEP_WITHOUT_REMOTE_CACHE = 10000;

    private static final int SLEEP_WITH_REMOTE_CACHE = 10000;


    private static RemoteCache<String, UserSessionEntity> remoteCache;
    private static AtomicBoolean shouldUseRemoteCache = new AtomicBoolean(false);


    public static void main(String[] args) {
        // Start clustered cache on 2 nodes
        EmbeddedCacheManager node11 = createManager("node11", "234.56.78.99");
        Cache<String, UserSessionEntity> cache11 = node11.getCache(CACHE_NAME);

        EmbeddedCacheManager node12 = createManager("node12", "234.56.78.99");
        Cache<String, UserSessionEntity> cache12 = node12.getCache(CACHE_NAME);


        // Start inserting and updating on both clustered caches
        AtomicInteger counter1 = new AtomicInteger();
        InserterWorker inserter1 = new InserterWorker("n1-", counter1, cache11);
        UpdaterWorker updater1 = new UpdaterWorker("n1-", counter1, cache11);
        new Thread(inserter1).start();
        new Thread(updater1).start();

        AtomicInteger counter2 = new AtomicInteger();
        InserterWorker inserter2 = new InserterWorker("n2-", counter2, cache12);
        UpdaterWorker updater2 = new UpdaterWorker("n2-", counter2, cache12);
        new Thread(inserter2).start();
        new Thread(updater2).start();

        System.out.println("Workers started");

        // Sleep when inserters+updaters in progress
        sleep(SLEEP_WITHOUT_REMOTE_CACHE);

        // Attach remote cache now
        System.out.println("Adding remote cache");
        remoteCache = new TestCacheManagerFactory().bootstrapRemoteCache(REMOTE_CACHE_PORT, CACHE_NAME);

        // Just doublecheck that there is no some remaining state
        if (remoteCache.size() != 0) {
            throw new RuntimeException("Remote cache already had " + remoteCache.size() + " existing items in it!!!");
        }
        System.out.println("Remote cache successfully started. Cluster cache size: " + cache11.size());

        // Ensure that further writes will be propagated to the remote cache
        shouldUseRemoteCache.set(true);
        System.out.println("Switched shouldUseRemoteCache to true. Cluster cache size: " + cache11.size());

        // Every cluster node will sync the items owned by him to the remote cache
        addExistingItemsToRemoteCache(cache11);
        System.out.println("node1: Finished sync entries to remoteCache");
        addExistingItemsToRemoteCache(cache12);
        System.out.println("node2: Finished sync entries to remoteCache");

        System.out.println("Inserted items to remote cache. Cluster cache size: " + cache11.size());

        sleep(SLEEP_WITH_REMOTE_CACHE);

        System.out.println("Stop threads");
        inserter1.stop();
        updater1.stop();
        inserter2.stop();
        updater2.stop();
        System.out.println("Checking contents");

        // Just some syncs sleep before test the content
        sleep(2000);

        if (testCache(cache11)) {
            System.out.println("Test cache cache11 PASSED");
        }
        if (testCache(cache12)) {
            System.out.println("Test cache cache12 PASSED");
        }

        cache11.getCacheManager().stop();
        cache12.getCacheManager().stop();
    }


    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }


    private static EmbeddedCacheManager createManager(String nodeName, String jgroupsUdpAddr) {
        return new TestCacheManagerFactory().createClusteredCacheManager(CACHE_NAME, nodeName, jgroupsUdpAddr);
    }



    private static abstract class AbstractWorker implements Runnable {

        final String prefix;
        final AtomicInteger counter;
        final Cache<String, UserSessionEntity> cache;

        private AtomicBoolean stopped = new AtomicBoolean(false);

        private AbstractWorker(String prefix, AtomicInteger counter, Cache<String, UserSessionEntity> cache) {
            this.prefix = prefix;
            this.counter = counter;
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


    // Method is executed separately for every cluster node. Hence every node updating just the local entities
    private static void addExistingItemsToRemoteCache(Cache<String, UserSessionEntity> cache) {
        FutureHelper futureHelper = new FutureHelper();

        cache
                .getAdvancedCache().withFlags(org.infinispan.context.Flag.CACHE_MODE_LOCAL)
                .entrySet().stream().forEach(new Consumer<Map.Entry<String, UserSessionEntity>>() {

            @Override
            public void accept(Map.Entry<String, UserSessionEntity> entry) {
                // Sync entries to remoteCache. Some may be already present (Those for which UpdaterTask updated items already).
                futureHelper.registerTask(() -> {

                    return remoteCache
                            .withFlags(Flag.FORCE_RETURN_VALUE)
                            .putIfAbsentAsync(entry.getKey(), entry.getValue());

                }, () -> {
                    remoteCache
                            .withFlags(Flag.FORCE_RETURN_VALUE)
                            .putIfAbsent(entry.getKey(), entry.getValue());
                });
            }

        });

        futureHelper.waitForAllToFinish();
    }


    private static class InserterWorker extends AbstractWorker {

        private InserterWorker(String prefix, AtomicInteger counter, Cache<String, UserSessionEntity> cache) {
            super(prefix, counter, cache);
        }

        @Override
        protected void runTask() {
            UserSessionEntity ent = new UserSessionEntity();
            int counterVal = counter.incrementAndGet();
            String id = prefix + counterVal;
            ent.setId(id);
            ent.setRealmId("foo");
            ent.setLastSessionRefresh(new Random().nextInt(2000));
            cache.put(id, ent);

            if (counterVal % 1000 == 0) {
                System.out.println(counterVal + " values inserted on " + prefix);
            }

            if (shouldUseRemoteCache.get()) {
                remoteCache.put(id, ent);
            }
        }

    }


    private static class UpdaterWorker extends AbstractWorker {

        private int updateCounter = 0;

        private UpdaterWorker(String prefix, AtomicInteger counter, Cache<String, UserSessionEntity> cache) {
            super(prefix, counter, cache);
        }


        @Override
        protected void runTask() {
            updateCounter++;
            int currentCounter = counter.get();
            if (currentCounter <= 1) {
                return;
            }

            String randomId = prefix + (new Random().nextInt(currentCounter - 1) + 1);

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
                System.out.println(updateCounter + " values updated on " + prefix);
            }

            if (shouldUseRemoteCache.get()) {
                replaced = false;
                while (!replaced) {
                    VersionedValue<UserSessionEntity> oldVersioned = remoteCache.getVersioned(randomId);

                    if (oldVersioned == null) {
                        //System.out.println("Not present entity with key " + randomId + " in remote cache. Use putIfAbsent");

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

        cache
                .getAdvancedCache().withFlags(org.infinispan.context.Flag.CACHE_MODE_LOCAL)
                .entrySet().stream().forEach(new Consumer<Map.Entry<String, UserSessionEntity>>() {

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

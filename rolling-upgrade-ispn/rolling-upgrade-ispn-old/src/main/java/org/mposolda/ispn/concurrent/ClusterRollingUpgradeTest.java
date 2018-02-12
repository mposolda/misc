package org.mposolda.ispn.concurrent;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jdk.nashorn.internal.runtime.regexp.joni.SearchAlgorithm;
import org.infinispan.Cache;
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

    public static void main(String[] args) {
        EmbeddedCacheManager node1 = createManager();
        Cache<String, UserSessionEntity> cache = node1.getCache(CACHE_NAME);

        InserterWorker inserter = new InserterWorker(cache);
        UpdaterWorker updater = new UpdaterWorker(cache);
        new Thread(inserter).start();
        new Thread(updater).start();
        System.out.println("Workers started");





    }


    private static EmbeddedCacheManager createManager() {
        return new TestCacheManagerFactory().createManager(1111, CACHE_NAME, RemoteStoreConfigurationBuilder.class, true, false);
    }

    private static AtomicInteger counter = new AtomicInteger();


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
            while (!replaced) {
                UserSessionEntity old = cache.get(randomId);
                UserSessionEntity neww = new UserSessionEntity();
                neww.setId(old.getId());
                neww.setRealmId(old.getRealmId());
                neww.setLastSessionRefresh(new Random().nextInt(2000));

                replaced = cache.replace(randomId, old, neww);
            }

//            System.out.println(updateCounter + " values updated");
            if (updateCounter % 1000 == 0) {
                System.out.println(updateCounter + " values updated");
            }
        }
    }
}

package org.mposolda.jpamig.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;

import org.jboss.logging.Logger;
import org.mposolda.jpamig.Company;
import org.mposolda.jpamig.Realm;
import org.mposolda.jpamig.common.JpaKeycloakTransaction;
import org.mposolda.jpamig.common.JpaProvider;
import org.mposolda.jpamig.common.JpaUtils;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class BootstrapTest {

    private static final int REALM_COUNT = 5;

    private static final Logger logger = Logger.getLogger(BootstrapTest.class);

    private static final AtomicInteger counter = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        List<AbstractTask> tasks = new LinkedList<>();

        Map<String, String> cfg = new HashMap<>();
        cfg.put("url", System.getProperty("keycloak.connectionsJpa.url"));
        cfg.put("driver", System.getProperty("keycloak.connectionsJpa.driver"));
        cfg.put("user", System.getProperty("keycloak.connectionsJpa.user"));
        cfg.put("password", System.getProperty("keycloak.connectionsJpa.password"));

        JpaProvider jpaProvider = new JpaProvider();
        jpaProvider.init(cfg);

        JpaUtils.wrapTransaction(jpaProvider, (EntityManager em, JpaKeycloakTransaction transaction) -> {
            Realm r = em.find(Realm.class, "1");

            // We don't yet have realms. Let's create them
            if (r == null) {
                logger.info("Creating realms");
                for (int i=1 ; i<REALM_COUNT+1 ; i++) {
                    r = new Realm();
                    r.setId(String.valueOf(i));
                    r.setName("realm " + i);
                    em.persist(r);
                }

            } else {
                logger.info("Realms already exist");
            }

            int count = getCompanyCount(em);
            logger.info("Existing company count: " + count);
            counter.set(count);
        });

        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (int i=0 ; i<10 ; i++) {
            tasks.add(new CreatorTask(jpaProvider));
        }
        for (int i=0 ; i<10 ; i++) {
            tasks.add(new ReaderTask(jpaProvider));
        }

        for (AbstractTask task : tasks) {
            executor.submit(task);
        }

        logger.info("Sleeping");

        Thread.sleep(3600000);

        logger.info("Stopping");
        for (AbstractTask task : tasks) {
            task.stop();
        }

        executor.awaitTermination(2, TimeUnit.SECONDS);
        logger.info("Going shutdown");

        executor.shutdown();
        jpaProvider.close();
    }


    private static abstract class AbstractTask implements Runnable {

        private final JpaProvider jpaProvider;

        private AtomicBoolean stopped = new AtomicBoolean(false);

        AbstractTask(JpaProvider jpaProvider) {
            this.jpaProvider = jpaProvider;
        }


        @Override
        public void run() {
            while (!stopped.get()) {
                try {
                    //Thread.sleep(1000);
                } catch (Exception e) {
                }

                JpaUtils.wrapTransaction(jpaProvider, (EntityManager em, JpaKeycloakTransaction transaction) -> {
                            doWork(em, transaction);
                        }
                );
            }

            logger.debug("Stop task: " + this);
        }

        protected abstract void doWork(EntityManager em, JpaKeycloakTransaction transaction);

        public void stop() {
            stopped.set(true);
        }

    }


    private static class CreatorTask extends AbstractTask {

        CreatorTask(JpaProvider jpaProvider) {
            super(jpaProvider);
        }

        @Override
        protected void doWork(EntityManager em, JpaKeycloakTransaction transaction) {
            int id = counter.incrementAndGet();
            Company company = new Company();
            company.setId(String.valueOf(id));
            company.setName("MyComp " + id);
            company.setAddress("Elm 123");

            int random = new Random().nextInt(5) + 1;
            Realm r = em.find(Realm.class, String.valueOf(random));
            company.setRealm(r);

            em.persist(company);
            if (id % 100 == 0) {
                logger.info("Created: " + id + " companies.");
            }
        }
    }


    private static class ReaderTask extends AbstractTask {

        ReaderTask(JpaProvider jpaProvider) {
            super(jpaProvider);
        }

        @Override
        protected void doWork(EntityManager em, JpaKeycloakTransaction transaction) {
            int randomId = new Random().nextInt(counter.get());
            Company company = em.find(Company.class, String.valueOf(randomId));
            if (randomId % 100 == 0 && company != null) {
                logger.info("Read: " + randomId + " " + company.getName());
            }
        }
    }



    static int getCompanyCount(EntityManager em) {
        String namedQuery = "findCount";


        Object count = em.createNamedQuery(namedQuery)
                .getSingleResult();
        return ((Number)count).intValue();
    }


}

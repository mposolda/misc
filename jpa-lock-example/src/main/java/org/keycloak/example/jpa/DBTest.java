package org.keycloak.example.jpa;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.ejb.AvailableSettings;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DBTest {

    private static final Logger logger = Logger.getLogger(DBTest.class);

    private static final int THREAD_COUNT = 10;
    private static final long SLEEP_MILLIS = 1000;

    public static void main(String[] args) throws Exception {
        new DBTest().run();
    }

    public void run() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(AvailableSettings.JDBC_URL, System.getProperty("keycloak.connectionsJpa.url", "jdbc:h2:mem:test;LOCK_TIMEOUT=30000"));
        properties.put(AvailableSettings.JDBC_DRIVER, System.getProperty("keycloak.connectionsJpa.driver", "org.h2.Driver"));
        properties.put(AvailableSettings.JDBC_USER, System.getProperty("keycloak.connectionsJpa.user", "sa"));
        properties.put(AvailableSettings.JDBC_PASSWORD, System.getProperty("keycloak.connectionsJpa.password", ""));

        properties.put("hibernate.show_sql", false);
        properties.put("hibernate.format_sql", true);

        properties.put("hibernate.hbm2ddl.auto", "update");

        logger.info("Properites: " + properties);

        // Will create table too among other things
        logger.info("Creating emf");
        final EntityManagerFactory emf = createEntityManagerFactory("keycloak-default-lock", properties, getClass().getClassLoader());
        logger.info("Created emf");

        // Insert object
        doInTransaction(emf, new Task() {

            public void run(EntityManager em) {
                DBLockEntity lock = em.find(DBLockEntity.class, "1");
                if (lock == null) {
                    DBLockEntity entity = new DBLockEntity();
                    entity.setId("1");
                    entity.setLocked("F");

                    logger.info("Persisting lock entity");
                    em.persist(entity);
                }
            }

        });


        // Test the lock
        List<Thread> threads = new LinkedList<Thread>();
        for (int i=0 ; i < THREAD_COUNT ; i++) {
            Thread t = new Thread() {

                @Override
                public void run() {
                    testLock(emf);
                }

            };
            threads.add(t);
        }

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        // Closing EntityManagerFactory
        emf.close();
    }


    public static EntityManagerFactory createEntityManagerFactory(String unitName, Map<String, Object> properties, ClassLoader classLoader) {
        PersistenceXmlParser parser = new PersistenceXmlParser(new ClassLoaderServiceImpl(classLoader), PersistenceUnitTransactionType.RESOURCE_LOCAL);
        List<ParsedPersistenceXmlDescriptor> persistenceUnits = parser.doResolve(properties);
        for (ParsedPersistenceXmlDescriptor persistenceUnit : persistenceUnits) {
            if (persistenceUnit.getName().equals(unitName)) {
                return Bootstrap.getEntityManagerFactoryBuilder(persistenceUnit, properties, classLoader).build();
            }
        }
        throw new RuntimeException("Persistence unit '" + unitName + "' not found");
    }


    private void doInTransaction(EntityManagerFactory emf, Task task) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            task.run(em);

            logger.info("Going to commit");
            em.getTransaction().commit();
            logger.info("Commited");
        } catch (Exception e) {
            logger.info("Going to rollback");
            em.getTransaction().rollback();
            logger.info("Rollbacked");
            logger.error(e);
        } finally {
            em.close();
        }
    }


    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void testLock(EntityManagerFactory emf) {
        doInTransaction(emf, new Task() {

            public void run(EntityManager em) {
                DBLockEntity lock = em.find(DBLockEntity.class, "1");
                if (lock == null) {
                    throw new RuntimeException("Not found lock object");
                }

                em.lock(lock, LockModeType.PESSIMISTIC_WRITE);
                logger.info("Locked successfully");
                sleep(SLEEP_MILLIS);
            }

        });
    }

    private interface Task {
        void run(EntityManager em);
    }
}

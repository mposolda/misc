package org.mposolda.jpamig.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.ejb.AvailableSettings;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JpaProvider {

    private static final Logger logger = Logger.getLogger(JpaProvider.class);

    private volatile EntityManagerFactory emf;

    private Map<String, String> config;

    public void init(Map<String, String> config) {
        this.config = config;
    }


    public EntityManager create() {
        lazyInit();

        EntityManager em = null;
//        if (!jtaEnabled) {
            logger.trace("enlisting EntityManager in JpaKeycloakTransaction");
            em = emf.createEntityManager();
//        } else {
//
//            em = emf.createEntityManager(SynchronizationType.SYNCHRONIZED);
//        }

        return em;
    }


    public void close() {
        if (emf != null) {
            emf.close();
            logger.info("Close EntityManagerFactory");
        }
    }

    private void lazyInit() {
        if (emf == null) {
            synchronized (this) {
                if (emf == null) {

                    logger.debug("Initializing JPA connections");

                    Map<String, Object> properties = new HashMap<String, Object>();

                    String unitName = "keycloak-default";


                    properties.put(AvailableSettings.JDBC_URL, config.get("url"));
                    properties.put(AvailableSettings.JDBC_DRIVER, config.get("driver"));

                    String user = config.get("user");
                    if (user != null) {
                        properties.put(AvailableSettings.JDBC_USER, user);
                    }
                    String password = config.get("password");
                    if (password != null) {
                        properties.put(AvailableSettings.JDBC_PASSWORD, password);
                    }


                    String schema = getSchema();
                    if (schema != null) {
                        properties.put(JpaUtils.HIBERNATE_DEFAULT_SCHEMA, schema);
                    }


                    boolean initializeEmpty = true;

                    // TODO: possibly change
                    properties.put("hibernate.show_sql", Boolean.getBoolean("showSql"));
                    properties.put("hibernate.format_sql", true);

                    Connection connection = getConnection();
                    try {
                        connection.setAutoCommit(false);

                        String driverDialect = detectDialect(connection);
                        if (driverDialect != null) {
                            properties.put("hibernate.dialect", driverDialect);
                        }

                        migration(initializeEmpty, schema, connection);

                        logger.info("Creating EntityManagerFactory");

                        emf = JpaUtils.createEntityManagerFactory(unitName, properties, getClass().getClassLoader(), false);
                        logger.info("EntityManagerFactory created");

                        connection.commit();

                    } catch (Exception e) {
                        try {
                            if (connection != null) {
                                logger.error("SENDING ROLLBACK TO THE DATABASE");
                                connection.rollback();
                            }
                        } catch (Exception e2) {
                            logger.error("Failed to rollback connection", e2);
                        }

                        throw new RuntimeException(e);
                    } finally {
                        // Close after creating EntityManagerFactory to prevent in-mem databases from closing
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (SQLException e) {
                                logger.warn("Can't close connection", e);
                            }
                        }
                    }

                }
            }
        }
    }



    protected String detectDialect(Connection connection) {
        String driverDialect = config.get("driverDialect");
        if (driverDialect != null && driverDialect.length() > 0) {
            return driverDialect;
        } else {
            try {
                String dbProductName = connection.getMetaData().getDatabaseProductName();
                String dbProductVersion = connection.getMetaData().getDatabaseProductVersion();

                // For MSSQL2014, we may need to fix the autodetected dialect by hibernate
                if (dbProductName.equals("Microsoft SQL Server")) {
                    String topVersionStr = dbProductVersion.split("\\.")[0];
                    boolean shouldSet2012Dialect = true;
                    try {
                        int topVersion = Integer.parseInt(topVersionStr);
                        if (topVersion < 12) {
                            shouldSet2012Dialect = false;
                        }
                    } catch (NumberFormatException nfe) {
                    }
                    if (shouldSet2012Dialect) {
                        String sql2012Dialect = "org.hibernate.dialect.SQLServer2012Dialect";
                        logger.debugf("Manually override hibernate dialect to %s", sql2012Dialect);
                        return sql2012Dialect;
                    }
                }
            } catch (SQLException e) {
                logger.warnf("Unable to detect hibernate dialect due database exception : %s", e.getMessage());
            }

            return null;
        }
    }


    public void migration(boolean initializeEmpty, String schema, Connection connection) {
        JpaUpdaterProvider updater = new JpaUpdaterProvider();

//        JpaUpdaterProvider.Status status = updater.validate(connection, schema);
//        if (status == JpaUpdaterProvider.Status.VALID) {
//            logger.debug("Database is up-to-date");
//        } else if (status == JpaUpdaterProvider.Status.EMPTY) {
//            update(connection, schema, updater);
//        } else {
            update(connection, schema, updater);
//        }
    }

    protected void update(Connection connection, String schema, JpaUpdaterProvider updater) {
//        DBLockProvider dbLock = new DBLockManager(session).getDBLock();
//        if (dbLock.hasLock()) {
            updater.update(connection, schema);
//        } else {
//            KeycloakModelUtils.runJobInTransaction(session.getKeycloakSessionFactory(), new KeycloakSessionTask() {
//                @Override
//                public void run(KeycloakSession lockSession) {
//                    DBLockManager dbLockManager = new DBLockManager(lockSession);
//                    DBLockProvider dbLock2 = dbLockManager.getDBLock();
//                    dbLock2.waitForLock();
//                    try {
//                        updater.update(connection, schema);
//                    } finally {
//                        dbLock2.releaseLock();
//                    }
//                }
//            });
//        }
    }


    private Connection getConnection() {
        try {
            Class.forName(config.get("driver"));
            return DriverManager.getConnection(config.get("url"), config.get("user"), config.get("password"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }


    private String getSchema() {
        return config.get("schema");
    }

}

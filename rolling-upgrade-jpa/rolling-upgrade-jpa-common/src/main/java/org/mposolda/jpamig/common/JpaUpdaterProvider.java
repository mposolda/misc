package org.mposolda.jpamig.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.executor.LoggingExecutor;
import liquibase.statement.core.CreateDatabaseChangeLogTableStatement;
import liquibase.util.StreamUtil;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JpaUpdaterProvider {

    private static final Logger logger = Logger.getLogger(JpaUpdaterProvider.class);

    /**
     * Status of database up-to-dateness
     */
    enum Status {
        /**
         * Database is valid and up to date
         */
        VALID,
        /**
         * No database exists.
         */
        EMPTY,
        /**
         * Database needs to be updated
         */
        OUTDATED
    }

    /**
     * Updates the Keycloak database
     * @param connection DB connection
     * @param defaultSchema DB connection
     */
    void update(Connection connection, String defaultSchema) {
        update(connection, null, defaultSchema);
    }

    private void update(Connection connection, File file, String defaultSchema) {
        logger.info("Starting database update");

        Writer exportWriter = null;
        try {
            // Run update with keycloak master changelog first
            Liquibase liquibase = getLiquibaseForKeycloakUpdate(connection, defaultSchema);
            if (file != null) {
                exportWriter = new FileWriter(file);
            }
            updateChangeSet(liquibase, liquibase.getChangeLogFile(), exportWriter);


        } catch (LiquibaseException | IOException e) {
            throw new RuntimeException("Failed to update database", e);
        } finally {
            if (exportWriter != null) {
                try {
                    exportWriter.close();
                } catch (IOException ioe) {
                    // ignore
                }
            }
        }
    }

    protected void updateChangeSet(Liquibase liquibase, String changelog, Writer exportWriter) throws LiquibaseException, IOException {
        List<ChangeSet> changeSets = getLiquibaseUnrunChangeSets(liquibase);
        if (!changeSets.isEmpty()) {
            List<RanChangeSet> ranChangeSets = liquibase.getDatabase().getRanChangeSetList();
            if (ranChangeSets.isEmpty()) {
                logger.infov("Initializing database schema. Using changelog {0}", changelog);
            } else {
                logger.infov("Updating database from {0} to {1}. Using changelog {2}", ranChangeSets.get(ranChangeSets.size() - 1).getId(), changeSets.get(changeSets.size() - 1).getId(), changelog);
            }

            if (exportWriter != null) {
                if (ranChangeSets.isEmpty()) {
                    outputChangeLogTableCreationScript(liquibase, exportWriter);
                }
                liquibase.update((Contexts) null, new LabelExpression(), exportWriter, false);
            } else {
                liquibase.update((Contexts) null);
            }

            logger.infov("Completed database update for changelog {0}", changelog);
        } else {
            logger.infov("Database is up to date for changelog {0}", changelog);
        }

        // Needs to restart liquibase services to clear ChangeLogHistoryServiceFactory.getInstance().
        // See https://issues.jboss.org/browse/KEYCLOAK-3769 for discussion relevant to why reset needs to be here
        //resetLiquibaseServices(liquibase);
    }

    private void outputChangeLogTableCreationScript(Liquibase liquibase, final Writer exportWriter) throws DatabaseException {
        Database database = liquibase.getDatabase();

        Executor oldTemplate = ExecutorService.getInstance().getExecutor(database);
        LoggingExecutor executor = new LoggingExecutor(ExecutorService.getInstance().getExecutor(database), exportWriter, database);
        ExecutorService.getInstance().setExecutor(database, executor);

        executor.comment("*********************************************************************");
        executor.comment("* Keycloak database creation script - apply this script to empty DB *");
        executor.comment("*********************************************************************" + StreamUtil.getLineSeparator());

        executor.execute(new CreateDatabaseChangeLogTableStatement());
        // DatabaseChangeLogLockTable is created before this code is executed and recreated if it does not exist automatically
        // in org.keycloak.connections.jpa.updater.liquibase.lock.CustomLockService.init() called indirectly from
        // KeycloakApplication constructor (search for waitForLock() call). Hence it is not included in the creation script.

        executor.comment("*********************************************************************" + StreamUtil.getLineSeparator());

        ExecutorService.getInstance().setExecutor(database, oldTemplate);
    }

//    @Override
//    public Status validate(Connection connection, String defaultSchema) {
//        logger.debug("Validating if database is updated");
//        ThreadLocalSessionContext.setCurrentSession(session);
//
//        try {
//            // Validate with keycloak master changelog first
//            Liquibase liquibase = getLiquibaseForKeycloakUpdate(connection, defaultSchema);
//
//            Status status = validateChangeSet(liquibase, liquibase.getChangeLogFile());
//            if (status != Status.VALID) {
//                return status;
//            }
//
//            // Validate each custom JpaEntityProvider
//            Set<JpaEntityProvider> jpaProviders = session.getAllProviders(JpaEntityProvider.class);
//            for (JpaEntityProvider jpaProvider : jpaProviders) {
//                String customChangelog = jpaProvider.getChangelogLocation();
//                if (customChangelog != null) {
//                    String factoryId = jpaProvider.getFactoryId();
//                    String changelogTableName = JpaUtils.getCustomChangelogTableName(factoryId);
//                    liquibase = getLiquibaseForCustomProviderUpdate(connection, defaultSchema, customChangelog, jpaProvider.getClass().getClassLoader(), changelogTableName);
//                    if (validateChangeSet(liquibase, liquibase.getChangeLogFile()) != Status.VALID) {
//                        return Status.OUTDATED;
//                    }
//                }
//            }
//        } catch (LiquibaseException e) {
//            throw new RuntimeException("Failed to validate database", e);
//        }
//
//        return Status.VALID;
//    }
//
//    protected Status validateChangeSet(Liquibase liquibase, String changelog) throws LiquibaseException {
//        final Status result;
//        List<ChangeSet> changeSets = getLiquibaseUnrunChangeSets(liquibase);
//
//        if (!changeSets.isEmpty()) {
//            if (changeSets.size() == liquibase.getDatabaseChangeLog().getChangeSets().size()) {
//                result = Status.EMPTY;
//            } else {
//                logger.debugf("Validation failed. Database is not up-to-date for changelog %s", changelog);
//                result = Status.OUTDATED;
//            }
//        } else {
//            logger.debugf("Validation passed. Database is up-to-date for changelog %s", changelog);
//            result = Status.VALID;
//        }
//
//        // Needs to restart liquibase services to clear ChangeLogHistoryServiceFactory.getInstance().
//        // See https://issues.jboss.org/browse/KEYCLOAK-3769 for discussion relevant to why reset needs to be here
//        resetLiquibaseServices(liquibase);
//
//        return result;
//    }

//    private void resetLiquibaseServices(Liquibase liquibase) {
//        Method resetServices = Reflections.findDeclaredMethod(Liquibase.class, "resetServices");
//        Reflections.invokeMethod(true, resetServices, liquibase);
//    }

    @SuppressWarnings("unchecked")
    private List<ChangeSet> getLiquibaseUnrunChangeSets(Liquibase liquibase) {
        // TODO tracked as: https://issues.jboss.org/browse/KEYCLOAK-3730
        // TODO: When https://liquibase.jira.com/browse/CORE-2919 is resolved, replace the following two lines with:
        // List<ChangeSet> changeSets = liquibase.listUnrunChangeSets((Contexts) null, new LabelExpression(), false);
        Method listUnrunChangeSets = Reflectionss.findDeclaredMethod(Liquibase.class, "listUnrunChangeSets", Contexts.class, LabelExpression.class, boolean.class);
        return Reflectionss.invokeMethod(true, listUnrunChangeSets, List.class, liquibase, (Contexts) null, new LabelExpression(), false);
    }

    private Liquibase getLiquibaseForKeycloakUpdate(Connection connection, String defaultSchema) throws LiquibaseException {
        return LiquibaseConnectionProvider.getInstance().getLiquibase(connection, defaultSchema);
    }

//    private Liquibase getLiquibaseForCustomProviderUpdate(Connection connection, String defaultSchema, String changelogLocation, ClassLoader classloader, String changelogTableName) throws LiquibaseException {
//        LiquibaseConnectionProvider liquibaseProvider = session.getProvider(LiquibaseConnectionProvider.class);
//        return liquibaseProvider.getLiquibaseForCustomUpdate(connection, defaultSchema, changelogLocation, classloader, changelogTableName);
//    }


    public static String getTable(String table, String defaultSchema) {
        return defaultSchema != null ? defaultSchema + "." + table : table;
    }
}

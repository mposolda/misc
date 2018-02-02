package org.mposolda.jpamig.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;
import org.hibernate.jpa.boot.spi.Bootstrap;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JpaUtils {

    public static final String HIBERNATE_DEFAULT_SCHEMA = "hibernate.default_schema";

    public static String getTableNameForNativeQuery(String tableName, EntityManager em) {
        String schema = (String) em.getEntityManagerFactory().getProperties().get(HIBERNATE_DEFAULT_SCHEMA);
        return (schema==null) ? tableName : schema + "." + tableName;
    }

    public static EntityManagerFactory createEntityManagerFactory(String unitName, Map<String, Object> properties, ClassLoader classLoader, boolean jta) {
        PersistenceUnitTransactionType txType = jta ? PersistenceUnitTransactionType.JTA : PersistenceUnitTransactionType.RESOURCE_LOCAL;
        PersistenceXmlParser parser = new PersistenceXmlParser(new ClassLoaderServiceImpl(classLoader), txType);
        List<ParsedPersistenceXmlDescriptor> persistenceUnits = parser.doResolve(properties);
        for (ParsedPersistenceXmlDescriptor persistenceUnit : persistenceUnits) {
            if (persistenceUnit.getName().equals(unitName)) {
//                List<Class<?>> providedEntities = getProvidedEntities(session);
//                for (Class<?> entityClass : providedEntities) {
//                    // Add all extra entity classes to the persistence unit.
//                    persistenceUnit.addClasses(entityClass.getName());
//                }
                // Now build the entity manager factory, supplying a proxy classloader, so Hibernate will be able
                // to find and load the extra provided entities. Set the provided classloader as parent classloader.
                persistenceUnit.setTransactionType(txType);
                return Bootstrap.getEntityManagerFactoryBuilder(persistenceUnit, properties, classLoader).build();
            }
        }
        throw new RuntimeException("Persistence unit '" + unitName + "' not found");
    }



    public static void wrapTransaction(JpaProvider jpaProvider, TransactionTask task) {
        EntityManager em = jpaProvider.create();
        JpaKeycloakTransaction transaction = new JpaKeycloakTransaction(em);

        transaction.begin();

        try {
            task.run(em, transaction);

            if (transaction.getRollbackOnly()) {
                transaction.rollback();
            } else {
                transaction.commit();
            }
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }



    public interface TransactionTask {

        void run(EntityManager em, JpaKeycloakTransaction transaction);
    }




}

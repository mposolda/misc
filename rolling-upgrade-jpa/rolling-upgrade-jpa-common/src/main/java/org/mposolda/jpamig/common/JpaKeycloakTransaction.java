package org.mposolda.jpamig.common;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JpaKeycloakTransaction {


    private static final Logger logger = Logger.getLogger(JpaKeycloakTransaction.class);

    protected EntityManager em;

    public JpaKeycloakTransaction(EntityManager em) {
        this.em = em;
    }


    public void begin() {
        logger.debug("Begin transaction");
        em.getTransaction().begin();
    }


    public void commit() {
//        try {
            logger.debug("Committing transaction");
            em.getTransaction().commit();
//        } catch (PersistenceException e) {
//            throw PersistenceExceptionConverter.convert(e.getCause() != null ? e.getCause() : e);
//        }
    }


    public void rollback() {
        logger.info("Rollback transaction");
        em.getTransaction().rollback();
    }


    public void setRollbackOnly() {
        em.getTransaction().setRollbackOnly();
    }


    public boolean getRollbackOnly() {
        return  em.getTransaction().getRollbackOnly();
    }


    public boolean isActive() {
        return em.getTransaction().isActive();
    }
}

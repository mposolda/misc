package org.mposolda.jpamig.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
public class BootstrapTestV2 {

    private static final Logger logger = Logger.getLogger(BootstrapTestV2.class);

    public static void main(String[] args) throws Exception {

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
                throw new RuntimeException("REalms not found!!!");

            }

            Company c = em.find(Company.class, "1");
            if (c == null) {
                throw new RuntimeException("Companies not found!!!");
            } else {
                logger.info("Company name=" + c.getName() + ", address: " + c.getAddress() +
                        ", realm: " + c.getRealm().getName() + ", foo: " + c.getFoo());
            }

            logger.info("Realms and companies are here");
        });

        jpaProvider.close();
    }
}

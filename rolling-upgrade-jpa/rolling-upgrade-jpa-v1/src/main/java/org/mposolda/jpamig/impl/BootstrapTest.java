package org.mposolda.jpamig.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.mposolda.jpamig.Company;
import org.mposolda.jpamig.common.JpaKeycloakTransaction;
import org.mposolda.jpamig.common.JpaProvider;
import org.mposolda.jpamig.common.JpaUtils;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class BootstrapTest {

    public static void main(String[] args) {
        Map<String, String> cfg = new HashMap<>();
        cfg.put("url", System.getProperty("keycloak.connectionsJpa.url"));
        cfg.put("driver", System.getProperty("keycloak.connectionsJpa.driver"));
        cfg.put("user", System.getProperty("keycloak.connectionsJpa.user"));
        cfg.put("password", System.getProperty("keycloak.connectionsJpa.password"));

        JpaProvider jpaProvider = new JpaProvider();
        jpaProvider.init(cfg);

        JpaUtils.wrapTransaction(jpaProvider, (EntityManager em, JpaKeycloakTransaction transaction) -> {


            Company company = em.find(Company.class, "1");
            if (company != null) {
                System.out.println("Found company: " + company.getName() + " " + company.getAddress());
            } else {
                company = new Company();
                company.setId("1");
                company.setName("MyComp");
                company.setAddress("Elm 123");

                em.persist(company);
                System.out.println("Created company: " + company.getName() + " " + company.getAddress());
            }
        });

        jpaProvider.close();
    }


}

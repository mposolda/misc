package org.jboss.resteasy.sample.keycloak;

import org.keycloak.services.resources.KeycloakApplication;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class KeycloakNettyApplication extends KeycloakApplication {

    public KeycloakNettyApplication() {
        super(new MockServletContext());
    }
}

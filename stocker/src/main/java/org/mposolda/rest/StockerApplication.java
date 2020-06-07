package org.mposolda.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StockerApplication extends Application {

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> classes = new HashSet<>();

    public StockerApplication() {
        try {
//            Resteasy.pushDefaultContextObject(KeycloakApplication.class, this);
//            Resteasy.pushContext(KeycloakApplication.class, this); // for injection

            singletons.add(new RobotsResource());

            // TODO: Check if this is needed
            //singletons.add(new RealmsResource());

            singletons.add(new AdminRoot());

            classes.add(ThemeResource.class);

            // Not sure if this is needed...
            // classes.add(KeycloakErrorHandler.class);

            singletons.add(new ObjectMapperResolver(Boolean.parseBoolean(System.getProperty("keycloak.jsonPrettyPrint", "false"))));

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}

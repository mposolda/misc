package org.mposolda;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SimpleJaxRsApp extends Application {

    private final  Set<Object> singletons = new HashSet<Object>();

    public SimpleJaxRsApp() {
        this.singletons.add(new SimpleResource());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}

package org.jboss.resteasy.sample;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

/**
 * just to test which constructor has precedence
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SampleServletApplication extends SampleApplication {

    public SampleServletApplication() {
        super();
        System.out.println("SampleServletApplication() called");
    }

    public SampleServletApplication(@Context ServletContext servletContext) {
        super();
        System.out.println("SampleServletApplication(servletContext) called");
    }
}

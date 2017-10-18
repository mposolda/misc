package org.jboss.sample;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ServletInitializer implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.err.println("HEELOO!");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.err.println("HEELOO!");
    }
}

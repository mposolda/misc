package org.jboss;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JMXTest {

    public static void main(String[] args) throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        server.getAttribute(new ObjectName("java.util.logging:type=Logging"), "LoggerNames");

        System.out.println("Hello");
    }


}

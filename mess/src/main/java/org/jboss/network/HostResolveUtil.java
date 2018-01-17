package org.jboss.network;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class HostResolveUtil {

    public static String getHostname() {
        return System.getProperty("my.host", "localhost");
    }

    public static int getPort() {
        return Integer.parseInt(System.getProperty("my.port", "4445"));
    }
}

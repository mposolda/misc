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

    public static String getMulticastHost() {
        return System.getProperty("multicast.host", "224.0.0.10");
    }

    public static int getMulticastPort() {
        return Integer.parseInt(System.getProperty("multicast.port", "4446"));
    }
}

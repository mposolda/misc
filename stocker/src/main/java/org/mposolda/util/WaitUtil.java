package org.mposolda.util;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class WaitUtil {

    // Interval in milliseconds to wait among Finhub calls
    public static final long INTERVAL = 1000;

    public static void pause(long intervalMillis) {
        try {
            Thread.sleep(intervalMillis);
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }
}

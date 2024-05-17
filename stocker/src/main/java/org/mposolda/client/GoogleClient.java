package org.mposolda.client;

import java.io.Closeable;

import org.jboss.logging.Logger;

/**
 * Downloading info from google
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class GoogleClient implements Closeable {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    public double getSp500() {
        // TODO:mposolda
        double val = 4737.57;
        log.infof("S&P 500 value from google: %,.2f", val);
        return val;
    }

    @Override
    public void close() {
        // TODO:mposolda
    }
}

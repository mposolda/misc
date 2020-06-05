package org.mposolda;

import org.mposolda.cli.StockerCli;
import org.mposolda.services.Services;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StockerRunner {

    public static void main(String[] args) throws Exception {
        Services services = Services.instance();
        services.start();

        StockerCli cli = new StockerCli(services);
        cli.start();
    }
}

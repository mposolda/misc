package org.mposolda;


import org.jboss.logging.Logger;
import org.mposolda.cli.StockerCli;
import org.mposolda.services.Services;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StockerRunner {

    private static final Logger log = Logger.getLogger(StockerRunner.class);

    public static void main(String[] args) throws Throwable {
        long start = System.currentTimeMillis();

        // Start services
        Services services = Services.instance();
        services.start();

        // Start REST server
        StockerServer server = new StockerServer();
        server.start(start);

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                log.info("Closing services");
                services.close();
                log.info("Stoping the undertow server");
                server.stop();
            }

        });

        // Start CLI
        StockerCli cli = new StockerCli(services);
        cli.start();
    }
}

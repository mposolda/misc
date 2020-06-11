package org.mposolda;

import io.undertow.Undertow;
import io.undertow.servlet.api.DefaultServletConfig;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.mposolda.rest.StockerApplication;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StockerServer {

    protected static final Logger logger = Logger.getLogger(StockerServer.class);

    public static final String HOST = "localhost";

    public static final int PORT = 8085;

    public static final String CONTEXT_ROOT = "/stocker";

    public static final int WORKER_THREADS = Math.max(Runtime.getRuntime().availableProcessors(), 2) * 8;

    public static final String DEFAULT_LOCALE = "cs";

    private UndertowJaxrsServer server;

    public void start(long startTimeMs) throws Throwable {
        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setApplicationClass(StockerApplication.class.getName());

        Undertow.Builder builder = Undertow.builder()
                .addHttpListener(PORT, HOST)
                .setWorkerThreads(WORKER_THREADS)
                .setIoThreads(WORKER_THREADS / 8);

//        if (config.getPortHttps() != -1) {
//            builder = builder
//                    .addHttpsListener(config.getPortHttps(), config.getHost(), createSSLContext())
//                    .setSocketOption(Options.SSL_CLIENT_AUTH_MODE, SslClientAuthMode.REQUESTED);
//        }

        server = new UndertowJaxrsServer();
        try {
            server.start(builder);

            DeploymentInfo di = server.undertowDeployment(deployment, "");
            di.setClassLoader(getClass().getClassLoader());
            di.setContextPath(CONTEXT_ROOT);
            di.setDeploymentName("Stocker");
            di.setDefaultEncoding("UTF-8");

            di.setDefaultServletConfig(new DefaultServletConfig(true));

            // Note that the ResteasyServlet is configured via server.undertowDeployment(...);
            // KEYCLOAK-14178
            deployment.setProperty(ResteasyContextParameters.RESTEASY_DISABLE_HTML_SANITIZER, true);

//            FilterInfo filter = Servlets.filter("SessionFilter", TestKeycloakSessionServletFilter.class);
//            filter.setAsyncSupported(true);
//
//            di.addFilter(filter);
//            di.addFilterUrlMapping("SessionFilter", "/*", DispatcherType.REQUEST);

            server.deploy(di);

//            sessionFactory = ((KeycloakApplication) deployment.getApplication()).getSessionFactory();
//
//            setupDevConfig();

//            if (config.getResourcesHome() != null) {
//                info("Loading resources from " + config.getResourcesHome());
//            }

            logger.info("Started Stocker (http://" + HOST + ":" + PORT + CONTEXT_ROOT
                    + " in "
                    + (System.currentTimeMillis() - startTimeMs) + " ms\n");
        } catch (RuntimeException e) {
            server.stop();
            throw e;
        }
    }

    public void stop() {
        server.stop();
        logger.info("Stopped Keycloak");
    }
}

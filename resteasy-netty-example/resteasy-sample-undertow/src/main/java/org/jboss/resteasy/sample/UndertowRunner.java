package org.jboss.resteasy.sample;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;

import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DefaultServletConfig;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;

import static io.undertow.servlet.Servlets.servlet;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class UndertowRunner {

    private Undertow server;
    private PathHandler root;
    private ServletContainer container;

    public static void main(String[] args) {
        new UndertowRunner().run();
    }

    public void run() {

        root = new PathHandler();
        container = ServletContainer.Factory.newInstance();

        Undertow.Builder builder = Undertow.builder().addListener(8383, "localhost");
        server = builder.setHandler(root).build();
        server.start();

        deployApp();

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                undeployApp();
                server.stop();
            }
        });

    }

    public void deployApp() {
        ResteasyDeployment deployment = new ResteasyDeployment();
        // deployment.setApplicationClass(SampleApplication.class.getName());
        deployment.setApplicationClass(SampleServletApplication.class.getName());

        DeploymentInfo deploymentInfo = new DeploymentInfo();
        deploymentInfo.setClassLoader(getClass().getClassLoader());
        deploymentInfo.setContextPath("/app" );
        deploymentInfo.setDeploymentName("Sample");

        deploymentInfo.setDefaultServletConfig(new DefaultServletConfig(true));

        ServletInfo resteasyServlet = servlet("ResteasyServlet", HttpServlet30Dispatcher.class)
                .setAsyncSupported(true)
                .setLoadOnStartup(1)
                .addMapping("/rest/*");
        resteasyServlet.addInitParam("resteasy.servlet.mapping.prefix", "/rest" );

        deploymentInfo.addServletContextAttribute(ResteasyDeployment.class.getName(), deployment);
        deploymentInfo.addServlet(resteasyServlet);

        FilterInfo filter = Servlets.filter("InjectorFilter", InjectorFilter.class);
        deploymentInfo.addFilter(filter);
        deploymentInfo.addFilterUrlMapping("InjectorFilter", "/rest/*", DispatcherType.REQUEST);

        DeploymentManager manager = container.addDeployment(deploymentInfo);
        manager.deploy();
        try {
            root.addPath(deploymentInfo.getContextPath(), manager.start());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    public void undeployApp() {
        DeploymentManager deployment = container.getDeployment("Sample");
        try {
            deployment.stop();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }


}

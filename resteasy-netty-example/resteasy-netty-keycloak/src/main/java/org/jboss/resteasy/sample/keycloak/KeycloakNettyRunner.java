package org.jboss.resteasy.sample.keycloak;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;
import org.jboss.resteasy.sample.NettyRunner;
import org.jboss.resteasy.sample.ServicesInjector;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resources.KeycloakApplication;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class KeycloakNettyRunner extends NettyRunner {

    public static final String KEYCLOAK_PATH = "/auth/rest";

    @Override
    protected String getRootPath() {
        return KEYCLOAK_PATH;
    }

    @Override
    protected void initChannel(EventLoopGroup eventExecutor, RequestDispatcher dispatcher, String rootPath, ResteasyDeployment deployment, NioSocketChannel ch) throws Exception {
        super.initChannel(eventExecutor, dispatcher, rootPath, deployment, ch);
        KeycloakApplication application = (KeycloakApplication)deployment.getApplication();
        KeycloakSessionFactory factory = application.getFactory();

        ch.pipeline().replace(ServicesInjector.class, "transaction-handler", new KeycloakTransactionHandler(factory));
    }

    @Override
    protected ResteasyDeployment createDeployment() {
        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setApplicationClass(KeycloakNettyApplication.class.getName());
        deployment.setSecurityEnabled(true);
        return deployment;
    }

    public static void main(String[] args) throws Exception {
        new KeycloakNettyRunner().run();
    }
}

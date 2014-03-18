package org.jboss.resteasy.sample.keycloak;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jboss.resteasy.plugins.server.netty.NettyHttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.KeycloakTransaction;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class KeycloakTransactionHandler extends SimpleChannelInboundHandler<NettyHttpRequest> {

    private final KeycloakSessionFactory factory;

    public KeycloakTransactionHandler(KeycloakSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyHttpRequest msg) throws Exception {
        KeycloakSession session = factory.createSession();
        ResteasyProviderFactory.pushContext(KeycloakSession.class, session);
        KeycloakTransaction tx = session.getTransaction();
        ResteasyProviderFactory.pushContext(KeycloakTransaction.class, tx);
        tx.begin();
        try {
            ctx.fireChannelRead(msg);
            if (tx.isActive()) {
                if (tx.getRollbackOnly()) tx.rollback();
                else tx.commit();
            }
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            ex.printStackTrace();
            throw ex;
        } finally {
            session.close();
            ResteasyProviderFactory.clearContextData();
        }
    }
}

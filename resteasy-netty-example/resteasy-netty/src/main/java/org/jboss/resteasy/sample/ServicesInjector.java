package org.jboss.resteasy.sample;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jboss.resteasy.plugins.server.netty.NettyHttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Version of InjectorFilter for netty
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ServicesInjector extends SimpleChannelInboundHandler<NettyHttpRequest> {

    private HelloService helloService = new HelloService();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyHttpRequest msg) throws Exception {
        ResteasyProviderFactory.pushContext(HelloService.class, helloService);
        try {
            ctx.fireChannelRead(msg);
        } finally {
            ResteasyProviderFactory.clearContextData();
        }
    }
}

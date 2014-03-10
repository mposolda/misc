package org.jboss.resteasy.sample;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.Future;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class NettyRunner {

    public static void main(String[] args) throws Exception {
        new NettyRunner().run();
    }

    public void run() throws Exception {
        final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .channel(NioServerSocketChannel.class)
                .group(eventLoopGroup)
                .localAddress(new InetSocketAddress( "localhost", 8383 ))
                        //.handler( new DebugHandler( "server-handler" ) )
                .childHandler(createChildHandler());
        ChannelFuture future = serverBootstrap.bind();
        future.sync();

        System.out.println("NETTY BOTSTRAP FINISHED");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                Future<?> future = eventLoopGroup.shutdownGracefully();
                try {
                    future.sync();
                } catch (Exception e) {}
            }
        });
    }

    protected ChannelHandler createChildHandler() {
        return new ChannelInitializer<NioSocketChannel>() {
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpRequestDecoder());
                pipeline.addLast(new HttpResponseEncoder());

                pipeline.addLast(new NettySampleHandler());
            }
        };
    }
}

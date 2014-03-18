package org.jboss.resteasy.sample;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.Future;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;
import org.jboss.resteasy.plugins.server.netty.RequestHandler;
import org.jboss.resteasy.plugins.server.netty.RestEasyHttpRequestDecoder;
import org.jboss.resteasy.plugins.server.netty.RestEasyHttpResponseEncoder;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * Find resteasy application under path: "http://localhost:8383/rest-app/a/someParam/some"
 * The url different than /rest-app will be served by NettySampleHandler
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class NettyRunner {

    public static final String REST_PATH = "/rest-app";

    private int maxRequestSize = 1024 * 1024 * 10;
    private int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
    private int executorThreadCount = 16;
    private int backlog = 128;

    public static void main(String[] args) throws Exception {
        new NettyRunner().run();
    }

    public void run() throws Exception {
        final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(ioWorkerCount);
        final EventLoopGroup eventExecutor = new NioEventLoopGroup(executorThreadCount);

        ResteasyDeployment deployment = createDeployment();
        deployment.start();

        // Special dispatcher needed just because RequestHandler is eating exceptions
        RequestDispatcher dispatcher = new SendErrorRequestDispatcher((SynchronousDispatcher)deployment.getDispatcher(), deployment.getProviderFactory(), null);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .channel(NioServerSocketChannel.class)
                .group(eventLoopGroup)
                .localAddress(new InetSocketAddress("localhost", 8383))
                        //.handler( new DebugHandler( "server-handler" ) )
                .childHandler(createChildHandler(eventExecutor, dispatcher, getRootPath(), deployment))
                .option(ChannelOption.SO_BACKLOG, backlog)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = serverBootstrap.bind();
        future.sync();

        System.out.println("NETTY BOOTSTRAP FINISHED");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                Future<?> future = eventLoopGroup.shutdownGracefully();
                Future<?> future2 = eventExecutor.shutdownGracefully();
                try {
                    future.sync();
                    future2.sync();
                    System.out.println("Both event loop groups shutdowned gracefully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected ChannelHandler createChildHandler(final EventLoopGroup eventExecutor, final RequestDispatcher dispatcher, final String rootPath, final ResteasyDeployment deployment) {

        return new ChannelInitializer<NioSocketChannel>() {

            protected void initChannel(NioSocketChannel ch) throws Exception {
                NettyRunner.this.initChannel(eventExecutor, dispatcher, rootPath, deployment, ch);
            }
        };
    }

    protected ResteasyDeployment createDeployment() {
        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setApplicationClass(SampleApplication.class.getName());
        deployment.setSecurityEnabled(true);
        return deployment;
    }

    protected void initChannel(final EventLoopGroup eventExecutor, final RequestDispatcher dispatcher, final String rootPath, ResteasyDeployment deployment, NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpObjectAggregator(maxRequestSize));
        pipeline.addLast(new HttpResponseEncoder());

        // My simple handler
        pipeline.addLast(new NettySampleHandler(rootPath));

        // Resteasy decoder/encoder
        ch.pipeline().addLast(new RestEasyHttpRequestDecoder(dispatcher.getDispatcher(), rootPath, RestEasyHttpRequestDecoder.Protocol.HTTP));
        ch.pipeline().addLast(new RestEasyHttpResponseEncoder(dispatcher));

        // ServicesInjector, which push some objects
        ch.pipeline().addLast(eventExecutor, new ServicesInjector());

        // Process resteasy request now
        ch.pipeline().addLast(eventExecutor, new RequestHandler(dispatcher));
    }

    protected String getRootPath() {
        return REST_PATH;
    }
}

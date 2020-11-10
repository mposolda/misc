package org.mposolda;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.infinispan.client.hotrod.ProtocolVersion;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.ExhaustedAction;
import org.infinispan.client.hotrod.configuration.SaslQop;
import org.infinispan.executors.DefaultExecutorFactory;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SimpleRemoteCacheProvider {

    public RemoteCache getRemoteCache(String cacheName) {
//        Map<String, String> config = new HashMap<>();
//        config.put("com.sun.security.sasl.digest.utf8", "true");

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder
                .addServer()
                    .host("127.0.0.1")
                    .port(11222)
                .forceReturnValues(false)
                .security()
                    .authentication()
                        .enable()
                        .realm("default")
                        .serverName("infinispan")
                        .username("myuser")
                        .password("qwer1234!")
                        .saslMechanism("DIGEST-MD5")
                        //.saslProperties(config)
                        //.saslQop(SaslQop.AUTH)
                .version(ProtocolVersion.PROTOCOL_VERSION_29)
                .connectionPool()
                    .maxActive(20)
                    .exhaustedAction(ExhaustedAction.CREATE_NEW);

        return new RemoteCacheManager(builder.build()).getCache(cacheName);
    }



    // Do the same as RemoteStore.buildRemoteConfiguration in Infinispan 9.2.1
//    @Override
//    public RemoteCache getRemoteCache() {
//        ConfigurationBuilder builder = new ConfigurationBuilder();
//        builder
//                .addServer()
//                    .host("127.0.0.1")
//                    .port(11222);
//
//        builder.classLoader(RemoteCache.class.getClassLoader())
//                .balancingStrategy("org.infinispan.client.hotrod.impl.transport.tcp.RoundRobinBalancingStrategy")
//                .connectionPool()
//                    .exhaustedAction(ExhaustedAction.CREATE_NEW)
//                .maxActive(20)
//                .maxIdle(-1)
//                .maxTotal(-1)
//                .minIdle(-1)
//                .minEvictableIdleTime(1800000)
//                .testWhileIdle(true)
//                .timeBetweenEvictionRuns(120000)
//                .connectionTimeout(60000)
//                .forceReturnValues(false)
//                .keySizeEstimate(64)
//                //.marshaller(marshaller)
//                .asyncExecutorFactory().factoryClass(DefaultExecutorFactory.class)
//                .socketTimeout(60000)
//                .tcpNoDelay(true)
//                .valueSizeEstimate(512);
//
//        builder.protocolVersion("2.7");
//        builder.withProperties (new Properties());
//
//        return new RemoteCacheManager(builder.build()).getCache("default");
//    }
}

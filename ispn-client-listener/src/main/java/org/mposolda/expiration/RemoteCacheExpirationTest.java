package org.mposolda.expiration;

import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.ProtocolVersion;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.container.versioning.NumericVersion;
import org.infinispan.metadata.EmbeddedMetadata;
import org.infinispan.metadata.Metadata;
import org.infinispan.metadata.impl.InternalMetadataImpl;
import org.infinispan.util.TimeService;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RemoteCacheExpirationTest {

    public static void main(String[] args) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT);
        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());

        RemoteCache<String, String> remoteCache = cacheManager.getCache("trans");

        remoteCache.put("key1", "value1");
        // This val1 item is fine and it's metadata are not expired
        MetadataValue val1 = remoteCache.getWithMetadata("key1");

        remoteCache.put("key2", "value2", 1000, TimeUnit.SECONDS, 100, TimeUnit.SECONDS);

        // ERROR HERE: val2 metadata has "created" and "lastUsed" set to 0
        MetadataValue val2 = remoteCache.getWithMetadata("key2");
        System.out.println("val2.created: " + val2.getCreated() + ", val2.lastUsed: " + val2.getLastUsed());

        long currentTimeMs = System.currentTimeMillis();

        boolean expired1 = isExpired(val1, currentTimeMs);
        boolean expired2 = isExpired(val2, currentTimeMs);

        System.out.println("expired1: " + expired1 + ", expired2: " + expired2);

        if (expired2) {
            System.err.println("ERROR: val2 is expired!");
        }

        cacheManager.stop();
    }


    /**
     * Convert RemoteCache metadata to InternalMetadata and check expiration
     * (same check used by org.infinispan.persistence.PersistenceUtil#loadAndCheckExpiration
     */
    private static boolean isExpired(MetadataValue value , long currentTimeMs) {
        Metadata metadata = new EmbeddedMetadata.Builder()
                .version(new NumericVersion(value.getVersion()))
                .lifespan(value.getLifespan(), TimeUnit.SECONDS)
                .maxIdle(value.getMaxIdle(), TimeUnit.SECONDS).build();
        long created = value.getCreated();
        long lastUsed = value.getLastUsed();

        InternalMetadataImpl internalMetadata = new InternalMetadataImpl(metadata, created, lastUsed);
        boolean expired = internalMetadata.isExpired(currentTimeMs);
        return expired;
    }
}

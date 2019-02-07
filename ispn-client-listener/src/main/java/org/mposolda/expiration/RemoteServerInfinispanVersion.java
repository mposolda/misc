package org.mposolda.expiration;

import java.util.HashMap;
import java.util.Map;

import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.logging.Logger;

/**
 * Singleton utility class to retrieve the Infinispan version of remote JDG server
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RemoteServerInfinispanVersion {

    protected static final Logger log = Logger.getLogger(RemoteServerInfinispanVersion.class);


    private static final String REMOTE_SERVER_INFINISPAN_VERSION =
            "function getIspnVersion() {" +
                    "  return cache.getVersion();\n" +
                    "};\n" +
                    "\n" +
                    "getIspnVersion();";


    private static String version;


    public static String getVersion(RemoteCache remoteCache) {
        if (version == null) {
            version = loadVersion(remoteCache);
        }
        return version;
    }


    private static String loadVersion(RemoteCache remoteCache) {
        // TODO:mposolda
        RemoteCache<String, String> scriptCache = remoteCache.getRemoteCacheManager().getCache("___script_cache");

        if (!scriptCache.containsKey("get-version.js")) {
            // TODO:
            log.debugf("Adding script get-sessions.js to remote cache: %s", "___script_cache");

            scriptCache.put("get-version.js",
                    "// mode=local,language=javascript\n" +
                            REMOTE_SERVER_INFINISPAN_VERSION);
        }

        Map<String, Integer> remoteParams = new HashMap<>();
        Object version = remoteCache.execute("get-version.js", remoteParams);
        log.infof("Version of remote infinispan server: %s", version);
        return version.toString();
    }
}

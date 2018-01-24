package org.mposolda.ispn.v2;

import java.util.Map;

import org.infinispan.Cache;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ListSessionsCommand<K, V> extends AbstractCommand<K, V> {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    protected void doRunCommand(Cache<K, V> cache) {
        int size = cache.size();
        log.infof("Cache %s, size: %d", cache.getName(), size);

        if (size > 50) {
            log.info("Skip printing cache records due to big size");
        } else {
            for (Map.Entry<K, V> entry : cache.entrySet()) {
                log.infof("%s=%s", entry.getKey(), entry.getValue());
            }
        }
    }
}

package org.mposolda.ispn.v2;

import java.util.Map;

import org.infinispan.Cache;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class GetSessionCommand<K, V> extends AbstractCommand<K, V> {

    @Override
    public String getName() {
        return "get";
    }

    @Override
    protected void doRunCommand(Cache<K, V> cache) {
        String id = getArg(0);
        V session = cache.get(id);
        log.info("Retrieved session: " + session);
    }

    @Override
    public String printUsage() {
        return super.printUsage() + " <id>";
    }
}

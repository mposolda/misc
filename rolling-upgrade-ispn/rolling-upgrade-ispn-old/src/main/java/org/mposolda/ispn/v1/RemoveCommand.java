package org.mposolda.ispn.v1;

import org.infinispan.Cache;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RemoveCommand<V> extends AbstractCommand<String, V> {

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    protected void doRunCommand(Cache<String, V> cache) {
        String id = getArg(0);

        cache.remove(id);
        log.infof("Removed: %s", id);
    }

    @Override
    public String printUsage() {
        return super.printUsage() + " <id>";
    }
}

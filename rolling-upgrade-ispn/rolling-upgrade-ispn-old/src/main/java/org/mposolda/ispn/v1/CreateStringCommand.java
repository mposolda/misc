package org.mposolda.ispn.v1;


import org.infinispan.Cache;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CreateStringCommand extends AbstractCommand<String, String> {

    @Override
    public String getName() {
        return "createString";
    }

    @Override
    protected void doRunCommand(Cache<String, String> cache) {
        String id = getArg(0);

        cache.put(id, "val-" + id);
        log.infof("Created: %s=%s", id, "val-" + id);
    }

    @Override
    public String printUsage() {
        return super.printUsage() + " <id>";
    }
}

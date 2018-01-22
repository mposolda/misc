package org.mposolda.ispn.v1;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */

import java.util.List;

import org.infinispan.Cache;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public abstract class AbstractCommand<K, V> {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    protected TestsuiteCLI<K, V> cli;
    private List<String> args;
    private Cache<K, V> cache;

    public void injectProperties(List<String> args, TestsuiteCLI<K, V> cli, Cache<K, V> cache) {
        this.cli = cli;
        this.args = args;
        this.cache = cache;
    }

    public void runCommand() {
        try {
             doRunCommand(cache);
        } catch (HandledException handled) {
            // Fine to ignore. Was handled already
        } catch (RuntimeException e) {
            log.error("Error occured during command. ", e);
        }
    }

    public abstract String getName();
    protected abstract void doRunCommand(Cache<K, V> cache);

    protected String getArg(int index) {
        try {
            return args.get(index);
        } catch (IndexOutOfBoundsException ex) {
            log.errorf("Usage: %s", printUsage());
            throw new HandledException();
        }
    }

    protected Integer getIntArg(int index) {
        String str = getArg(index);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nex) {
            log.errorf("Usage: %s", printUsage());
            throw new HandledException();
        }
    }

    public String printUsage() {
        return getName();
    }

    public static class HandledException extends RuntimeException {
    }

}

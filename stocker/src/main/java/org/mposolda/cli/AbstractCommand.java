package org.mposolda.cli;

import java.io.IOException;
import java.util.List;

import org.jboss.logging.Logger;
import org.mposolda.cli.StockerCli;
import org.mposolda.client.FinnhubHttpClient;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public abstract class AbstractCommand {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    protected List<String> args;

    protected FinnhubHttpClient finhubClient;

    public void injectProperties(List<String> args, StockerCli cli, FinnhubHttpClient finhubClient) {
        this.args = args;
        this.finhubClient = finhubClient;
    }

    public void runCommand() {
        try {
            doRunCommand();
        } catch (HandledException handled) {
            // Fine to ignore. Was handled already
        } catch (IOException e) {
            log.error("Error occured during command. ", e);
        } catch (RuntimeException e) {
            log.error("Error occured during command. ", e);
        }
    }

    public abstract String getName();
    protected abstract void doRunCommand() throws IOException;

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

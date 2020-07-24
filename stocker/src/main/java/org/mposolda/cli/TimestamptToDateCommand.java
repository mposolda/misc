package org.mposolda.cli;

import java.io.IOException;
import java.util.Date;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class TimestamptToDateCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "timestampToDate";
    }

    @Override
    protected void doRunCommand() throws IOException {
        long timestamp = getLongArg(0);

        log.info("Date: " + new Date(timestamp * 1000));
    }

    public static long getDate(int day, int month, int year) {
        Date d = new Date(year - 1900, month - 1, day);
        return d.getTime() / 1000;
    }

    @Override
    public String printUsage() {
        return getName() + " <unix-timestamp>";
    }
}

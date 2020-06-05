package org.mposolda.cli;

import java.io.IOException;
import java.util.Date;

import org.mposolda.cli.AbstractCommand;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DateConvertCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "date";
    }

    @Override
    protected void doRunCommand() throws IOException {
        int day = getIntArg(0);
        int month = getIntArg(1);
        int year = getIntArg(2);

        log.info("Date: " + new Date(year - 1900, month - 1, day) + " : " + getDate(day, month, year));
    }

    public static long getDate(int day, int month, int year) {
        Date d = new Date(year - 1900, month - 1, day);
        return d.getTime() / 1000;
    }

    @Override
    public String printUsage() {
        return getName() + " <day 1-31> <month 1-12> <year>";
    }
}

package org.mposolda.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DateUtil {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Returns conversion from date like "2020-10-20" to milliseconds.
     */
    public static long dateToNumber(String dateStr) {
        try {
            Date date = dateFormat.parse(dateStr);
            return date.getTime();
        } catch (ParseException pe) {
            throw new IllegalArgumentException("Exception when parsing " + dateStr, pe);
        }
    }

    /**
     * Returns conversion from date like "2020-10-20" to seconds.
     */
    public static long dateToNumberSeconds(String dateStr) {
        return dateToNumber(dateStr) / 1000;
    }

    public static String numberInSecondsToDate(long timestamp) {
        Date d = new Date(timestamp * 1000);
        return dateFormat.format(d);
    }

    /**
     * @return current unix timestamp (Count of seconds since 1.1.1970)
     */
    public static long getCurrentTimestamp() {
        return new Date().getTime() / 1000;
    }

    /**
     * @param dateStr dateStr like "2023-10-11"
     * @return the year like "2023" from the dateStr above
     */
    public static int getYearFromDate(String dateStr) {
        return Integer.parseInt(dateStr.substring(0, dateStr.indexOf("-")));
    }
}

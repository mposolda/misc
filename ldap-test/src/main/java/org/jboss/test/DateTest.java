package org.jboss.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DateTest {

    public static void main(String[] args) {
        System.out.println(formatDate(new Date()));
    }


    public static final String formatDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("You must provide a date.");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'");

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(date);
    }

}

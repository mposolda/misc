package org.mposolda.services;

import java.text.NumberFormat;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class NumberFormatUtil {

    public static String format(double value) {
        return format(value, 2);
    }

    public static String format(double value, int digits) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(value);
    }
}

package org.mposolda.util;

import java.text.NumberFormat;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class NumberFormatUtil {

    public static String format(double number) {
        return format(number, 2);
    }

    // Maybe change to public
    private static String format(double number, int digits) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(digits);
        return nf.format(number);
    }
}

package org.mposolda.util;

import java.text.NumberFormat;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class NumberUtil {

    public static String format(double value) {
        return format(value, 2);
    }

    public static String format(double value, int digits) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(value);
    }

    /**
     * Check if the particular value is zero or close to zero (As doubles are not 100% accurate)
     *
     * @param value
     * @return
     */
    public static boolean isZero(double value) {
        return isZero(value, 0.01);
    }

    /**
     * Check if the particular value is close to zero (As doubles are not 100% accurate)
     *
     * @param value
     * @param threshold
     * @return
     */
    private static boolean isZero(double value, double threshold){
        return value >= -threshold && value <= threshold;
    }
}

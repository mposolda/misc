package org.mposolda.util.returner;

import org.jboss.logging.Logger;
import org.mposolda.util.DateUtil;

/**
 * Compute what is "rate of return" per year etc.
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RateOfReturnUtil {

    protected static final Logger log = Logger.getLogger(RateOfReturnUtil.class);

    private static final int MAX_ITERATIONS = 1000;

    public static Double computeYearRateOfReturn(RateOfReturnInput input) {
        double lower = 0;
        double bigger = 1;
        double currentX = bigger;
        boolean biggerEstablished = false;

        for (int i = 0 ; i < MAX_ITERATIONS ; i++) {
            double result1 = tryComputeWithX(input, currentX);

            log.tracef("lower: %s, bigger: %s, currentX: %s, biggerEstablished: %s, result1: %s", lower, bigger, currentX, String.valueOf(biggerEstablished), result1);


            double diff = result1 - input.getEndValue();
            if (Math.abs(diff) < 0.01) {
                // To percent
                return (currentX - 1) * 100;
            };

            if (biggerEstablished) {
                if (diff < 0) {
                    lower = currentX;
                } else {
                    bigger = currentX;
                }
                currentX = lower + (bigger - lower) / 2;
            } else {
                if (diff < 0) {
                    bigger = bigger * 2;
                    currentX = bigger;
                } else {
                    biggerEstablished = true;
                    currentX = lower + (bigger - lower) / 2;
                }
            }
        }

        throw new IllegalStateException("Max count of iterations elapsed");
    }

    private static Double tryComputeWithX(RateOfReturnInput input, Double x) {
        double sum = 0;

        for (RateOfReturnInput.Deposit deposit : input.getDeposits()) {
            double periodOfDeposit = DateUtil.getPeriodInYears(deposit.getDepositDateMs(), input.getEndDateMs());
            if (periodOfDeposit < 0) {
                throw new IllegalStateException("Deposit " + DateUtil.numberInSecondsToDate(deposit.getDepositDateMs() / 1000) + " should be older than endDate " + DateUtil.numberInSecondsToDate(input.getEndDateMs() / 1000));
            }

            double depositValue = deposit.getDepositValue();
            Double currentCmp = depositValue  * Math.pow(x, periodOfDeposit);
            sum += currentCmp;
        }

        return sum;
    }






}

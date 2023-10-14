package org.mposolda.test;

import org.junit.Assert;
import org.junit.Test;
import org.mposolda.util.DateUtil;
import org.mposolda.util.returner.RateOfReturnInput;
import org.mposolda.util.returner.RateOfReturnUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RateOfReturnTest {

        // Deposit on "2020-01-01" of value 200. End date is "2022-01-01". Gain is 20 (10% per 2 years). So rate of return is around 4.88%
    @Test
    public void testRateOfReturn1() {
        RateOfReturnInput.Deposit dep1 = new RateOfReturnInput.Deposit(DateUtil.dateToNumber("2020-01-01"), 200d);
        RateOfReturnInput input = new RateOfReturnInput(DateUtil.dateToNumber("2022-01-01"), 220d, dep1);
        double output = RateOfReturnUtil.computeYearRateOfReturn(input);
        Assert.assertEquals(output, 4.88, 0.01);
    }

    // Deposit on "2021-01-01" of value 200. End date is "2022-01-01". So gain is 20 (10% per 1 years). So rate of return is around 10%
    @Test
    public void testRateOfReturn2() {
        RateOfReturnInput.Deposit dep1 = new RateOfReturnInput.Deposit(DateUtil.dateToNumber("2021-01-01"), 200d);
        RateOfReturnInput input = new RateOfReturnInput(DateUtil.dateToNumber("2022-01-01"), 220d, dep1);
        double output = RateOfReturnUtil.computeYearRateOfReturn(input);
        Assert.assertEquals(output, 10, 0.01);
    }

    // Deposit on "2020-01-01" of value 100 and another of "2021-01-01" of value 100. End date is "2022-01-01". So gain is 20 (10% per 2 years) and ate of return is around 6.53%
    @Test
    public void testRateOfReturn3() {
        RateOfReturnInput.Deposit dep1 = new RateOfReturnInput.Deposit(DateUtil.dateToNumber("2020-01-01"), 100d);
        RateOfReturnInput.Deposit dep2 = new RateOfReturnInput.Deposit(DateUtil.dateToNumber("2021-01-01"), 100d);
        RateOfReturnInput input = new RateOfReturnInput(DateUtil.dateToNumber("2022-01-01"), 220d, dep1, dep2);
        double output = RateOfReturnUtil.computeYearRateOfReturn(input);
        Assert.assertEquals(output, 6.53, 0.01);
    }



}

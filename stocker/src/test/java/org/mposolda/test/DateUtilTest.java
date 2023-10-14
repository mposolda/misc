package org.mposolda.test;

import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mposolda.util.DateUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DateUtilTest {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    @Test
    public void testDateConversions() {
        Assert.assertEquals(DateUtil.getPeriodInYears(DateUtil.dateToNumber("2020-01-01"), DateUtil.dateToNumber("2020-04-01")), 0.25, 0.01);
        Assert.assertEquals(DateUtil.getPeriodInYears(DateUtil.dateToNumber("2020-01-01"), DateUtil.dateToNumber("2021-01-01")), 1, 0.01);

        log.infof("Time in years since 2020-04-01: %d", DateUtil.getPeriodInYears(DateUtil.dateToNumber("2020-04-01"), System.currentTimeMillis()));
    }
}

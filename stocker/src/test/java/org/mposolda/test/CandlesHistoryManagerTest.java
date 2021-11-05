package org.mposolda.test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mposolda.mock.MockConfigImpl;
import org.mposolda.mock.MockFinnhubClient;
import org.mposolda.reps.CandlesRep;
import org.mposolda.services.CandlesHistoryManager;
import org.mposolda.services.Services;
import org.mposolda.util.DateUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CandlesHistoryManagerTest {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    private String workingDir;
    private CandlesHistoryManager candlesManager;
    MockFinnhubClient mockClient;

    // Contains simple file names, which should be under the working dir
    private List<String> filesToCleanupAfterTest = new LinkedList<>();

    @Before
    public void before() {
        workingDir = getWorkingDir();
        log.info("Use working dir for testing candles: " + workingDir);

        File workingDirFile = new File(workingDir);
        if (!workingDirFile.exists()) {
            workingDirFile.mkdir();
        }

        Services.instance().startTests(new MockConfigImpl(workingDir, null));

        mockClient = new MockFinnhubClient(getUserDir() + "/src/test/resources");

        // Will need to replace null if we need CompaniesJsonFileLocation
        candlesManager = new CandlesHistoryManager(mockClient, Services.instance().getCurrencyConvertor());
    }

    @After
    public void after() {
        for (String fileToCleanup : filesToCleanupAfterTest) {
            log.info("Delete file " + fileToCleanup + " after the test");
            boolean b = new File(workingDir + "/" + fileToCleanup).delete();
            if (!b) {
                log.warn("Failed to delete " + fileToCleanup);
            }
        }
    }

    @Test
    public void currencyCandlesTest() {
        log.info("Starting the test");

        filesToCleanupAfterTest.add("cur_CAD.json");

        // 1 - Test to not download any candle first. Verify that it is empty
        CandlesRep candlesRep = candlesManager.getCurrencyCandles("CAD", false, "2020-01-01", "2020-01-10");
        assertCandles(candlesRep, "CAD", null, 0, 0);
        assertMockClientDates(null, null);

        // 2 - Test download candles from 2020-01-01 to 2020-01-10. Check that it was successfully downloaded
        candlesRep = candlesManager.getCurrencyCandles("CAD", true, "2020-01-01", "2020-01-10");
        assertCandles(candlesRep, "CAD", null, DateUtil.dateToNumberSeconds("2020-01-10"), 7);
        assertMockClientDates("2020-01-01", "2020-01-10");
        mockClient.cleanupLastDatesUsed();

        Assert.assertEquals(candlesRep.getCandles().get(0).toString(), "2020-01-01 : 1.3");
        Assert.assertEquals(candlesRep.getCandles().get(candlesRep.getCandles().size() - 1).toString(), "2020-01-09 : 1.3");

        // 3 - Test to download candles, which were already downloaded. Verify that it is still there and nothing was downloaded
        candlesRep = candlesManager.getCurrencyCandles("CAD", true, "2020-01-01", "2020-01-10");
        assertCandles(candlesRep, "CAD", null, DateUtil.dateToNumberSeconds("2020-01-10"), 7);
        assertMockClientDates(null, null);

        // 4 - Test to check candles from 2020-01-01 to 2020-01-10 without download. Check no new candles downloaded
        candlesRep = candlesManager.getCurrencyCandles("CAD", false, "2020-01-01", "2020-01-20");
        assertCandles(candlesRep, "CAD", null, DateUtil.dateToNumberSeconds("2020-01-10"), 7);
        assertMockClientDates(null, null);

        // 5 - Test to download candles from 2020-01-01 to 2020-01-20. Check that it was successfully downloaded
        candlesRep = candlesManager.getCurrencyCandles("CAD", true, "2020-01-10", "2020-01-20");
        assertCandles(candlesRep, "CAD", null, DateUtil.dateToNumberSeconds("2020-01-20"), 13);
        assertMockClientDates("2020-01-10", "2020-01-20");

        Assert.assertEquals(candlesRep.getCandles().get(0).toString(), "2020-01-01 : 1.3");
        Assert.assertEquals(candlesRep.getCandles().get(candlesRep.getCandles().size() - 1).toString(), "2020-01-19 : 1.3");

    }

    private void assertCandles(CandlesRep candlesRep, String expectedCurrencyTicker, String expectedStockTicker,
                               long expectedLastTimestamp, int expectedCandlesSize) {
        if (expectedCurrencyTicker == null) {
            Assert.assertNull(candlesRep.getCurrencyTicker());
        } else {
            Assert.assertEquals(expectedCurrencyTicker, candlesRep.getCurrencyTicker());
        }
        if (expectedStockTicker == null) {
            Assert.assertNull(candlesRep.getStockTicker());
        } else {
            Assert.assertEquals(expectedStockTicker, candlesRep.getStockTicker());
        }
        Assert.assertEquals(expectedLastTimestamp, candlesRep.getLastDateTimestampSec());
        Assert.assertEquals(expectedCandlesSize, candlesRep.getCandles().size());
    }

    private void assertMockClientDates(String expectedStartDate, String expectedEndDate) {
        if (expectedStartDate == null) {
            Assert.assertNull(mockClient.getLastStartDateUsed());
        } else {
            Assert.assertEquals(expectedStartDate, mockClient.getLastStartDateUsed());
        }

        if (expectedEndDate == null) {
            Assert.assertNull(mockClient.getLastEndDateUsed());
        } else {
            Assert.assertEquals(expectedEndDate, mockClient.getLastEndDateUsed());
        }
    }

    private String getUserDir() {
        String userDir = System.getProperty("user.dir");
        if (userDir == null) {
            throw new IllegalStateException("System property user.dir is missing");
        }
        return userDir;
    }

    private String getWorkingDir() {
        String dir = getUserDir() + "/target";

        File targetDir = new File(dir);
        if (!targetDir.exists() || !targetDir.isDirectory()) {
            throw new IllegalStateException("Target directory " + targetDir + " does not exists or is not directory");
        }

        return targetDir + "/candles";
    }
}

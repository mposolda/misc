package org.mposolda.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.mposolda.mock.MockConfigImpl;
import org.mposolda.services.HistoryManager;
import org.mposolda.services.Services;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class HistoryManagerTest {

    @Test
    public void test01NonExistentDir() {
        String jsonDir = getJsonFilesDir();
        String jsonFile = jsonDir + "/stocks-1-usd-single-company-no-fees.json";
        Services.instance().startTests(new MockConfigImpl(jsonDir + "/nonexistent", jsonFile));

        try {
            HistoryManager historyManager = new HistoryManager();
            historyManager.checkHistoryOfStocksFile();
            Assert.fail("Not expected to start successfully");
        } catch (IllegalArgumentException expected) {
            //
        }
    }

    @Test
    public void test02NonExistentFile() {
        String jsonDir = getJsonFilesDir();
        String jsonFile = jsonDir + "/stocks-1-usd-single-company-no-fees-nonexistent.json";
        Services.instance().startTests(new MockConfigImpl(jsonDir, jsonFile));

        try {
            HistoryManager historyManager = new HistoryManager();
            historyManager.checkHistoryOfStocksFile();
            Assert.fail("Not expected to start successfully");
        } catch (IllegalArgumentException expected) {
            //
        }
    }

    // Test that new file would be created when we already have some old existing files in the "history" directory for this pattern
    @Test
    public void test03NewFileCreatedForExistingFilesInTheHistoryDirectory() {
        testHistoryFileCreated("stocks-7-usd-single-company-fees-sold.json");
    }

    // Test that new file would be created when we don't have any old existing files in the "history" directory for this pattern
    @Test
    public void test04NewFileCreatedForNonExistingFilesInTheHistoryDirectory() {
        testHistoryFileCreated("stocks-6-usd-cad-more-companies-fees-dividends.json");
    }

    // Test that new file won't be created when we have in "history" directory existing new file
    @Test
    public void test05NewFileNotCreatedWhenAlreadyPresent() {
        String jsonDir = getJsonFilesDir();
        String jsonFile = jsonDir + File.separator + "stocks-8-usd-single-company-fees-dividends-sold.json";
        Services.instance().startTests(new MockConfigImpl(jsonDir, jsonFile));

        HistoryManager historyManager = new HistoryManager();
        String newFilePath = null;

        // New history file should not be created
        historyManager.checkHistoryOfStocksFile();
        newFilePath = historyManager.getNewFileFullPath();
        Assert.assertNull(newFilePath);
    }

    private void testHistoryFileCreated(String baseFileName) {
        String jsonDir = getJsonFilesDir();
        String jsonFile = jsonDir + File.separator + baseFileName;
        Services.instance().startTests(new MockConfigImpl(jsonDir, jsonFile));

        HistoryManager historyManager = new HistoryManager();
        String newFilePath = null;
        try {
            // New history file should be created
            historyManager.checkHistoryOfStocksFile();
            newFilePath = historyManager.getNewFileFullPath();
            Assert.assertNotNull(newFilePath);
            Assert.assertTrue(newFilePath.endsWith(baseFileName));
            Assert.assertEquals(newFilePath.length(), jsonFile.length() + 19); // 19 is size of the pattern like "history/2022-02-02_"

            // Another attempt - new history file won't be created now (NOTE: This may fail in the corner case when this test is executed around midnight. That is fine for now...)
            historyManager = new HistoryManager();
            historyManager.checkHistoryOfStocksFile();
            String newFilePath2 = historyManager.getNewFileFullPath();
            Assert.assertNull(newFilePath2);
        } finally {
            removeNewHistoryFile(newFilePath);
        }
    }

    private String getJsonFilesDir() {
        return System.getProperty("user.dir") + "/src/test/resources";
    }

    private void removeNewHistoryFile(String newFilePath) {
        if (newFilePath != null) {
            File f = new File(newFilePath);
            boolean deleted = f.delete();
            if (!deleted) {
                Assert.fail("File " + newFilePath + " was not deleted! Please check what happened and possibly cleanup manually");
            }
        }
    }
}

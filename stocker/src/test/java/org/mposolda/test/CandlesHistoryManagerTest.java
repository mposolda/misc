package org.mposolda.test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mposolda.services.CandlesHistoryManager;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CandlesHistoryManagerTest {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    private String workingDir;
    private CandlesHistoryManager candlesManager;

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

        MockFinhubClient mockClient = new MockFinhubClient(getTargetDir());
        candlesManager = new CandlesHistoryManager(workingDir, mockClient);
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
    }

    private String getTargetDir() {
        String userDir = System.getProperty("user.dir");
        if (userDir == null) {
            throw new IllegalStateException("System property user.dir is missing");
        }
        String dir = userDir + "/target";

        File targetDir = new File(dir);
        if (!targetDir.exists() || !targetDir.isDirectory()) {
            throw new IllegalStateException("Target directory " + targetDir + " does not exists or is not directory");
        }
        return dir;
    }

    private String getWorkingDir() {
        return getTargetDir() + "/candles";
    }
}

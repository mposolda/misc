package org.mposolda.util;

import java.io.File;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FileUtil {

    public static final String HISTORY_DIR = "history";
    public static final String CANDLES_DIR = "candles";

    public static File checkDirectoryExistsAndIsDirectory(String dirPathFull) {
        File historyDir = new File(dirPathFull);
        if (!historyDir.exists() || !historyDir.isDirectory()) {
            throw new IllegalArgumentException("Directory " + historyDir.getAbsolutePath() + " does not exists or is not directory");
        }
        return historyDir;
    }
}

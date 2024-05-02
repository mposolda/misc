package org.mposolda.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import org.mposolda.util.DateUtil;
import org.mposolda.util.FileUtil;

/**
 * Possibly copy the current "stocks.json" file to the `history` directory. This is to preserve history of the changes in the stocks.json file.
 *
 * Not thread-safe class
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class HistoryManager {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    private final String  companiesJsonFileLocation;
    private final String  stocksDirLocation;

    private String newFileFullPath; // Computed when calling "checkHistoryOfStocksFile()"

    public HistoryManager() {
        this.companiesJsonFileLocation = Services.instance().getConfig().getCompaniesJsonFileLocation();
        this.stocksDirLocation = Services.instance().getConfig().getStocksDirLocation();
    }

    public void checkHistoryOfStocksFile() {
        File historyDir = getHistoryDir();
        File baseFile = getBaseFile();

        String baseFileName = baseFile.getName();

        // List files of our pattern
        File[] files = historyDir.listFiles((file, name) -> name.endsWith(baseFile.getName()));
        log.debugf("Files size: %d", files.length);

        String modifiedDateOfLastFile;
        String lastFileName = null;
        if (files.length == 0) {
            modifiedDateOfLastFile = "0000-00-00";
        } else {
            // Sort files and find the last one
            List<File> sortedFiles = List.of(files).stream()
                    .sorted((file1, file2) -> file1.getName().compareTo(file2.getName()))
                    .collect(Collectors.toList());
            lastFileName = sortedFiles.get(sortedFiles.size() - 1).getName();

            // Check if the date of the current base file is newer than the last history file. If yes, then copy new file into history directory
            modifiedDateOfLastFile = lastFileName.substring(0, 10);
        }

        // Find the date of the current base file
        String modifiedDateOfBaseFile = DateUtil.numberInMillisecondsToDate(baseFile.lastModified());

        if (modifiedDateOfBaseFile.compareTo(modifiedDateOfLastFile) > 0) {
            String newFileName = getNewFileName();
            this.newFileFullPath = historyDir.getAbsolutePath() + "/" + newFileName;
            log.infof("Changes found in the file '%s'. Will create new file '%s' in the history directory. Previous last file was '%s'.", baseFileName, newFileName, lastFileName);

            copyFiles(baseFile.getAbsolutePath(), this.newFileFullPath);
        } else {
            log.infof("File '%s' did not changed. Last history file is '%s'", baseFileName, lastFileName);
        }
    }

    /**
     * Should be called after "checkHistoryOfStocksFile()"
     *
     * @return null in case that new file was not created. Otherwise full path of newly created file.
     */
    public String getNewFileFullPath() {
        return newFileFullPath;
    }

    private File getHistoryDir() {
        return FileUtil.checkDirectoryExistsAndIsDirectory(this.stocksDirLocation + File.separator + FileUtil.HISTORY_DIR);
    }

    private File getBaseFile() {
        File baseFile = new File(this.companiesJsonFileLocation);
        if (!baseFile.exists() || !baseFile.isFile()) {
            throw new IllegalArgumentException("File " + this.companiesJsonFileLocation + " does not exists or is not file");
        }
        return baseFile;
    }

    // return only simple name (EG. "2024-04-20_stocks.json")
    private String getNewFileName() {
        String currentDate = DateUtil.numberInSecondsToDate(DateUtil.getCurrentTimestampInSeconds());
        File baseFile = getBaseFile();
        return currentDate + "_" + baseFile.getName();
    }



    private void copyFiles(String origFilePath, String newFilePath) {
        try {
            Path copied = Paths.get(newFilePath);
            Path originalPath = Paths.get(origFilePath);
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            log.errorf("Failed to copy from '%s' to '%s'.", origFilePath, newFilePath);
            throw new IllegalStateException(ioe);
        }
    }
}

package org.mposolda.services;

import java.io.File;

import org.mposolda.util.FileUtil;

/**
 * Static class to handle configs
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StockerConfigImpl implements StockerConfig {

    private final Integer port; // Port where server will listen
    private final String token; // Used for calling finnhub
    private final String fixerToken; // Used for calling fixer
    private final String stocksDirLocation;
    private final String candlesDirLocation;
    private final String companiesJsonFileLocation;
    private final boolean offlineMode; // Assumption is no internet connection during offline mode. Stuff loaded from last candles. Useful for development only


    public StockerConfigImpl() {
        this.port = Integer.parseInt(System.getProperty("port", "8085"));
        this.token = System.getProperty("token");
        if (this.token == null) {
            throw new IllegalArgumentException("Need to provide system property 'token' with the finnhub API token");
        }
        this.fixerToken = System.getProperty("fixerToken");
        if (this.fixerToken == null) {
            throw new IllegalArgumentException("Need to provide system property 'fixerToken' with the fixer API token");
        }
        this.stocksDirLocation = System.getProperty("stocksDir");
        if (stocksDirLocation == null) {
            throw new IllegalArgumentException("Need to provide system property 'stocksDir' with the directory, which will need" +
                    "to contain stocks.json file with the data about companies and currencies");
        }
        File stocksDir = FileUtil.checkDirectoryExistsAndIsDirectory(stocksDirLocation);

        this.candlesDirLocation = this.stocksDirLocation + File.separator + FileUtil.CANDLES_DIR;
        FileUtil.checkDirectoryExistsAndIsDirectory(this.candlesDirLocation);

        this.companiesJsonFileLocation = stocksDir + File.separator + "stocks.json";
        File companiesJsonFile = new File(companiesJsonFileLocation);
        if (!companiesJsonFile.exists()) {
            throw new IllegalArgumentException("File '" + companiesJsonFile +  "' does not exists");
        }

        this.offlineMode = Boolean.getBoolean("offlineMode");
    }

    @Override
    public Integer getUndertowPort() {
        return port;
    }

    @Override
    public String getFinnhubToken() {
        return token;
    }

    @Override
    public String getFixerToken() {
        return fixerToken;
    }

    @Override
    public String getStocksDirLocation() {
        return stocksDirLocation;
    }

    @Override
    public String getCandlesDirLocation() {
        return candlesDirLocation;
    }

    @Override
    public String getCompaniesJsonFileLocation() {
        return companiesJsonFileLocation;
    }

    @Override
    public boolean isOfflineMode() {
        return offlineMode;
    }
}

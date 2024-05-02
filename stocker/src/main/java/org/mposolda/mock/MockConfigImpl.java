package org.mposolda.mock;

import org.mposolda.services.StockerConfig;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MockConfigImpl implements StockerConfig {

    private final String stocksDirLocation;
    private final String companiesJsonFile;

    public MockConfigImpl(String companiesJsonFile) {
        this(null, companiesJsonFile);
    }

    public MockConfigImpl(String stocksDirLocation, String companiesJsonFile) {
        this.stocksDirLocation = stocksDirLocation;
        this.companiesJsonFile = companiesJsonFile;
    }

    @Override
    public Integer getUndertowPort() {
        return 8087;
    }

    @Override
    public String getFinnhubToken() {
        return null;
    }

    @Override
    public String getFixerToken() {
        return null;
    }

    @Override
    public String getStocksDirLocation() {
        return this.stocksDirLocation;
    }

    @Override
    public String getCandlesDirLocation() {
        return this.stocksDirLocation;
    }

    @Override
    public String getCompaniesJsonFileLocation() {
        return this.companiesJsonFile;
    }

    @Override
    public boolean isOfflineMode() {
        return true; // Always return offlineMode in tests
    }
}

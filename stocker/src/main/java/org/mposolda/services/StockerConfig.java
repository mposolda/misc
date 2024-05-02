package org.mposolda.services;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface StockerConfig {

    Integer getUndertowPort();

    String getFinnhubToken();

    String getFixerToken();

    String getStocksDirLocation();

    String getCandlesDirLocation();

    String getCompaniesJsonFileLocation();

    boolean isOfflineMode();
}

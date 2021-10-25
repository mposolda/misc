package org.mposolda.services;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface StockerConfig {

    String getFinnhubToken();

    String getFixerToken();

    String getStocksDirLocation();

    String getCompaniesJsonFileLocation();

    boolean isOfflineMode();
}

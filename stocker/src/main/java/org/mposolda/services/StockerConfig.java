package org.mposolda.services;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface StockerConfig {

    String getToken();

    String getStocksDirLocation();

    String getCompaniesJsonFileLocation();

    boolean isOfflineMode();
}

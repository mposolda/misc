package org.mposolda.services;

import org.jboss.logging.Logger;
import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.client.FinnhubHttpClientImpl;
import org.mposolda.client.FinnhubHttpClientWrapper;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Services {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    // singleton
    private static final Services INSTANCE = new Services();

    private Services() {
    }

    public static Services instance() {
        return INSTANCE;
    }

    // Services
    private FinnhubHttpClient finhubClient;
    private CurrencyConvertor currencyConvertor;
    private CompanyInfoManager companyInfoManager;


    public void start() {
        String token = System.getProperty("token");
        if (token == null) {
            throw new IllegalArgumentException("Need to provide system property 'token' with the finnhub API token");
        }
        String companiesJsonFileLocation = System.getProperty("companiesJson");
        if (companiesJsonFileLocation == null) {
            throw new IllegalArgumentException("Need to provide system property 'companiesJson' with the JSON information about companies");
        }

        finhubClient = new FinnhubHttpClientWrapper(new FinnhubHttpClientImpl(token));
        log.info("Created finnhub client");

        currencyConvertor = new CurrencyConvertor(finhubClient);
        currencyConvertor.start();
        log.info("Created currencyConvertor and loaded currencies from forex");

        companyInfoManager = new CompanyInfoManager(finhubClient, currencyConvertor, companiesJsonFileLocation);
        companyInfoManager.start();
        log.info("Created companyInfoManager and loaded companies");
    }

    public FinnhubHttpClient getFinhubClient() {
        return finhubClient;
    }

    public CurrencyConvertor getCurrencyConvertor() {
        return currencyConvertor;
    }

    public CompanyInfoManager getCompanyInfoManager() {
        return companyInfoManager;
    }
}

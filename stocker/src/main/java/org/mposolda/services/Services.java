package org.mposolda.services;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;

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
    private PurchaseManager purchaseManager;
    private FinnhubHttpClient finhubClient;
    private CurrencyConvertor currencyConvertor;
    private CompanyInfoManager companyInfoManager;

    private List<Closeable> closeables = new LinkedList<>();


    public void start() {
        String token = System.getProperty("token");
        if (token == null) {
            throw new IllegalArgumentException("Need to provide system property 'token' with the finnhub API token");
        }
        String companiesJsonFileLocation = System.getProperty("companiesJson");
        if (companiesJsonFileLocation == null) {
            throw new IllegalArgumentException("Need to provide system property 'companiesJson' with the JSON information about companies");
        }

        purchaseManager = new PurchaseManager(companiesJsonFileLocation);
        purchaseManager.start();
        log.info("Created purchase manager");

        finhubClient = new FinnhubHttpClientWrapper(new FinnhubHttpClientImpl(token));
        closeables.add(finhubClient);
        log.info("Created finnhub client");

        currencyConvertor = new CurrencyConvertor(finhubClient);
        currencyConvertor.start();
        log.info("Created currencyConvertor and loaded currencies from forex");

        companyInfoManager = new CompanyInfoManager(finhubClient, currencyConvertor, companiesJsonFileLocation);
        companyInfoManager.start();
        log.info("Created companyInfoManager and loaded companies");
    }

    // Called at the server shutdown
    public void close() {
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (Exception e) {
                log.warn("Exception when closing " + closeable, e);
            }
        }
    }

    public FinnhubHttpClient getFinhubClient() {
        return finhubClient;
    }

    public CurrencyConvertor getCurrencyConvertor() {
        return currencyConvertor;
    }

    public PurchaseManager getPurchaseManager() {
        return purchaseManager;
    }

    public CompanyInfoManager getCompanyInfoManager() {
        return companyInfoManager;
    }
}

package org.mposolda.services;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;
import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.client.FinnhubHttpClientImpl;
import org.mposolda.client.FinnhubHttpClientWrapper;
import org.mposolda.client.FixerHttpClientImpl;
import org.mposolda.mock.MockFinnhubClient;

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
    private CandlesHistoryManager candlesManager;

    private StockerConfig config;

    private List<Closeable> closeables = new LinkedList<>();


    public void start() {
        this.config = new StockerConfigImpl();

        // Just test if network host/port is available to be able to fail-fast
        new QuickNetworkTestManager().test();

        purchaseManager = new PurchaseManager();
        purchaseManager.start();
        log.info("Created purchase manager");

        if (this.config.isOfflineMode()) {
            finhubClient = new MockFinnhubClient(this.config.getStocksDirLocation());
            log.info("Created MOCK finnhub client");
        } else {
            finhubClient = new FinnhubHttpClientWrapper(new FinnhubHttpClientImpl(), new FixerHttpClientImpl());
            log.info("Created finnhub client");
        }
        closeables.add(finhubClient);

        List<String> currencyTickers = new ArrayList<>(purchaseManager.getCurrenciesInfo().getCurrencyRemainingAmount().keySet());

        currencyConvertor = new CurrencyConvertor(finhubClient, currencyTickers);
        currencyConvertor.start();
        log.info("Created currencyConvertor and loaded currencies from forex");

        candlesManager = new CandlesHistoryManager(finhubClient, currencyTickers);

        companyInfoManager = new CompanyInfoManager(finhubClient, currencyConvertor, candlesManager);
        companyInfoManager.start();
        log.info("Created companyInfoManager and loaded companies");
    }

    /** Use in unit tests only! */
    public void startTests(StockerConfig config) {
        this.config = config;
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

    public StockerConfig getConfig() {
        return config;
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

    public CandlesHistoryManager getCandlesHistoryManager() {
        return candlesManager;
    }

}

package org.mposolda.services;

import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;
import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.reps.CandlesRep;
import org.mposolda.reps.CompanyRep;
import org.mposolda.reps.CurrencyRep;
import org.mposolda.reps.DatabaseRep;
import org.mposolda.reps.QuoteLoaderRep;
import org.mposolda.util.JsonUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CandlesHistoryManager {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    private final String companiesJsonFileLocation;
    private final String stocksDir;
    private final CandlesDAO candlesDAO;

    public CandlesHistoryManager(String companiesJsonFileLocation, String stocksDir, FinnhubHttpClient finhubClient) {
        this.companiesJsonFileLocation = companiesJsonFileLocation;
        this.stocksDir = stocksDir;
        this.candlesDAO = new CandlesDAO(stocksDir, finhubClient);
    }

    /**
     * Will try to download all currency candles and company candles
     */
    public void allCandlesDownload() {
        String companyJsonFileLocation = Services.instance().getCompaniesJsonFileLocation();
        DatabaseRep database = JsonUtil.loadDatabase(companyJsonFileLocation);

        for (CurrencyRep currency : database.getCurrencies()) {
            // Skip "EUR"
            if (currency.getTicker().equals("EUR")) continue;

            CandlesRep currencyCandle = getCurrencyCandles(currency.getTicker(), true);
            log.info("Loaded candles representation for currency " + currency.getTicker());
        }

        List<String> failedCompanies = new LinkedList<>();
        for (CompanyRep company : database.getCompanies()) {
            try {
                CandlesRep stockCandle = getStockCandles(company, true);
                log.info("Loaded candles representation for company " + company.getTicker());
            } catch (FailedCandleDownloadException e) {
                log.warn("Failed to download candles representation for company " + company.getTicker() + ". Fallback was needed to download only quote for current day.");
                failedCompanies.add(company.getTicker());
            }
        }

        log.infof("All failed company tickers where fallback was needed " + failedCompanies);
    }

    public CandlesRep getStockCandles(QuoteLoaderRep company, boolean downloadNewest) throws FailedCandleDownloadException {
        return candlesDAO.getStockCandles(company, downloadNewest);
    }

    public CandlesRep getStockCandles(QuoteLoaderRep company, boolean downloadNewest, String startingDateStr, String endDateStr) throws FailedCandleDownloadException {
        return candlesDAO.getStockCandles(company, downloadNewest, startingDateStr, endDateStr);
    }

    public CandlesRep getStockCandlesInCZK(String stockTicker, boolean downloadNewest) {
        // TODO:mposolda
        return null;
    }

    public CandlesRep getCurrencyCandles(String currencyTicker, boolean downloadNewest) {
        return candlesDAO.getCurrencyCandles(currencyTicker, downloadNewest);
    }

    public CandlesRep getCurrencyCandles(String currencyTicker, boolean downloadNewest, String startingDateStr, String endDateStr) {
        return candlesDAO.getCurrencyCandles(currencyTicker, downloadNewest, startingDateStr, endDateStr);
    }

    /**
     * Return currency candles to CZK (1 targetCurrency to X CZK where X will be the number displayed in the candles)
     *
     * @param currencyTicker
     * @param downloadNewest
     * @return
     */
    public CandlesRep getCurrencyCandlesToCZK(String currencyTicker, boolean downloadNewest) {
        // TODO:mposolda
        return null;
    }
}

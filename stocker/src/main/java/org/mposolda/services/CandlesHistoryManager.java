package org.mposolda.services;

import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;
import org.mposolda.client.StockerHttpClient;
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
    private final String candlesDir;
    private final CandlesDAO candlesDAO;

    public CandlesHistoryManager(StockerHttpClient finhubClient, CurrencyConvertor currencyConvertor) {
        this.companiesJsonFileLocation = Services.instance().getConfig().getCompaniesJsonFileLocation();
        this.candlesDir = Services.instance().getConfig().getCandlesDirLocation();
        this.candlesDAO = new CandlesDAO(candlesDir, finhubClient, currencyConvertor);
    }

    /**
     * Will try to download all currency candles and company candles
     */
    public void allCandlesDownload() {
        DatabaseRep database = JsonUtil.loadDatabase(companiesJsonFileLocation);

        for (CurrencyRep currency : database.getCurrencies()) {
            // Skip "EUR"
            if (currency.getTicker().equals("EUR")) continue;

            CandlesRep currencyCandle = getCurrencyCandles(currency.getTicker(), true);
            log.info("Loaded candles representation for currency " + currency.getTicker());
        }

        List<String> skippedCompanies = new LinkedList<>();
        List<String> fallbackFailedCompanies = new LinkedList<>();
        List<String> failedCompanies = new LinkedList<>();
        for (CompanyRep company : database.getCompanies()) {
            try {
                if (company.isSkipLoadingCandle()) {
                    log.info("Skip loading candle for company " + company.getTicker());
                    skippedCompanies.add(company.getTicker());
                } else {
                    CandlesRep stockCandle = getStockCandles(company, true);
                    log.info("Loaded candles representation for company " + company.getTicker());
                }
            } catch (FailedCandleDownloadException e) {
                if (e.isQuoteFallbackFailed()) {
                    log.warnf("Failed to download candles representation for company '%s'. Fallback failed as well.", company.getTicker());
                    failedCompanies.add(company.getTicker());
                } else {
                    log.warnf("Failed to download candles representation for company '%s'. Fallback was needed to download only quote for current day.", company.getTicker());
                    fallbackFailedCompanies.add(company.getTicker());
                }
            } catch (RuntimeException re) {
                log.warnf(re, "Unexpected exception when downloading candle for company %s. Will skip the candle", company.getTicker());
                failedCompanies.add(company.getTicker());
            }
        }

        log.infof("All skipped company tickers for candles download " + skippedCompanies);
        log.infof("All failed company tickers where fallback was needed " + fallbackFailedCompanies);
        if (!failedCompanies.isEmpty()) {
            log.warnf("All failed company tickers where fallback was not used " + failedCompanies);
        }
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

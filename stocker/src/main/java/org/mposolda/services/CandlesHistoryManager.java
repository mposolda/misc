package org.mposolda.services;

import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.reps.CandlesRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CandlesHistoryManager {

    private final String stocksDir;
    private final CandlesDAO candlesDAO;

    public CandlesHistoryManager(String stocksDir, FinnhubHttpClient finhubClient) {
        this.stocksDir = stocksDir;
        this.candlesDAO = new CandlesDAO(stocksDir, finhubClient);
    }

    public CandlesRep getStockCandles(String stockTicker, boolean downloadNewest) {
        return candlesDAO.getStockCandles(stockTicker, downloadNewest);
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

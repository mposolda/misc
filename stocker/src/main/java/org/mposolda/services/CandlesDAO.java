package org.mposolda.services;

import java.util.Date;

import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.reps.CandlesRep;
import org.mposolda.util.DateUtil;

/**
 * Intended to be used only by CandlesHistoryManager
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
class CandlesDAO {

    /**
     * Date from which our history is started
     */
    public static final String DEFAULT_STARTING_DATE = "2020-01-01";

    private final String stocksDir;
    private final FinnhubHttpClient finhubClient;

    CandlesDAO(String stocksDir, FinnhubHttpClient finhubClient) {
        this.stocksDir = stocksDir;
        this.finhubClient = finhubClient;
    }

    CandlesRep getStockCandles(String stockTicker, boolean downloadNewest) {
        // TODO:mposolda implement
        // Algorithm:
        // 1) Go to the specified directory and check for the file <stockTicker>.json
        // 2) If does not exists and "downloadNewest" is false, then return empty CandlesRep (probably hand-create some)
        // 3) If exists, then download the JSON from it
        // 4) If "downloadNewest" == false, then immediately return
        // 5) Check last timestamp from the JSON and compare with current timestamp. If it is same day, then return
        // 6) Download the newest candle from "lastDate" to the current date from finhub
        // 7) In case, that candle did not yet exists (step 2), then onvert downloaded candle to the "internal" CandlesRep and return
        // 8) In case, that candle existed (step 3), then convert downloaded candle to the "internal" candle and add new candles to the CandlesRep
        return null;
    }

    CandlesRep getCurrencyCandles(String currencyTicker, boolean downloadNewest) {
        String currentDate = DateUtil.numberInSecondsToDate(new Date().getTime() / 1000);
        return getCurrencyCandles(currencyTicker, downloadNewest, DEFAULT_STARTING_DATE, currentDate);
    }

    CandlesRep getCurrencyCandles(String currencyTicker, boolean downloadNewest, String startingDateStr, String endDateStr) {
        long startDate = DateUtil.dateToNumberSeconds(startingDateStr);
        long endDate = DateUtil.dateToNumberSeconds(endDateStr);

        // TODO:mposolda implement
        // Algorithm:
        // 1) Go to the specified directory and check for the file <stockTicker>.json
        // 2) If does not exists and "downloadNewest" is false, then return empty CandlesRep (probably hand-create some)
        // 3) If exists, then download the JSON from it
        // 4) If "downloadNewest" == false, then immediately return
        // 5) Check last timestamp from the JSON and compare with current timestamp. If it is same day, then return
        // 6) Download the newest candle from "lastDate" to the current date from finhub
        // 7) In case, that candle did not yet exists (step 2), then onvert downloaded candle to the "internal" CandlesRep and return
        // 8) In case, that candle existed (step 3), then convert downloaded candle to the "internal" candle and add new candles to the CandlesRep
        return null;
    }

}

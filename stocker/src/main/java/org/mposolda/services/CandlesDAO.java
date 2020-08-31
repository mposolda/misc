package org.mposolda.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.client.JsonSerialization;
import org.mposolda.reps.CandlesRep;
import org.mposolda.reps.finhub.CandleRep;
import org.mposolda.reps.finhub.QuoteRep;
import org.mposolda.util.DateUtil;
import org.mposolda.util.JsonUtil;
import org.mposolda.util.WaitUtil;

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

    CandlesRep getStockCandles(String stockTicker, boolean downloadNewest) throws FailedCandleDownloadException {
        String currentDate = DateUtil.numberInSecondsToDate(DateUtil.getCurrentTimestamp());
        return getStockCandles(stockTicker, downloadNewest, DEFAULT_STARTING_DATE, currentDate);
    }

    CandlesRep getStockCandles(String stockTicker, boolean downloadNewest, String startingDateStr, String endDateStr) throws FailedCandleDownloadException {
        return getCandles(stockTicker, downloadNewest, startingDateStr, endDateStr, false);
    }

    CandlesRep getCurrencyCandles(String currencyTicker, boolean downloadNewest) {
        String currentDate = DateUtil.numberInSecondsToDate(DateUtil.getCurrentTimestamp());
        return getCurrencyCandles(currencyTicker, downloadNewest, DEFAULT_STARTING_DATE, currentDate);
    }

    CandlesRep getCurrencyCandles(String currencyTicker, boolean downloadNewest, String startingDateStr, String endDateStr) {
        try {
            return getCandles(currencyTicker, downloadNewest, startingDateStr, endDateStr, true);
        } catch (FailedCandleDownloadException fcde) {
            // This should not happen. We rethrow the exception if it happens
            throw new RuntimeException("Failed to download currency candle. No fallback available for currency candles", fcde);
        }
    }

    // "isCurrencyCandle" is true when downloading currency candles. It is false when downloading stock candles
    private CandlesRep getCandles(String ticker, boolean downloadNewest, String startingDateStr, String endDateStr, boolean isCurrencyCandle) throws FailedCandleDownloadException {
        long startDate = DateUtil.dateToNumberSeconds(startingDateStr);
        long endDate = DateUtil.dateToNumberSeconds(endDateStr);

        // 1) Go to the specified directory and check for the file cur_<currencyTicker>.json (in case of currency candles) or
        // comp_<stockTicker>.json in case of stock candles
        String tickerFile = isCurrencyCandle ? stocksDir + "/cur_" + ticker + ".json" : stocksDir + "/comp_" + ticker + ".json";
        boolean fileExists = new File(tickerFile).exists();

        // 2) If does not exists and "downloadNewest" is false, then return empty CandlesRep
        CandlesRep candlesRep = null;
        if (!fileExists) {
            candlesRep = createEmptyCandlesRepForTicker(ticker, isCurrencyCandle);
        } else {
            // 3) If exists, then download the JSON from it
            candlesRep = JsonUtil.loadFileToJson(tickerFile, CandlesRep.class);
        }

        // 4) If "downloadNewest" == false, then immediately return
        if (!downloadNewest) {
            return candlesRep;
        }

        // 5) Check last timestamp from the JSON and compare with current timestamp. If it is same day, then return
        long lastComputedTimestamp = candlesRep.getLastDateTimestampSec();
        if (lastComputedTimestamp >= endDate) {
            return candlesRep;
        }

        // 6) Download the newest candle from "lastDate" to the current date from finhub
        long startDateToUse = Math.max(startDate, lastComputedTimestamp);
        String startDateToUseStr = DateUtil.numberInSecondsToDate(startDateToUse);
        CandleRep finhubCandle = isCurrencyCandle ? finhubClient.getCurrencyCandle(ticker, startDateToUseStr, endDateStr)
                : finhubClient.getStockCandle(ticker, startDateToUseStr, endDateStr);

        // 7 For failed company candles, we will fallback to download current quote and just use the quote from the current day
        boolean fallbackNeeded = false;
        if (!isCurrencyCandle && finhubCandle.getOpenDayPrice() == null) {
            fallbackNeeded = true;
            QuoteRep quote = finhubClient.getQuoteRep(ticker);

            // Just manually create candlesRep from the quote
            finhubCandle = new CandleRep();
            finhubCandle.setCurrentPrice(Collections.singletonList(quote.getCurrentPrice()));
            finhubCandle.setHighDayPrice (Collections.singletonList(quote.getHighDayPrice()));
            finhubCandle.setLowDayPrice(Collections.singletonList(quote.getLowDayPrice()));
            finhubCandle.setOpenDayPrice(Collections.singletonList(quote.getOpenDayPrice()));
            finhubCandle.setTimestamps(Collections.singletonList(DateUtil.getCurrentTimestamp()));

            // Rather wait to enforce some pause among calls
            WaitUtil.pause(WaitUtil.INTERVAL * 2);
        }

        // 8 convert downloaded candle to the "internal" CandlesRep and add to it
        addFinhubCandleToCandles(finhubCandle, candlesRep);
        candlesRep.setLastDateTimestampSec(endDate);

        // 9 Save the newest candle
        JsonUtil.saveJsonToFile(tickerFile, candlesRep);

        // Just throw the exception if we needed to fallback. This is not so great solution, but sufficient for now...
        if (fallbackNeeded) {
            throw new FailedCandleDownloadException("Failed to download candle for the company " + ticker);
        }

        return candlesRep;
    }

    private CandlesRep createEmptyCandlesRepForTicker(String ticker, boolean isCurrencyCandle) {
        CandlesRep candlesRep = new CandlesRep();
        if (isCurrencyCandle) {
            candlesRep.setCurrencyTicker(ticker);
        } else {
            candlesRep.setStockTicker(ticker);
        }
        candlesRep.setCandles(new LinkedList<>());
        return candlesRep;
    }

    private void addFinhubCandleToCandles(CandleRep finhubCandle, CandlesRep candlesRep) {
        if (finhubCandle.getOpenDayPrice().size() != finhubCandle.getTimestamps().size()) {
            throw new IllegalArgumentException("Invalid finhubCandle. Size of the open days is different than size of the timestamps. " +
                    "Candle is " + finhubCandle);
        }

        List<CandlesRep.CandleRep> internalCandles = candlesRep.getCandles();
        if (internalCandles == null) {
            candlesRep.setCandles(new ArrayList<>());
            internalCandles = candlesRep.getCandles();
        }

        long lastStartDate = internalCandles.size() == 0 ? 0 : internalCandles.get(internalCandles.size() - 1).getTimestampInSeconds();

        for (int i=0 ; i<finhubCandle.getCurrentPrice().size() ; i++) {
            double currentDay = finhubCandle.getCurrentPrice().get(i);
            long timestamp = finhubCandle.getTimestamps().get(i);

            // Ignore this. Sometimes the already existing candles are shown
            if (timestamp <= lastStartDate) {
                continue;
            }

            CandlesRep.CandleRep candle = new CandlesRep.CandleRep();
            candle.setTimestampInSeconds(timestamp);
            candle.setValue(currentDay);

            internalCandles.add(candle);
        }
    }
}

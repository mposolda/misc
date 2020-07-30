package org.mposolda.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.client.JsonSerialization;
import org.mposolda.reps.CandlesRep;
import org.mposolda.reps.finhub.CandleRep;
import org.mposolda.util.DateUtil;
import org.mposolda.util.JsonUtil;

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

        // 1) Go to the specified directory and check for the file <stockTicker>.json
        String stocksTickerFile = stocksDir + "/cur_" + currencyTicker + ".json";
        boolean fileExists = new File(stocksTickerFile).exists();

        // 2) If does not exists and "downloadNewest" is false, then return empty CandlesRep (probably hand-create some)
        CandlesRep candlesRep = null;
        if (!fileExists) {
            candlesRep = createEmptyCurrencyCandlesRepForTicker(currencyTicker, endDate);
        } else {
            // 3) If exists, then download the JSON from it
            candlesRep = JsonUtil.loadFileToJson(stocksTickerFile, CandlesRep.class);
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
        CandleRep finhubCandle = finhubClient.getCurrencyCandle(currencyTicker, startDateToUseStr, endDateStr);

        // 7 convert downloaded candle to the "internal" CandlesRep and add to it
        addFinhubCandleToCandles(finhubCandle, candlesRep);
        candlesRep.setLastDateTimestampSec(endDate);

        // 8 Save the newest candle
        JsonUtil.saveJsonToFile(stocksTickerFile, candlesRep);

        return candlesRep;
    }

    private CandlesRep createEmptyCurrencyCandlesRepForTicker(String currencyTicker, long endDate) {
        CandlesRep candlesRep = new CandlesRep();
        candlesRep.setCurrencyTicker(currencyTicker);
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

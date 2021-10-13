package org.mposolda.mock;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.reps.CandlesRep;
import org.mposolda.reps.QuoteLoaderRep;
import org.mposolda.reps.finhub.CandleRep;
import org.mposolda.reps.finhub.CompanyProfileRep;
import org.mposolda.reps.finhub.CurrenciesRep;
import org.mposolda.reps.finhub.QuoteRep;
import org.mposolda.util.JsonUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MockFinnhubClient implements FinnhubHttpClient {

    private final String resourcesDir;

    private String lastStartDateUsed;
    private String lastEndDateUsed;

    public MockFinnhubClient(String resourcesDir) {
        this.resourcesDir = resourcesDir;
    }

    @Override
    public CompanyProfileRep getCompanyProfile(String ticker) {
        return null;
    }

    @Override
    public QuoteRep getQuoteRep(QuoteLoaderRep quoteLoader, boolean retryIfNeeded) {
        return null;
    }

    @Override

    public CandleRep getStockCandle(QuoteLoaderRep quoteLoader, String startDate, String endDate) {
        return null;
    }

    @Override
    public CurrenciesRep getCurrencies() {

        File[] currencyCandles = new File(resourcesDir).listFiles((dir, fileName) ->

                fileName.startsWith(("cur_"))

        );

        Map<String, Double> quotes = new HashMap<>();
        for (File currencyCandleFile : currencyCandles) {
            CandlesRep candles = JsonUtil.loadFileToJson(currencyCandleFile.getAbsolutePath(), CandlesRep.class);
            String currencyTicker = candles.getCurrencyTicker();
            CandlesRep.CandleRep lastCandle = candles.getCandles().get(candles.getCandles().size() - 1);
            quotes.put(currencyTicker, lastCandle.getValue());
        }

        CurrenciesRep currenciesRep = new CurrenciesRep();
        currenciesRep.setQuote(quotes);
        return currenciesRep;
    }

    @Override
    public CandleRep getCurrencyCandle(String targetCurrencyTicker, String startDate, String endDate) {
        // Load dummy currency candle from the file
        String file = resourcesDir + "/candles_" + targetCurrencyTicker + "_" + startDate + "_" + endDate + ".json";
        if (!new File(file).exists()) {
            throw new IllegalArgumentException("File does not exists " + file);
        }

        this.lastStartDateUsed = startDate;
        this.lastEndDateUsed = endDate;

        return JsonUtil.loadFileToJson(file, CandleRep.class);
    }

    @Override
    public void close() throws IOException {

    }

    public String getLastStartDateUsed() {
        return lastStartDateUsed;
    }

    public String getLastEndDateUsed() {
        return lastEndDateUsed;
    }

    public void cleanupLastDatesUsed() {
        this.lastStartDateUsed = null;
        this.lastEndDateUsed = null;
    }
}

package org.mposolda.test;

import java.io.File;
import java.io.IOException;

import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.reps.finhub.CandleRep;
import org.mposolda.reps.finhub.CompanyProfileRep;
import org.mposolda.reps.finhub.CurrenciesRep;
import org.mposolda.reps.finhub.QuoteRep;
import org.mposolda.util.JsonUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MockFinhubClient implements FinnhubHttpClient {

    private final String targetDir;

    public MockFinhubClient(String targetDir) {
        this.targetDir = targetDir;
    }

    @Override
    public CompanyProfileRep getCompanyProfile(String ticker) {
        return null;
    }

    @Override
    public QuoteRep getQuoteRep(String ticker) {
        return null;
    }

    @Override
    public CandleRep getStockCandle(String ticker, String startDate, String endDate) {
        return null;
    }

    @Override
    public CurrenciesRep getCurrencies() {
        return null;
    }

    @Override
    public CandleRep getCurrencyCandle(String targetCurrencyTicker, String startDate, String endDate) {
        // Load dummy currency candle from the file
        String file = targetDir + "/candles_" + targetCurrencyTicker + "_" + startDate + "_" + endDate;
        if (!new File(file).exists()) {
            throw new IllegalArgumentException("File does not exists " + file);
        }

        return JsonUtil.loadFileToJson(file, CandleRep.class);
    }

    @Override
    public void close() throws IOException {

    }


}

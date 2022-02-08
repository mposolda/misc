package org.mposolda.client;

import java.io.Closeable;
import java.util.List;

import org.mposolda.reps.QuoteLoaderRep;
import org.mposolda.reps.finhub.CompanyProfileRep;
import org.mposolda.reps.finhub.CurrenciesRep;
import org.mposolda.reps.finhub.QuoteRep;
import org.mposolda.reps.finhub.CandleRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface FinnhubHttpClient extends Closeable {

    CompanyProfileRep getCompanyProfile(String ticker);

    /**
     *
     * @param company
     * @param maxAttempts how many retries we try in case that download fails
     * @return
     */
    QuoteRep getQuoteRep(QuoteLoaderRep company, int maxAttempts);

    /**
     * Dates are in the format like "2020-10-20"
     */
    CandleRep getStockCandle(QuoteLoaderRep company, String startDate, String endDate);

    CurrenciesRep getCurrencies(List<String> currencies);

    /**
     * Get currency candles from EUR to the target currency (How much "currency" amount is needed for 1 EUR)
     *
     * Dates are in the format like "2020-10-20"
     *
     * @param targetCurrencyTicker
     * @param startDate
     * @param endDate
     * @return
     */
    CandleRep getCurrencyCandle(String targetCurrencyTicker, String startDate, String endDate);
}

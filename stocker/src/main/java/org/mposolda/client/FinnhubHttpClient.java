package org.mposolda.client;

import java.io.Closeable;

import org.mposolda.reps.finhub.CompanyProfileRep;
import org.mposolda.reps.finhub.CurrenciesRep;
import org.mposolda.reps.finhub.QuoteRep;
import org.mposolda.reps.finhub.StockCandleRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface FinnhubHttpClient extends Closeable {

    CompanyProfileRep getCompanyProfile(String ticker);

    QuoteRep getQuoteRep(String ticker);

    /**
     * Dates are in the format like "2020-10-20"
     */
    StockCandleRep getStockCandle(String ticker, String startDate, String endDate);

    CurrenciesRep getCurrencies();
}

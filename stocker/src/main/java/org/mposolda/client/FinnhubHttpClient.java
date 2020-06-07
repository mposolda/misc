package org.mposolda.client;

import org.mposolda.reps.finhub.CompanyProfileRep;
import org.mposolda.reps.finhub.CurrenciesRep;
import org.mposolda.reps.finhub.QuoteRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface FinnhubHttpClient {

    CompanyProfileRep getCompanyProfile(String ticker);

    QuoteRep getQuoteRep(String ticker);

    CurrenciesRep getCurrencies();
}

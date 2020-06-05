package org.mposolda.client;

import org.mposolda.reps.rest.CompanyProfileRep;
import org.mposolda.reps.rest.CurrenciesRep;
import org.mposolda.reps.rest.QuoteRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface FinnhubHttpClient {

    CompanyProfileRep getCompanyProfile(String ticker);

    QuoteRep getQuoteRep(String ticker);

    CurrenciesRep getCurrencies();
}

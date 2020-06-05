package org.mposolda.client;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.client.HttpClient;
import org.mposolda.reps.rest.CompanyProfileRep;
import org.mposolda.reps.rest.CurrenciesRep;
import org.mposolda.reps.rest.QuoteRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FinnhubHttpClientImpl implements FinnhubHttpClient {

    private static final String URL_PREFIX = "https://finnhub.io/api/v1";

    private final String token;
    private final HttpClient httpClient;

    public FinnhubHttpClientImpl(String token) {
        this.token = token;
        this.httpClient = new HttpClientBuilder().build();
    }

    @Override
    public CompanyProfileRep getCompanyProfile(String ticker) {
        try {
            String url = URL_PREFIX + "/stock/profile2?symbol=" + ticker + "&token=" + token;
            return SimpleHttp.doGet(url, httpClient)
                    .asJson(new TypeReference<CompanyProfileRep>() {
                    });
        } catch (IOException ioe) {
            throw new RuntimeException("Exception getting company profile for " + ticker, ioe);
        }
    }

    @Override
    public QuoteRep getQuoteRep(String ticker) {
        try {
            String url = URL_PREFIX + "/quote?symbol=" + ticker + "&token=" + token;
            return SimpleHttp.doGet(url, httpClient)
                    .asJson(new TypeReference<QuoteRep>() {
                    });
        } catch (IOException ioe) {
            throw new RuntimeException("Exception getting quote rep for " + ticker, ioe);
        }
    }

    @Override
    public CurrenciesRep getCurrencies() {
        try {
            String url = URL_PREFIX + "/forex/rates?token=" + token;
            return SimpleHttp.doGet(url, httpClient)
                    .asJson(new TypeReference<CurrenciesRep>() {
                    });
        } catch (IOException ioe) {
            throw new RuntimeException("Exception when loading currencies", ioe);
        }
    }
}

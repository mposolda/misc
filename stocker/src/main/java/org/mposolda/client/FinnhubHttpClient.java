package org.mposolda.client;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.client.HttpClient;
import org.mposolda.reps.rest.CompanyProfileRep;
import org.mposolda.reps.rest.QuoteRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FinnhubHttpClient {

    private static final String URL_PREFIX = "https://finnhub.io/api/v1";

    private final String token;
    private final HttpClient httpClient;

    public FinnhubHttpClient(String token) {
        this.token = token;
        this.httpClient = new HttpClientBuilder().build();
    }

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
}

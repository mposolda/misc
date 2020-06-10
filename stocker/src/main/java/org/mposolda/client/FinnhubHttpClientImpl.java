package org.mposolda.client;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.client.HttpClient;
import org.jboss.logging.Logger;
import org.mposolda.reps.finhub.CompanyProfileRep;
import org.mposolda.reps.finhub.CurrenciesRep;
import org.mposolda.reps.finhub.QuoteRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FinnhubHttpClientImpl implements FinnhubHttpClient {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

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
            log.infof("Loading company profile: %s", ticker);
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
            log.infof("Loading quote for company: %s", ticker);
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

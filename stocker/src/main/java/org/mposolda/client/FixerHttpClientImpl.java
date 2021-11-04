package org.mposolda.client;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jboss.logging.Logger;
import org.mposolda.reps.QuoteLoaderRep;
import org.mposolda.reps.finhub.CandleRep;
import org.mposolda.reps.finhub.CompanyProfileRep;
import org.mposolda.reps.finhub.CurrenciesRep;
import org.mposolda.reps.finhub.CurrencyCandlesRep;
import org.mposolda.reps.finhub.QuoteRep;
import org.mposolda.services.Services;

/**
 * Wrapper around http://data.fixer.io client
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FixerHttpClientImpl implements FinnhubHttpClient {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    private static final String URL_PREFIX = "http://data.fixer.io/api/";

    private final String token;
    private final CloseableHttpClient httpClient;

    public FixerHttpClientImpl() {
        this.token = Services.instance().getConfig().getFixerToken();
        this.httpClient = new HttpClientBuilder().build();
    }

    @Override
    public CompanyProfileRep getCompanyProfile(String ticker) {
        throw new UnsupportedOperationException("This is not supported for Fixer client");
    }

    @Override
    public QuoteRep getQuoteRep(QuoteLoaderRep company, boolean retryIfFailure) {
        throw new UnsupportedOperationException("This is not supported for Fixer client");
    }

    @Override
    public CandleRep getStockCandle(QuoteLoaderRep company, String startDate, String endDate) {
        throw new UnsupportedOperationException("This is not supported for Fixer client");
    }

    @Override
    public CurrenciesRep getCurrencies(List<String> currencies) {
        try {
            String currencySymbols = getCurrenciesStringFromList(currencies);

            String url = URL_PREFIX + "latest?format=1&access_key=" + this.token + "&symbols=" + currencySymbols;
            return SimpleHttp.doGet(url, httpClient)
                    .asJson(new TypeReference<CurrenciesRep>() {
                    });
        } catch (IOException ioe) {
            throw new RuntimeException("Exception when loading currencies from fixer", ioe);
        }
    }

    @Override
    public CurrencyCandlesRep getCurrencyCandles(List<String> targetCurrenciesTickers, String startDate, String endDate) {
        try {
            log.infof("Loading currency candles from %s to %s", startDate, endDate);
            String currencySymbols = getCurrenciesStringFromList(targetCurrenciesTickers);

            String url = URL_PREFIX + "timeseries?start_date=" + startDate + "&end_date=" + endDate + "&access_key=" + this.token
                    + "&base=EUR&symbols=" + currencySymbols;
            return SimpleHttp.doGet(url, httpClient)
                    .asJson(new TypeReference<CurrencyCandlesRep>() {
                    });
        } catch (IOException ioe) {
            throw new RuntimeException("Exception when loading currency candles from fixer", ioe);
        }
    }

    // Returns list like "USD,PLN,AUD" from the list of those 3 currencies
    private String getCurrenciesStringFromList(List<String> currencies) {
        // Dummy but ok for now...
        String currencySymbols = "";
        int counter = 0;
        for (String cur : currencies) {
            if ("EUR".equals(cur)) continue;
            counter += 1;
            if (counter == 1) {
                currencySymbols += cur;
            } else {
                currencySymbols += "," + cur;
            }
        }
        return currencySymbols;
    }

    @Override
    public void close() throws IOException {
        log.info("Closing Fixer HTTP Client");
        httpClient.close();
    }
}

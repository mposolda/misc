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
import org.mposolda.reps.finhub.QuoteRep;
import org.mposolda.services.Services;

/**
 * Wrapper around http://data.fixer.io client
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FixerHttpClientImpl implements FinnhubHttpClient {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    private static final String URL_PREFIX = "http://data.fixer.io/api/latest";

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
    public QuoteRep getQuoteRep(QuoteLoaderRep company, int maxAttempts) {
        throw new UnsupportedOperationException("This is not supported for Fixer client");
    }

    @Override
    public CandleRep getStockCandle(QuoteLoaderRep company, String startDate, String endDate) {
        throw new UnsupportedOperationException("This is not supported for Fixer client");
    }

    @Override
    public CurrenciesRep getCurrencies(List<String> currencies) {
        try {
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

            String url = URL_PREFIX + "?format=1&access_key=" + this.token + "&symbols=" + currencySymbols;
            return SimpleHttp.doGet(url, httpClient)
                    .asJson(new TypeReference<CurrenciesRep>() {
                    });
        } catch (IOException ioe) {
            throw new RuntimeException("Exception when loading currencies from fixer", ioe);
        }
    }

    @Override
    public CandleRep getCurrencyCandle(String targetCurrencyTicker, String startDate, String endDate) {
        throw new UnsupportedOperationException("This is not supported for Fixer client");
    }

    @Override
    public void close() throws IOException {
        log.info("Closing Fixer HTTP Client");
        httpClient.close();
    }
}

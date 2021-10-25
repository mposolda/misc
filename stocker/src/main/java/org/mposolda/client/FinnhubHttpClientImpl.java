package org.mposolda.client;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jboss.logging.Logger;
import org.mposolda.reps.QuoteLoaderRep;
import org.mposolda.reps.finhub.CompanyProfileRep;
import org.mposolda.reps.finhub.CurrenciesRep;
import org.mposolda.reps.finhub.QuoteRep;
import org.mposolda.reps.finhub.CandleRep;
import org.mposolda.services.Services;
import org.mposolda.util.DateUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FinnhubHttpClientImpl implements FinnhubHttpClient {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    private static final String URL_PREFIX = "https://finnhub.io/api/v1";

    private final String token;
    private final CloseableHttpClient httpClient;

    public FinnhubHttpClientImpl() {
        this.token = Services.instance().getConfig().getFinnhubToken();
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
    public QuoteRep getQuoteRep(QuoteLoaderRep quoteLoader, boolean retryIfNeeded) {
        String ticker = quoteLoader.getTicker();
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
    public CandleRep getStockCandle(QuoteLoaderRep quoteLoaderRep, String startDate, String endDate) {
        String ticker = quoteLoaderRep.getTicker();
        try {
            log.infof("Loading stock candles for company: %s. From %s to %s", ticker, startDate, endDate);

            long start = DateUtil.dateToNumberSeconds(startDate);
            long end = DateUtil.dateToNumberSeconds(endDate);

            log.infof("Timestamps %d to %d", start, end);

            String url = URL_PREFIX + "/stock/candle?symbol=" + ticker + "&resolution=D&from=" + start + "&to=" + end + "&token=" + token;
            return SimpleHttp.doGet(url, httpClient)
                    .asJson(new TypeReference<CandleRep>() {
                    });
        } catch (IOException ioe) {
            throw new RuntimeException("Exception getting stock candles rep for " + ticker, ioe);
        }
    }

    @Override
    public CurrenciesRep getCurrencies(List<String> sourceCurrencies) {
        throw new UnsupportedOperationException("This is not supported as it is payed API for Finnhub");
//        try {
//            String url = URL_PREFIX + "/forex/rates?token=" + token;
//            return SimpleHttp.doGet(url, httpClient)
//                    .asJson(new TypeReference<CurrenciesRep>() {
//                    });
//        } catch (IOException ioe) {
//            throw new RuntimeException("Exception when loading currencies", ioe);
//        }
    }

    @Override
    public CandleRep getCurrencyCandle(String targetCurrencyTicker, String startDate, String endDate) {
        try {
            log.infof("Loading currency candles from EUR to %s. From %s to %s", targetCurrencyTicker, startDate, endDate);

            long start = DateUtil.dateToNumberSeconds(startDate);
            long end = DateUtil.dateToNumberSeconds(endDate);

            log.infof("Timestamps %d to %d", start, end);

            String url = URL_PREFIX + "/forex/candle?symbol=OANDA:EUR_" + targetCurrencyTicker + "&resolution=D&from=" + start + "&to=" + end + "&token=" + token;
            return SimpleHttp.doGet(url, httpClient)
                    .asJson(new TypeReference<CandleRep>() {
                    });
        } catch (IOException ioe) {
            throw new RuntimeException("Exception getting currency candles rep for " + targetCurrencyTicker, ioe);
        }
    }

    @Override
    public void close() throws IOException {
        log.info("Closing Finnhub HTTP Client");
        httpClient.close();
    }
}

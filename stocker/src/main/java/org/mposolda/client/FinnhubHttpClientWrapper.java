package org.mposolda.client;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jboss.logging.Logger;
import org.mposolda.reps.QuoteLoaderRep;
import org.mposolda.reps.finhub.CompanyProfileRep;
import org.mposolda.reps.finhub.CurrenciesRep;
import org.mposolda.reps.finhub.QuoteRep;
import org.mposolda.reps.finhub.CandleRep;
import org.mposolda.util.NumberUtil;
import org.mposolda.util.FinhubOutputUtil;
import org.mposolda.util.WaitUtil;

/**
 * Just implements some "quotes" to not call Finhub API in big speed - like 10 calls in 1 second
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FinnhubHttpClientWrapper implements FinnhubHttpClient {

    private static final Logger log = Logger.getLogger(FinnhubHttpClientWrapper.class);

    // Max count of attempts for every HTTP request sent to Finnhub
    private static final int MAX_ATTEMPTS = 10;

    private final FinnhubHttpClient delegate;

    private long lastCallTimeMs;

    public FinnhubHttpClientWrapper(FinnhubHttpClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public CompanyProfileRep getCompanyProfile(String ticker) {
        return waitAndCall(ticker, delegate::getCompanyProfile);
    }

    @Override
    public QuoteRep getQuoteRep(QuoteLoaderRep quoteLoader, boolean retryIfNeeded) {
        int maxAttempts = retryIfNeeded ? MAX_ATTEMPTS : 1;
        QuoteRep result = retry(
                       quoteLoader,
                        quoteLoader2 -> {
                           return waitAndCall(quoteLoader2, (quoteLoaderr) -> delegate.getQuoteRep(quoteLoaderr, false));
                       },
                       quoteRep -> {
                           return !NumberUtil.isZero(quoteRep.getCurrentPrice());
                       },
                maxAttempts,
                       "getQuoteRep");

        return FinhubOutputUtil.convertQuoteRep(quoteLoader, result);
    }

    @Override
    public CandleRep getStockCandle(QuoteLoaderRep quoteLoader, String startDate, String endDate) {
        CandleRep result = waitAndCall(quoteLoader, quoteLoader2 -> {
            return delegate.getStockCandle(quoteLoader2, startDate, endDate);
        });

        return FinhubOutputUtil.convertCandleRep(quoteLoader, result);
    }

    @Override
    public CurrenciesRep getCurrencies() {
        return waitAndCall(null, (str) -> delegate.getCurrencies());
    }

    @Override
    public CandleRep getCurrencyCandle(String targetCurrencyTicker, String startDate, String endDate) {
        return waitAndCall(targetCurrencyTicker, ticker2 -> {
            return delegate.getCurrencyCandle(ticker2, startDate, endDate);
        });
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    private <INPUT, OUTPUT> OUTPUT waitAndCall(INPUT input, Function<INPUT, OUTPUT> function) {
        waitUntilCanCall();
        OUTPUT o = function.apply(input);
        lastCallTimeMs = System.currentTimeMillis();
        return o;
    }

    // Retry particular "function" until the predicate is met OR until the maximum count of attempts is met
    private <INPUT, OUTPUT> OUTPUT retry(INPUT input, Function<INPUT, OUTPUT> function, Predicate<OUTPUT> predicate,
                                         int maxAttempts, String operationName) {
        int attempts = maxAttempts;
        while (attempts > 0) {
            OUTPUT output = function.apply(input);
            if (predicate.test(output)) {
                return output;
            } else {
                attempts -= 1;
                if (attempts > 0) {
                    log.infof("Predicate failed when run operation '%s' for input '%s'. Will try another attempt", operationName, input);
                } else {
                    log.warnf("Unable to run operation '%s' for input '%s' for the maximum count of %d attempts", operationName, input, maxAttempts);
                    return output;
                }
            }
        }

        // Should not happen
        return null;
    }

    private void waitUntilCanCall() {
        while (!canCall()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                // TODO.. Improve
                throw new RuntimeException(ie);
            }
        }
    }

    private boolean canCall() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastCallTimeMs > WaitUtil.INTERVAL);
    }
}

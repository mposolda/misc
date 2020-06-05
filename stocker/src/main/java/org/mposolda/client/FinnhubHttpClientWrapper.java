package org.mposolda.client;

import org.mposolda.reps.rest.CompanyProfileRep;
import org.mposolda.reps.rest.CurrenciesRep;
import org.mposolda.reps.rest.QuoteRep;

/**
 * Just implements some "quotes" to not call Finhub API in big speed - like 10 calls in 1 second
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FinnhubHttpClientWrapper implements FinnhubHttpClient {

    // Interval in miliseconds, which should be always spent among 2 calls
    private static final long INTERVAL = 2000;

    private final FinnhubHttpClient delegate;

    private long lastCallTimeMs;

    public FinnhubHttpClientWrapper(FinnhubHttpClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public CompanyProfileRep getCompanyProfile(String ticker) {
        waitUntilCanCall();
        return delegate.getCompanyProfile(ticker);
    }

    @Override
    public QuoteRep getQuoteRep(String ticker) {
        waitUntilCanCall();
        return delegate.getQuoteRep(ticker);
    }

    @Override
    public CurrenciesRep getCurrencies() {
        waitUntilCanCall();
        return delegate.getCurrencies();
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
        return (currentTime - lastCallTimeMs > INTERVAL);
    }
}

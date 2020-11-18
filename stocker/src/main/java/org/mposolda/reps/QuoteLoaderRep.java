package org.mposolda.reps;

/**
 * Contract for loading company quotes or company candles or currency candles
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface QuoteLoaderRep {

    String getTicker();

    Integer getCurrencyFromQuoteRatio();

    static QuoteLoaderRep fromTicker(String ticker) {
        return new QuoteLoaderRep() {

            @Override
            public String getTicker() {
                return ticker;
            }

            @Override
            public Integer getCurrencyFromQuoteRatio() {
                return null;
            }
        };
    }
}

package org.mposolda.util;

import java.util.ArrayList;
import java.util.List;

import org.mposolda.reps.QuoteLoaderRep;
import org.mposolda.reps.finhub.CandleRep;
import org.mposolda.reps.finhub.QuoteRep;

/**
 * Format (decorate) some output from finnhub client
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FinhubOutputUtil {

    public static QuoteRep convertQuoteRep(QuoteLoaderRep company, QuoteRep quote) {
        if (company.getCurrencyFromQuoteRatio() != null) {
            int r = company.getCurrencyFromQuoteRatio();
            quote.setCurrentPrice(quote.getCurrentPrice() / r);
            quote.setHighDayPrice(quote.getHighDayPrice() / r);
            quote.setLowDayPrice(quote.getLowDayPrice() / r);
            quote.setOpenDayPrice(quote.getOpenDayPrice() / r);
            quote.setPreviousClosePrice(quote.getPreviousClosePrice() / r);
        }
        return quote;
    }

    public static CandleRep convertCandleRep(QuoteLoaderRep company, CandleRep candle) {
        if (company.getCurrencyFromQuoteRatio() != null) {
            int r = company.getCurrencyFromQuoteRatio();
            candle.setCurrentPrice(convertCandlePrices(candle.getCurrentPrice(), r));
            candle.setHighDayPrice(convertCandlePrices(candle.getHighDayPrice(), r));
            candle.setLowDayPrice(convertCandlePrices(candle.getLowDayPrice(), r));
            candle.setOpenDayPrice(convertCandlePrices(candle.getOpenDayPrice(), r));
        }
        return candle;
    }

    private static List<Double> convertCandlePrices(List<Double> old, int ratio) {
        List<Double> out = new ArrayList<>();
        for (Double d : old) {
            out.add(d / ratio);
        }
        return out;
    }
}

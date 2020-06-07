package org.mposolda.services;

import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.reps.finhub.CurrenciesRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CurrencyConvertor {

    private final FinnhubHttpClient finhubClient;
    private CurrenciesRep currencies;

    CurrencyConvertor(FinnhubHttpClient finhubClient) {
        this.finhubClient = finhubClient;
    }

    void start() {
        currencies = finhubClient.getCurrencies();
    }

    public double exchangeMoney(double currencyFromAmount, String currencyFrom, String currencyTo) {
        double oneEuroToCurrencyFrom = getOneEuroToCurrency(currencyFrom);

        double oneEuroToCurrencyTo = getOneEuroToCurrency(currencyTo);

        return currencyFromAmount * oneEuroToCurrencyTo / oneEuroToCurrencyFrom;
    }

    private double getOneEuroToCurrency(String currencySymbol) {
        if ("EUR".equals(currencySymbol)) {
            return 1;
        } else {
            Double result = currencies.getQuote().get(currencySymbol);
            if (result == null) {
                throw new IllegalArgumentException("Cant find currency with symbol: " + currencySymbol);
            }
            return result;
        }
    }

}

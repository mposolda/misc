package org.mposolda.services;

import java.util.ArrayList;
import java.util.List;

import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.reps.finhub.CurrenciesRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CurrencyConvertor {

    private final FinnhubHttpClient finhubClient;
    private final List<String> currencyTickers;
    private CurrenciesRep currencies;


    CurrencyConvertor(FinnhubHttpClient finhubClient, List<String> currencyTickers) {
        this.finhubClient = finhubClient;
        this.currencyTickers = currencyTickers;
    }

    void start() {
        this.currencies = finhubClient.getCurrencies(currencyTickers);
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
            Double result = currencies.getRates().get(currencySymbol);
            if (result == null) {
                throw new IllegalArgumentException("Cant find currency with symbol: " + currencySymbol);
            }
            return result;
        }
    }

}

package org.mposolda.services;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import org.mposolda.reps.CurrencyRep;
import org.mposolda.reps.DatabaseRep;
import org.mposolda.reps.QuoteLoaderRep;
import org.mposolda.reps.RateOfReturnsRep;
import org.mposolda.reps.finhub.QuoteRep;
import org.mposolda.reps.rest.CompaniesRep;
import org.mposolda.reps.rest.CurrenciesRep;
import org.mposolda.reps.rest.CurrencyFullRep;
import org.mposolda.util.DateUtil;
import org.mposolda.util.JsonUtil;
import org.mposolda.util.returner.RateOfReturnInput;
import org.mposolda.util.returner.RateOfReturnUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RateOfReturnsManager {

    protected static final Logger log = Logger.getLogger(RateOfReturnsManager.class);

    private final String  companiesJsonFileLocation;
    private RateOfReturnsRep rateOfReturnsRep;

    public RateOfReturnsManager() {
        this.companiesJsonFileLocation = Services.instance().getConfig().getCompaniesJsonFileLocation();
    }

    public void start() {
        rateOfReturnsRep = new RateOfReturnsRep();

        // Load company informations from JSON file
        DatabaseRep database = JsonUtil.loadDatabase(this.companiesJsonFileLocation);
        log.infof("Database loaded");

        // Find current total value of companies and currencies in CZK
        CompaniesRep companies = Services.instance().getCompanyInfoManager().getCompanies();
        CurrenciesRep currencies = Services.instance().getCompanyInfoManager().getCurrencies();
        double totalValue = companies.getCurrentPriceOfAllStocksInHoldCZK() + currencies.getPriceInHoldCZK();

        // Find all CZK deposits
        CurrencyRep czkCurrency = database.getCurrencies().stream()
                .filter(currencyRep -> "CZK".equals(currencyRep.getTicker()))
                .findFirst().orElseThrow();
        List<RateOfReturnInput.Deposit> deposits = czkCurrency.getDeposits().stream()
                .map(deposit -> {
                    return new RateOfReturnInput.Deposit(DateUtil.dateToNumber(deposit.getDate()), deposit.getCountBought());
                }).toList();

        // Compute rateOfReturn per year
        RateOfReturnInput input = new RateOfReturnInput(System.currentTimeMillis(), totalValue, deposits.toArray(new RateOfReturnInput.Deposit[0]));
        double rateOfReturnInPercent = RateOfReturnUtil.computeYearRateOfReturn(input);
        rateOfReturnsRep.setRateOfReturnPerYear(rateOfReturnInPercent);

        // Compute absolute rateOfReturn
        double absoluteRateOfReturnInPercent = ((totalValue / currencies.getDepositTotalInCZK()) - 1) * 100;
        rateOfReturnsRep.setRateOfReturnAbsolute(absoluteRateOfReturnInPercent);

        double currentCzkToUsdQuotation = getQuotationCzkToUsd(currencies);

        // Berkshire
        double rateOfReturnBerkshire = computeBenchmark(czkCurrency, currentCzkToUsdQuotation, deposits,
                deposit -> deposit.getBenchmarks().getBerkshireBPrice(),
                () -> {
                    QuoteLoaderRep quoteLoader = QuoteLoaderRep.fromTicker("BRK.B");
                    QuoteRep quote = Services.instance().getFinhubClient().getQuoteRep(quoteLoader, 3);
                    double berkshirePrice = quote.getCurrentPrice();
                    log.infof("Berkshire B price: %s", berkshirePrice);
                    return berkshirePrice;
                });
        rateOfReturnsRep.setRateOfReturnPerYearBerkshire(rateOfReturnBerkshire);

        // Markel
        double rateOfReturnMarkel = computeBenchmark(czkCurrency, currentCzkToUsdQuotation, deposits,
                deposit -> deposit.getBenchmarks().getMarkelPrice(),
                () -> {
                    QuoteLoaderRep quoteLoader = QuoteLoaderRep.fromTicker("MKL");
                    QuoteRep quote = Services.instance().getFinhubClient().getQuoteRep(quoteLoader, 3);
                    double markelPrice = quote.getCurrentPrice();
                    log.infof("Markel price: %s", markelPrice);
                    return markelPrice;
                });
        rateOfReturnsRep.setRateOfReturnPerYearMarkel(rateOfReturnMarkel);

        // TODO:mposolda S&P 500 and others...
    }

    public RateOfReturnsRep getRateOfReturnsRep() {
        if (rateOfReturnsRep == null) {
            throw new IllegalStateException("RateOfReturnsManager not started");
        }
        return rateOfReturnsRep;
    }

    private double computeBenchmark(CurrencyRep czkCurrency, double currentQuotationCzkToUsd, List<RateOfReturnInput.Deposit> deposits,
                                    Function<CurrencyRep.CurrencyDepositRep, Double> getStockPriceFromBenchmark, Supplier<Double> getCurrentBenchmarkPriceLoader) {
        double totalStocksBought = 0;
        for (CurrencyRep.CurrencyDepositRep deposit : czkCurrency.getDeposits()) {
            // Compute how much was the price of 1 unit of 3rd party stock (benchmark stock) at the time of deposit
            double thirdpartyStockPriceAtDeposit = getStockPriceFromBenchmark.apply(deposit);
            double czkToUsd = deposit.getBenchmarks().getCzkToUsd();
            double depositCzk = deposit.getCountBought();

            double pricePerStockCZK = czkToUsd * thirdpartyStockPriceAtDeposit;
            double stocksBought = depositCzk / pricePerStockCZK;
            totalStocksBought += stocksBought;
        }

        // Compute current thirdparty price
        double currentPrice = getCurrentBenchmarkPriceLoader.get();
        double totalThirdpartyInUSD = currentPrice * totalStocksBought;
        double totalThirdpartyInCZK = currentQuotationCzkToUsd * totalThirdpartyInUSD;

        RateOfReturnInput input2 = new RateOfReturnInput(System.currentTimeMillis(), totalThirdpartyInCZK, deposits.toArray(new RateOfReturnInput.Deposit[0]));
        double targetRateOfReturn = RateOfReturnUtil.computeYearRateOfReturn(input2);
        return targetRateOfReturn;
    }

    private double getQuotationCzkToUsd(CurrenciesRep currencies) {
        CurrencyFullRep usdCurrency = currencies.getCurrencies().stream()
                .filter(currencyFullRep -> "USD".equals(currencyFullRep.getTicker()))
                .findFirst().orElseThrow(() -> new IllegalStateException("No USD currency found"));
        return usdCurrency.getQuotation();
    }
}

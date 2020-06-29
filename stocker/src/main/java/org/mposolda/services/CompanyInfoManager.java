package org.mposolda.services;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.reps.CurrencyRep;
import org.mposolda.reps.DatabaseRep;
import org.mposolda.reps.rest.CompaniesRep;
import org.mposolda.reps.rest.CompanyFullRep;
import org.mposolda.reps.CompanyRep;
import org.mposolda.reps.ExpectedBackflowRep;
import org.mposolda.reps.PurchaseRep;
import org.mposolda.reps.finhub.QuoteRep;
import org.mposolda.reps.rest.CurrenciesRep;
import org.mposolda.reps.rest.CurrencyFullRep;
import org.mposolda.util.JsonUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CompanyInfoManager {

    private final FinnhubHttpClient finhubClient;

    private final CurrencyConvertor currencyConvertor;

    private final String  companiesJsonFileLocation;

    private final CompaniesRep companies = new CompaniesRep();

    private final CurrenciesRep currencies = new CurrenciesRep();

    CompanyInfoManager(FinnhubHttpClient finhubClient, CurrencyConvertor currencyConvertor, String companiesJsonFileLocation) {
        this.finhubClient = finhubClient;
        this.currencyConvertor = currencyConvertor;
        this.companiesJsonFileLocation = companiesJsonFileLocation;
    }

    void start() {
        // Load company informations from JSON file
        DatabaseRep database = JsonUtil.loadDatabase(this.companiesJsonFileLocation);

        // Load company informations with HTTP client and compute rest of them
        List<CompanyFullRep> fullCompanies = computeCompanies(database.getCompanies());
        this.companies.setCompanies(fullCompanies);
        this.companies.setFinished(true);

        // Load company informations with HTTP client and compute rest of them
        List<CurrencyFullRep> fullCurrencies = computeCurrencies(database.getCurrencies());
        this.currencies.setCurrencies(fullCurrencies);

        // Total deposit is based on the computation in PurchaseManager
        PurchaseManager purchaseManager = Services.instance().getPurchaseManager();
        PurchaseManager.CurrenciesInfo currenciesInfo = purchaseManager.getCurrenciesInfo();
        this.currencies.setDepositTotalInCZK(currenciesInfo.getCzkDepositsTotal());
        this.currencies.setTotalFeesCZK(currenciesInfo.getCzkFeesTotal());
        this.currencies.setFinished(true);
    }

    public CompaniesRep getCompanies() {
        return companies;
    }

    public CurrenciesRep getCurrencies() {
        return currencies;
    }

    private List<CompanyFullRep> computeCompanies(List<CompanyRep> companies) {
        return companies.stream()
                .map(companyRep -> {

                    CompanyFullRep company = computeCompanyFull(companyRep);

                    return company;

                })
                .collect(Collectors.toList());
    }

    private CompanyFullRep computeCompanyFull(CompanyRep company) {
        CompanyFullRep result = new CompanyFullRep(company);

        QuoteRep quote = finhubClient.getQuoteRep(company.getTicker());

        // Using open-day price for now. Using current price returned strange results for some companies (EG. 1.03 instead of 1.3)
        // TODO:mposolda figure the issue if possible and eventually replace with current price
        double currentPrice = quote.getCurrentPrice();

        result.setCurrentStockPrice(currentPrice);
        int totalStocksInHold = 0;
        double totalPricePayed = 0;

        // TODO:mposolda For now, use just always first expected backflow
        ExpectedBackflowRep expectedBackflow = company.getExpectedBackflows().get(0);

        List<CompanyFullRep.PurchaseFull> purchases = new LinkedList<>();
        for (PurchaseRep purchase : company.getPurchases()) {
            totalStocksInHold += purchase.getStocksCount();
            totalPricePayed += (purchase.getPricePerStock() * purchase.getStocksCount());
            int expectedBackflowInPercent = (int) Math.round((expectedBackflow.getPrice() * expectedBackflow.getBackflowInPercent()) / purchase.getPricePerStock());

            CompanyFullRep.PurchaseFull purchaseFull = new CompanyFullRep.PurchaseFull(purchase);
            purchaseFull.setExpectedBackflowInPercent(expectedBackflowInPercent);
            purchases.add(purchaseFull);
        }

        double expectedBackflowInPercentRightNow = (expectedBackflow.getPrice() * expectedBackflow.getBackflowInPercent()) / currentPrice;
        result.setExpectedYearBackflowInPercentRightNow(expectedBackflowInPercentRightNow);

        result.setTotalStocksInHold(totalStocksInHold);
        result.setTotalPricePayed(totalPricePayed);
        result.setPurchasesFull(purchases);

        double currentPriceOfAllStocksInHold = totalStocksInHold * currentPrice;
        result.setCurrentPriceOfAllStocksInHold(currentPriceOfAllStocksInHold);

        double earning = currentPriceOfAllStocksInHold - totalPricePayed;
        result.setEarning(earning);

        // TODO:mposolda compute averageYearBackflowInPercent

        PurchaseManager purchaseManager = Services.instance().getPurchaseManager();
        PurchaseManager.CompanyPurchasesPrice companyPurchases = purchaseManager.getCompanyPurchases(company.getTicker());
        double totalPriceOfAllPurchasesCZK = companyPurchases==null ? 0 : companyPurchases.getTotalCZKPriceOfAllPurchases();

        // result.setTotalPricePayedCZK(currencyConvertor.exchangeMoney(result.getTotalPricePayed(), result.getCurrency(), "CZK"));
        result.setTotalPricePayedCZK(totalPriceOfAllPurchasesCZK);

        result.setCurrentPriceOfAllStocksInHoldCZK(currencyConvertor.exchangeMoney(result.getCurrentPriceOfAllStocksInHold(), result.getCurrency(), "CZK"));
        result.setEarningCZK(result.getCurrentPriceOfAllStocksInHoldCZK() - result.getTotalPricePayedCZK());

        double totalBackflowInPercent = ((result.getCurrentPriceOfAllStocksInHoldCZK() / totalPriceOfAllPurchasesCZK) - 1) * 100;
        result.setTotalBackflowInPercent(totalBackflowInPercent);

        return result;
    }

    private List<CurrencyFullRep> computeCurrencies(List<CurrencyRep> currencies) {
        List<CurrencyFullRep> result = currencies.stream()
                .map(currencyRep -> {

                    CurrencyFullRep currency = computeCurrencyFull(currencyRep);

                    return currency;

                })
                .collect(Collectors.toList());

        return result;
    }

    private CurrencyFullRep computeCurrencyFull(CurrencyRep currency) {
        CurrencyFullRep result = new CurrencyFullRep();
        result.setTicker(currency.getTicker());

        //double totalCountBought = 0;
        //double totalPrice = 0;

//        double investedToStocks = 0;
//        // Check how much money of particular currency we invested to stocks
//        for (CompanyFullRep company : companies.getCompanies()) {
//            if (company.getCurrency().equals(currency.getTicker())) {
//                investedToStocks += company.getTotalPricePayed();
//            }
//        }

        PurchaseManager purchaseManager = Services.instance().getPurchaseManager();
        PurchaseManager.CurrenciesInfo currenciesInfo = purchaseManager.getCurrenciesInfo();
        double inHold = currenciesInfo.getCurrencyRemainingAmount().get(currency.getTicker());

        double quotation = currencyConvertor.exchangeMoney(1, currency.getTicker(), "CZK");

        double priceInHoldCZK = currencyConvertor.exchangeMoney(inHold, currency.getTicker(), "CZK");

        //result.setBoughtTotal(totalCountBought);
        //result.setBoughtTotalPriceInCZK(totalPrice);
        //result.setInvestedTotal(investedToStocks);
        result.setTotalHold(inHold);
        result.setQuotation(quotation);
        result.setPriceInHoldCZK(priceInHoldCZK);

        return result;
    }

}

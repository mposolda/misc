package org.mposolda.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.client.JsonSerialization;
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
        DatabaseRep database = loadDatabase();

        // Load company informations with HTTP client and compute rest of them
        List<CompanyFullRep> fullCompanies = computeCompanies(database.getCompanies(), true);
        this.companies.setCompanies(fullCompanies);
        this.companies.setFinished(true);

        // Load company informations with HTTP client and compute rest of them
        List<CurrencyFullRep> fullCurrencies = computeCurrencies(database.getCurrencies());
        this.currencies.setCurrencies(fullCurrencies);
        this.currencies.setFinished(true);

        //System.out.println(fullCompanies);
    }

    public CompaniesRep getCompanies() {
        return companies;
    }

    public CurrenciesRep getCurrencies() {
        return currencies;
    }

    private DatabaseRep loadDatabase() {
        try {
            return JsonSerialization.readValue(new FileInputStream(companiesJsonFileLocation), DatabaseRep.class);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private List<CompanyFullRep> computeCompanies(List<CompanyRep> companies, boolean dump) {
        return companies.stream()
                .map(companyRep -> {

                    CompanyFullRep company = computeCompanyFull(companyRep);
                    if (dump) {
                        dumpCompany(company);
                    }

                    return company;

                })
                .collect(Collectors.toList());
    }

    private CompanyFullRep computeCompanyFull(CompanyRep company) {
        CompanyFullRep result = new CompanyFullRep(company);

        QuoteRep quote = finhubClient.getQuoteRep(company.getTicker());
        result.setCurrentStockPrice(quote.getCurrentPrice());
        int totalStocksInHold = 0;
        double totalPricePayed = 0;

        // TODO:mposolda For now, use just always first expected backflow
        ExpectedBackflowRep expectedBackflow = company.getExpectedBackflows().get(0);

        List<CompanyFullRep.PurchaseFull> purchases = new LinkedList<>();
        for (PurchaseRep purchase : company.getPurchases()) {
            totalStocksInHold += purchase.getStocksCount();
            totalPricePayed += (purchase.getPricePerStock() * totalStocksInHold);
            int expectedBackflowInPercent = (int) Math.round((expectedBackflow.getPrice() * expectedBackflow.getBackflowInPercent()) / purchase.getPricePerStock());

            CompanyFullRep.PurchaseFull purchaseFull = new CompanyFullRep.PurchaseFull(purchase);
            purchaseFull.setExpectedBackflowInPercent(expectedBackflowInPercent);
            purchases.add(purchaseFull);
        }

        double expectedBackflowInPercentRightNow = (expectedBackflow.getPrice() * expectedBackflow.getBackflowInPercent()) / quote.getCurrentPrice();
        result.setExpectedYearBackflowInPercentRightNow(expectedBackflowInPercentRightNow);

        result.setTotalStocksInHold(totalStocksInHold);
        result.setTotalPricePayed(totalPricePayed);
        result.setPurchasesFull(purchases);

        double currentPriceOfAllStocksInHold = totalStocksInHold * quote.getCurrentPrice();
        result.setCurrentPriceOfAllStocksInHold(currentPriceOfAllStocksInHold);

        double earning = currentPriceOfAllStocksInHold - totalPricePayed;
        result.setEarning(earning);

        double totalBackflowInPercent = ((currentPriceOfAllStocksInHold / totalPricePayed) - 1) * 100;
        result.setTotalBackflowInPercent(totalBackflowInPercent);

        // TODO:mposolda compute averageYearBackflowInPercent

        // TODO:mposolda this is not accurate. It should be based on latest currency purchase and not on the currencyConvertor price
        result.setTotalPricePayedCZK(currencyConvertor.exchangeMoney(result.getTotalPricePayed(), result.getCurrency(), "CZK"));

        result.setCurrentPriceOfAllStocksInHoldCZK(currencyConvertor.exchangeMoney(result.getCurrentPriceOfAllStocksInHold(), result.getCurrency(), "CZK"));
        result.setEarningCZK(result.getCurrentPriceOfAllStocksInHoldCZK() - result.getTotalPricePayedCZK());

        return result;
    }

    private List<CurrencyFullRep> computeCurrencies(List<CurrencyRep> currencies) {
        return currencies.stream()
                .map(currencyRep -> {

                    CurrencyFullRep currency = computeCurrencyFull(currencyRep);

                    return currency;

                })
                .collect(Collectors.toList());
    }

    private CurrencyFullRep computeCurrencyFull(CurrencyRep currency) {
        CurrencyFullRep result = new CurrencyFullRep();
        result.setTicker(currency.getTicker());

        double totalCountBought = 0;
        double totalPrice = 0;
        double totalFeesInCZK = 0;
        for (CurrencyRep.CurrencyPurchaseRep purchase : currency.getPurchases()) {
            totalCountBought += purchase.getCountBought();
            totalPrice += purchase.getCountBought() * purchase.getPricePerUnit();
            totalFeesInCZK += purchase.getFeeInCZK();
        }
        
        double investedToStocks = 0;
        // Check how much money of particular currency we invested to stocks
        for (CompanyFullRep company : companies.getCompanies()) {
            if (company.getCurrency().equals(currency.getTicker())) {
                investedToStocks += company.getTotalPricePayed();
            }
        }

        double inHold = totalCountBought - investedToStocks;

        double quotation = currencyConvertor.exchangeMoney(1, currency.getTicker(), "CZK");

        double priceInHoldCZK = currencyConvertor.exchangeMoney(inHold, currency.getTicker(), "CZK");

        result.setBoughtTotal(totalCountBought);
        result.setBoughtTotalPriceInCZK(totalPrice);
        result.setInvestedTotal(investedToStocks);
        result.setTotalHold(inHold);
        result.setTotalFeesInCZK(totalFeesInCZK);
        result.setQuotation(quotation);
        result.setPriceInHoldCZK(priceInHoldCZK);

        return result;
    }

    private void dumpCompany(CompanyFullRep company) {
        // TODO:mposolda
        // Nakup v EU byl zrejme za 27 EU za KC
        // Nakup v CZK byl zrejme za 24.
//        Firma: Sameour realty capital
//
//        Ticker: SHHH
//
//        Mena: USD
//
//        Cena akcie: 28.66 USD (zjisteno online)
//
//        Mnozstvi drzenych akcii: 100 (spocitano ze vsech koupi jako soucet)
//
//        Cena za nakup vsech akcii: 2358 USD (spocitano z koupi)
//
//        Cena vsech akcii: 2866 USD (spocitano jako soucin toho navrchu a toho dole)
//
//        Vydelek: 508 USD (Spocitano jako rozdil tech dvou vrchnich parametru)
//
//        Skutecna navratnost: 15% (prepocitano z jedne koupe zatim - musim vzit v uvahu jeste cas)
//
//        Ocekavana navratnost zadana:
//        6.3.2020: 16% pri cene 25.26 USD (zadano)
//        8.6.2021: 17% pri cene 28.75 USD (zadano)
//
//        Koupe:
//        2.6.2020 - 100 akcii za 23.58 USD (zadano)
//        Ocekavana navratnost, ktera byla pri koupi: 14% (spocitat)
//
//
//                Ocekavana navratnost pri aktualni cene akcie: 13% (spocitano z posledni ocekavane navratnosti a z momentalni ceny akcie)
    }
}

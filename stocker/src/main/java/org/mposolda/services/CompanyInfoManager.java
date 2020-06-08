package org.mposolda.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.client.JsonSerialization;
import org.mposolda.reps.rest.CompaniesRep;
import org.mposolda.reps.rest.CompanyFullRep;
import org.mposolda.reps.CompanyRep;
import org.mposolda.reps.ExpectedBackflowRep;
import org.mposolda.reps.PurchaseRep;
import org.mposolda.reps.finhub.QuoteRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CompanyInfoManager {

    private final FinnhubHttpClient finhubClient;

    private final String  companiesJsonFileLocation;

    private final CompaniesRep companies = new CompaniesRep();

    CompanyInfoManager(FinnhubHttpClient finhubClient, String companiesJsonFileLocation) {
        this.finhubClient = finhubClient;
        this.companiesJsonFileLocation = companiesJsonFileLocation;
    }

    void start() {
        // Load company informations from JSON file
        List<CompanyRep> companies = loadCompanies();

        // Load company informations with HTTP client and compute rest of them
        List<CompanyFullRep> fullCompanies = computeCompanies(companies, true);
        this.companies.setCompanies(fullCompanies);
        this.companies.setFinished(true);

        System.out.println(fullCompanies);
    }

    public CompaniesRep getCompanies() {
        return companies;
    }

    private List<CompanyRep> loadCompanies() {
        try {
            return JsonSerialization.readValue(new FileInputStream(companiesJsonFileLocation), new TypeReference<List<CompanyRep>>() {
            });
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

        // TODO: For now, use just always first expected backflow
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

        int expectedBackflowInPercentRightNow = (int) Math.round((expectedBackflow.getPrice() * expectedBackflow.getBackflowInPercent()) / quote.getCurrentPrice());
        result.setExpectedYearBackflowInPercentRightNow(expectedBackflowInPercentRightNow);

        result.setTotalStocksInHold(totalStocksInHold);
        result.setTotalPricePayed(totalPricePayed);
        result.setPurchasesFull(purchases);

        double currentPriceOfAllStocksInHold = totalStocksInHold * quote.getCurrentPrice();
        result.setCurrentPriceOfAllStocksInHold(currentPriceOfAllStocksInHold);

        double earning = currentPriceOfAllStocksInHold - totalPricePayed;
        result.setEarning(earning);

        int totalBackflowInPercent = (int) Math.round(((currentPriceOfAllStocksInHold / totalPricePayed) - 1) * 100);
        result.setTotalBackflowInPercent(totalBackflowInPercent);

        // TODO: compute averageYearBackflowInPercent

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

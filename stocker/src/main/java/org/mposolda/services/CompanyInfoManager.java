package org.mposolda.services;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.reps.CandlesRep;
import org.mposolda.reps.CurrencyRep;
import org.mposolda.reps.DatabaseRep;
import org.mposolda.reps.DisposalRep;
import org.mposolda.reps.rest.CompaniesRep;
import org.mposolda.reps.rest.CompanyFullRep;
import org.mposolda.reps.CompanyRep;
import org.mposolda.reps.ExpectedBackflowRep;
import org.mposolda.reps.PurchaseRep;
import org.mposolda.reps.finhub.QuoteRep;
import org.mposolda.reps.rest.CurrenciesRep;
import org.mposolda.reps.rest.CurrencyFullRep;
import org.mposolda.util.JsonUtil;
import org.mposolda.util.NumberUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CompanyInfoManager {

    protected final Logger log = Logger.getLogger(this.getClass().getName());


    private final FinnhubHttpClient finhubClient;

    private final CurrencyConvertor currencyConvertor;

    private final String  companiesJsonFileLocation;

    private final CompaniesRep companies = new CompaniesRep();

    private final CurrenciesRep currencies = new CurrenciesRep();

    private final CandlesHistoryManager candlesManager;

    CompanyInfoManager(FinnhubHttpClient finhubClient, CurrencyConvertor currencyConvertor, CandlesHistoryManager candlesManager) {
        this.finhubClient = finhubClient;
        this.currencyConvertor = currencyConvertor;
        this.companiesJsonFileLocation = Services.instance().getConfig().getCompaniesJsonFileLocation();
        this.candlesManager = candlesManager;
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

        double currentPrice = 0;
        if (!company.isSkipLoadingQuote()) {
            QuoteRep quote = finhubClient.getQuoteRep(company, true);

            // Using current price for now
            currentPrice = quote.getCurrentPrice();

            if (NumberUtil.isZero(currentPrice)) {
                log.warnf("Was not able to load quote for the company %s. Fallback to last candle", company.getTicker());
                currentPrice = getCurrentPriceFromLastCandle(result);
            }
        } else {
            log.infof("Skip loading quote for the company %s. Fallback to last candle", company.getTicker());
            currentPrice = getCurrentPriceFromLastCandle(result);
        }

        result.setCurrentStockPrice(currentPrice);
        int totalStocksInHold = 0;
        double totalPricePayed = 0;

        // TODO:mposolda For now, use just always first expected backflow
        ExpectedBackflowRep expectedBackflow = company.getExpectedBackflows().get(0);

        for (PurchaseRep purchase : company.getPurchases()) {
            totalStocksInHold += purchase.getStocksCount();
            totalPricePayed += (purchase.getPricePerStock() * purchase.getStocksCount());

            // Apply fee in the "original" currency
            double fee = purchase.getFee();
            totalPricePayed += fee;
        }

        // Remove stocks in hold, which were already sold
        for (DisposalRep disposal : company.getDisposals()) {
            totalStocksInHold -= disposal.getStocksCount();
        }

        double expectedBackflowInPercentRightNow = (expectedBackflow.getPrice() * expectedBackflow.getBackflowInPercent()) / currentPrice;
        result.setExpectedYearBackflowInPercentRightNow(expectedBackflowInPercentRightNow);

        result.setTotalStocksInHold(totalStocksInHold);
        result.setTotalPricePayed(totalPricePayed);

        double currentPriceOfAllStocksInHold = totalStocksInHold * currentPrice;
        result.setCurrentPriceOfAllStocksInHold(currentPriceOfAllStocksInHold);

        PurchaseManager purchaseManager = Services.instance().getPurchaseManager();
        PurchaseManager.CompanyPurchasesPrice companyPurchases = purchaseManager.getCompanyPurchases(company.getTicker());
        double totalPriceOfAllPurchasesCZK = companyPurchases==null ? 0 : companyPurchases.getTotalCZKPriceOfAllPurchases();
        double totalFeesOfAllPurchasesCZK = companyPurchases==null ? 0 : companyPurchases.getTotalCZKPriceOfAllFees();

        result.setPurchasesFull(companyPurchases==null ? Collections.emptyList() : convertCompanyPurchasesToUIFormat(companyPurchases, expectedBackflow));
        result.setTotalPricePayedCZK(totalPriceOfAllPurchasesCZK);
        result.setTotalFeesPayedCZK(totalFeesOfAllPurchasesCZK);

        result.setDisposalsFull(companyPurchases==null || companyPurchases.getDisposals()==null ? Collections.emptyList() : convertCompanyDisposalsToUIFormat(companyPurchases.getDisposals()));
        result.setTotalPriceSold(companyPurchases==null ? 0 : companyPurchases.getTotalDisposalsPaymentsInOriginalCurrency());
        result.setTotalPriceSoldCZK(companyPurchases==null ? 0 : companyPurchases.getTotalDisposalsPaymentsInCZK());

        result.setDividendsSumPerYear(companyPurchases==null ? Collections.emptyList() : companyPurchases.getDividendsSumsPerYear());
        double dividendsTotal = companyPurchases==null ? 0 : companyPurchases.getTotalDividendsPaymentsInOriginalCurrency();
        double dividendsTotalCZK =  companyPurchases==null ? 0 : companyPurchases.getTotalDividendsPaymentsInCZK();
        result.setTotalDividends(dividendsTotal);
        result.setTotalDividendsCZK(dividendsTotalCZK);

        double earning = currentPriceOfAllStocksInHold - totalPricePayed + dividendsTotal + result.getTotalPriceSold();
        result.setEarning(earning);

        // TODO:mposolda compute averageYearBackflowInPercent

        result.setCurrentPriceOfAllStocksInHoldCZK(currencyConvertor.exchangeMoney(result.getCurrentPriceOfAllStocksInHold(), result.getCurrency(), "CZK"));
        result.setEarningCZK(result.getCurrentPriceOfAllStocksInHoldCZK() - result.getTotalPricePayedCZK() + dividendsTotalCZK + result.getTotalPriceSoldCZK());

        double totalBackflowInPercent = (((result.getCurrentPriceOfAllStocksInHoldCZK() + dividendsTotalCZK + result.getTotalPriceSoldCZK()) / totalPriceOfAllPurchasesCZK) - 1) * 100;
        result.setTotalBackflowInPercent(totalBackflowInPercent);

        return result;
    }

    private double getCurrentPriceFromLastCandle(CompanyFullRep company) {
        try {
            CandlesRep companyCandles = candlesManager.getStockCandles(company, false);
            if (companyCandles.getCandles().isEmpty()) {
                log.warnf("No candle available for company %s. Fallback to 0", company.getTicker());
                return 0;
            } else {
                CandlesRep.CandleRep lastCandle = companyCandles.getCandles().get(companyCandles.getCandles().size() - 1);
                company.setLastCandleDate(lastCandle.getDate());
                return lastCandle.getValue();
            }
        } catch (FailedCandleDownloadException fcde) {
            // Should not happen
            throw new RuntimeException(fcde);
        }
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

        PurchaseManager purchaseManager = Services.instance().getPurchaseManager();
        PurchaseManager.CurrenciesInfo currenciesInfo = purchaseManager.getCurrenciesInfo();
        double inHold = currenciesInfo.getCurrencyRemainingAmount().get(currency.getTicker());

        double quotation = currencyConvertor.exchangeMoney(1, currency.getTicker(), "CZK");

        double priceInHoldCZK = currencyConvertor.exchangeMoney(inHold, currency.getTicker(), "CZK");

        result.setTotalHold(inHold);
        result.setQuotation(quotation);
        result.setPriceInHoldCZK(priceInHoldCZK);

        return result;
    }

    private List<CompanyFullRep.PurchaseFull> convertCompanyPurchasesToUIFormat(PurchaseManager.CompanyPurchasesPrice internalPurchases, ExpectedBackflowRep expectedBackflow) {
        List<CompanyFullRep.PurchaseFull> purchases = internalPurchases.getPurchases()
                .stream()
                .map(purchaseInternal -> purchaseFromInternalPurchase(purchaseInternal, expectedBackflow))
                .collect(Collectors.toList());

        return purchases;
    }

    private CompanyFullRep.PurchaseFull purchaseFromInternalPurchase(PurchaseManager.CompanyPurchaseInternal purchaseInternal, ExpectedBackflowRep expectedBackflow) {
        CompanyFullRep.PurchaseFull purchaseFull = new CompanyFullRep.PurchaseFull();

        int expectedBackflowInPercent = (int) Math.round((expectedBackflow.getPrice() * expectedBackflow.getBackflowInPercent()) / purchaseInternal.getPricePerStock());
        purchaseFull.setExpectedBackflowInPercent(expectedBackflowInPercent);

        purchaseFull.setCompanyTicker(purchaseInternal.getCompanyTicker());
        purchaseFull.setCompanyName(purchaseInternal.getCompanyName());
        purchaseFull.setCurrency(purchaseInternal.getCurrency());
        purchaseFull.setDate(purchaseInternal.getDate());
        purchaseFull.setStocksCount(purchaseInternal.getStocksCount());
        purchaseFull.setPricePerStock(purchaseInternal.getPricePerStock());
        purchaseFull.setPriceTotalCZK(purchaseInternal.getTotalPriceInCZK());
        purchaseFull.setFee(purchaseInternal.getFeeInOriginalCurrency());
        purchaseFull.setFeeCZK(purchaseInternal.getTotalFeeInCZK());
        return purchaseFull;
    }

    private List<CompanyFullRep.DisposalFull> convertCompanyDisposalsToUIFormat(List<PurchaseManager.DisposalInternal> internalDisposals) {
        List<CompanyFullRep.DisposalFull> disposals = internalDisposals
                .stream()
                .map(this::disposalFromInternalDisposal)
                .collect(Collectors.toList());

        return disposals;
    }

    private CompanyFullRep.DisposalFull disposalFromInternalDisposal(PurchaseManager.DisposalInternal disposalInternal) {
        CompanyFullRep.DisposalFull disposalFull = new CompanyFullRep.DisposalFull();
        disposalFull.setDate(disposalInternal.getDate());
        disposalFull.setCompanyTicker(disposalInternal.getCompanyTicker());
        disposalFull.setCompanyName(disposalInternal.getCompanyName());
        disposalFull.setCurrency(disposalInternal.getCurrency());
        disposalFull.setStocksCount(disposalInternal.getSoldStocksCount());
        disposalFull.setPriceTotal(disposalInternal.getTotalAmountInOriginalCurrency());
        disposalFull.setPriceTotalCZK(disposalInternal.getTotalAmountInCZK());
        disposalFull.setFee(disposalInternal.getTotalFeeInOriginalCurrency());
        disposalFull.setFeeCZK(disposalInternal.getTotalFeeInCZK());
        return disposalFull;
    }

}

package org.mposolda.rest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.mposolda.reps.CandlesRep;
import org.mposolda.reps.CurrencyRep;
import org.mposolda.reps.DatabaseRep;
import org.mposolda.reps.QuoteLoaderRep;
import org.mposolda.reps.RateOfReturnsRep;
import org.mposolda.reps.rest.CompaniesRep;
import org.mposolda.reps.rest.CompanyFullRep;
import org.mposolda.reps.rest.CurrenciesRep;
import org.mposolda.reps.rest.DividendsAllSumRep;
import org.mposolda.reps.rest.TransactionsRep;
import org.mposolda.services.FailedCandleDownloadException;
import org.mposolda.services.RateOfReturnsManager;
import org.mposolda.services.Services;
import org.mposolda.util.JsonUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StockResource {

    private final Logger log = Logger.getLogger(StockResource.class);

    @Context
    protected HttpHeaders headers;

    public StockResource() {
    }


    /**
     * Get monitored companies
     *
     * @return
     */
    @GET
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    @Path("companies")
    public CompaniesRep getCompanies() {
        log.debug("getCompanies called");

        CompaniesRep companies = Services.instance().getCompanyInfoManager().getCompanies();

        return companies;
    }


    /**
     * Get company by ticker
     *
     * @param ticker
     * @return
     */
    @GET
    @NoCache
    @Path("companies/{ticker}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompanyFullRep getMapperById(@PathParam("ticker") String ticker) {
        log.debugf("getCompany called for ticker %s", ticker);

        CompaniesRep companies = Services.instance().getCompanyInfoManager().getCompanies();
        CompanyFullRep companyFound = companies.getCompanies().stream()
                .filter(company -> ticker.equals(company.getTicker()))
                .findFirst().orElseThrow(NotFoundException::new);

        return companyFound;
    }


    /**
     * Get company stock candles ticker
     *
     * @param ticker
     * @return
     */
    @GET
    @NoCache
    @Path("companies/candle/{ticker}")
    @Produces(MediaType.APPLICATION_JSON)
    public CandlesRep getCandlesByTicker(@PathParam("ticker") String ticker) {
        log.debugf("getCandlesByTicker called for ticker %s", ticker);

        try {
            CandlesRep candlesRep = Services.instance().getCandlesHistoryManager().getStockCandles(QuoteLoaderRep.fromTicker(ticker), false);
            return candlesRep;
        } catch (FailedCandleDownloadException fcde) {
            // should not happen
            throw new RuntimeException(fcde);
        }
    }


    /**
     * Get monitored currencies
     *
     * @return
     */
    @GET
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    @Path("currencies")
    public CurrenciesRep getCurrencies() {
        log.debug("getCurrencies called");

        CurrenciesRep currencies = Services.instance().getCompanyInfoManager().getCurrencies();

        return currencies;
    }

    /**
     * Get list of transactions
     *
     * @return
     */
    @GET
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    @Path("transactions")
    public TransactionsRep getTransactions() {
        log.debug("getTransactions called");

        CompaniesRep companies = Services.instance().getCompanyInfoManager().getCompanies();

        Set<CompanyFullRep.TradeFull> allTransactions = new TreeSet<>(Comparator.comparing(CompanyFullRep.TradeFull::getDate)
                .thenComparing(CompanyFullRep.TradeFull::getCompanyTicker)
                .thenComparing(CompanyFullRep.TradeFull::getOperation));

        for (CompanyFullRep company : companies.getCompanies()) {
            allTransactions.addAll(company.getPurchasesFull());
            allTransactions.addAll(company.getDisposalsFull());
        }

        TransactionsRep transactions = new TransactionsRep();
        transactions.setTransactions(new LinkedList<>(allTransactions));

        // Transaction summaries
        for (CompanyFullRep.TradeFull transaction : allTransactions) {
            String date = transaction.getDate();
            String year = date.substring(0, 4);
            String yearAndMonth = date.substring(0, 7);

            addTransactionToSummary(transaction, transactions.findOrAddMonthSummary(yearAndMonth));
            addTransactionToSummary(transaction, transactions.findOrAddYearSummary(year));
            addTransactionToSummary(transaction, transactions.getTotalSummary());
        }

        return transactions;
    }

    private void addTransactionToSummary(CompanyFullRep.TradeFull transaction, TransactionsRep.TransactionSummary transactionSummary) {
        if (transaction.getOperation().equals("purchase")) {
            transactionSummary.setPurchasesCount(transactionSummary.getPurchasesCount() + 1);
            transactionSummary.setTotalPurchasesCZK(transactionSummary.getTotalPurchasesCZK() + transaction.getPriceTotalCZK());
        } else {
            transactionSummary.setDisposalsCount(transactionSummary.getDisposalsCount() + 1);
            transactionSummary.setTotalDisposalsCZK(transactionSummary.getTotalDisposalsCZK() + transaction.getPriceTotalCZK());
            transactionSummary.setTotalGainInCZKIgnoringPurchaseCurrency(transactionSummary.getTotalGainInCZKIgnoringPurchaseCurrency() + ((CompanyFullRep.DisposalFull) transaction).getGainInCZKIgnoringPurchaseCurrency());
            transactionSummary.setTotalTaxFromDisposalInCZK(transactionSummary.getTotalTaxFromDisposalInCZK() + ((CompanyFullRep.DisposalFull) transaction).getTaxFromDisposalInCZK());
        }
    }


    /**
     * Get list of dividends sum of all companies for the "Dividends" tab
     *
     * @return
     */
    @GET
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    @Path("dividends-all-sum")
    public DividendsAllSumRep getDividendsSumByCompany() {
        log.debug("getDividendsByMonths called");

        CompaniesRep companies = Services.instance().getCompanyInfoManager().getCompanies();

        Set<DividendsAllSumRep.DividendsSumPerYear2> dividends = new TreeSet<>(
                Comparator.comparing(DividendsAllSumRep.DividendsSumPerYear2::getYear)
                        .thenComparing(DividendsAllSumRep.DividendsSumPerYear2::isYearSum)
                        .thenComparing((divSum1, divSum2) -> {
                            return (int) Math.round(Math.signum(divSum2.getTotalDividendsPaymentsInCZK() - divSum1.getTotalDividendsPaymentsInCZK()));
                        })
                        .thenComparing(DividendsAllSumRep.DividendsSumPerYear2::getCompanyTicker));

        // Add dividends of all companies
        companies.getCompanies().forEach((companyFull) -> {
            companyFull.getDividendsSumPerYear().forEach(dividendsSumPerYear -> {
                DividendsAllSumRep.DividendsSumPerYear2 dividend2 = new DividendsAllSumRep.DividendsSumPerYear2();
                dividend2.setCompanyTicker(companyFull.getTicker());
                dividend2.setCompanyName(companyFull.getName());
                dividend2.setCurrency(companyFull.getCurrency());
                dividend2.setYearSum(false);
                dividend2.setYear(dividendsSumPerYear.getYear());
                dividend2.setTotalDividendsPaymentsInCZK(dividendsSumPerYear.getTotalDividendsPaymentsInCZK());
                dividend2.setTotalDividendsPaymentsInOriginalCurrency(dividendsSumPerYear.getTotalDividendsPaymentsInOriginalCurrency());

                dividends.add(dividend2);
            });
        });

        // Add year sums
        DividendsAllSumRep result = new DividendsAllSumRep();
        result.setDividendsByYearAndCompany(dividends);
        for (DividendsAllSumRep.DividendsSumPerYear2 dividend2 : new ArrayList<>(dividends)) {
            DividendsAllSumRep.DividendsSumPerYear2 dividendSum = result.findOrAddYearSummaryInCompanyDividends(dividend2.getYear());
            dividendSum.setTotalDividendsPaymentsInCZK(dividendSum.getTotalDividendsPaymentsInCZK() + dividend2.getTotalDividendsPaymentsInCZK());
        }

        // Sum for whole year and month
        Set<DividendsAllSumRep.DividendsSumPerYearAndMonth> dividendsYM = new TreeSet<>(
                Comparator.comparing(DividendsAllSumRep.DividendsSumPerYearAndMonth::getYear)
                        .thenComparing(DividendsAllSumRep.DividendsSumPerYearAndMonth::isYearSum)
                        .thenComparing(DividendsAllSumRep.DividendsSumPerYearAndMonth::getYearAndMonth));
        result.setDividendsByYearAndMonth(dividendsYM);

        companies.getCompanies().forEach((companyFull) -> {
            companyFull.getDividendsSumPerYear().forEach(dividendsSumPerYear -> {
                dividendsSumPerYear.getDividendsOfYear().forEach(singleDividendRep -> {
                    String date = singleDividendRep.getDate();
                    String year = date.substring(0, 4);
                    String yearAndMonth = date.substring(0, 7);

                    DividendsAllSumRep.DividendsSumPerYearAndMonth yearAndMonthSum = result.findOrAddYearAndMonthSummaryInYearAndMonthDividends(year, yearAndMonth);
                    DividendsAllSumRep.DividendsSumPerYearAndMonth yearSum = result.findOrAddYearSummaryInYearAndMonthDividends(year);

                    yearAndMonthSum.setSumCZK(yearAndMonthSum.getSumCZK() + singleDividendRep.getTotalAmountInCZK());
                    yearSum.setSumCZK(yearSum.getSumCZK() + singleDividendRep.getTotalAmountInCZK());
                });
            });
        });

        return result;
    }

    /**
     * Get list of transactions
     *
     * @return
     */
    @GET
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    @Path("rate-of-returns")
    public RateOfReturnsRep getRateOfReturns() {
        log.debug("getRateOfReturns called");

        RateOfReturnsManager mgr = Services.instance().getRateOfReturnsManager();
        RateOfReturnsRep rep = mgr.getRateOfReturnsRep();

        return rep;
    }

    @GET
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    @Path("czk-currency")
    public CurrencyRep getCzkCurrency() {
        String companiesJsonFileLocation = Services.instance().getConfig().getCompaniesJsonFileLocation();
        DatabaseRep database = JsonUtil.loadDatabase(companiesJsonFileLocation);
        CurrencyRep czkCurrency = database.getCurrencies().stream()
                .filter(currencyRep -> "CZK".equals(currencyRep.getTicker()))
                .findFirst().orElseThrow();
        return czkCurrency;
    }

}

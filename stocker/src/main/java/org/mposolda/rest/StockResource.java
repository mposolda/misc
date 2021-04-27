package org.mposolda.rest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
import org.mposolda.reps.rest.CompaniesRep;
import org.mposolda.reps.rest.CompanyFullRep;
import org.mposolda.reps.rest.CurrenciesRep;
import org.mposolda.reps.rest.TransactionsRep;
import org.mposolda.services.Services;

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

        Set<CompanyFullRep.TradeFull> allTransactions = new TreeSet<>(Comparator.comparing(CompanyFullRep.TradeFull::getDate).thenComparing(CompanyFullRep.TradeFull::getCompanyTicker));

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
        }
    }

}

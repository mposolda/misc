package org.mposolda.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.mposolda.reps.rest.CompaniesRep;
import org.mposolda.reps.rest.CurrenciesRep;
import org.mposolda.reps.rest.CurrencyFullRep;
import org.mposolda.services.CurrencyConvertor;
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
        log.info("getCompanies called");

        CompaniesRep companies = Services.instance().getCompanyInfoManager().getCompanies();

        return companies;
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
        log.info("getCurrencies called");

        CurrencyConvertor currencyConvertor = Services.instance().getCurrencyConvertor();

        // TODO:mposolda - dont have this hardcoded here
        String[] currencies = new String[] { "EUR", "USD", "CAD", "HKD", "NOK" };

        List<CurrencyFullRep> result = new ArrayList<>();
        for (String currencyFrom : currencies) {
            double czechCrowns = currencyConvertor.exchangeMoney(1, currencyFrom, "CZK");
            CurrencyFullRep currency = new CurrencyFullRep();
            currency.setCurrencyTicker(currencyFrom);
            currency.setQuotation(czechCrowns);
            result.add(currency);
        }

        CurrenciesRep rep = new CurrenciesRep();
        rep.setCurrencies(result);
        rep.setFinished(true); // TODO:mposolda
        return rep;
    }



}

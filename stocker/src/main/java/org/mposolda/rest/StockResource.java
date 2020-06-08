package org.mposolda.rest;

import java.util.HashMap;
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
import org.mposolda.reps.rest.CurrenciesRestRep;
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
    public CurrenciesRestRep getCurrencies() {
        log.info("getCurrencies called");

        CurrencyConvertor currencyConvertor = Services.instance().getCurrencyConvertor();

        // TODO:mposolda - dont have this hardcoded here
        String[] currencies = new String[] { "EUR", "USD", "CAD", "HKD", "NOK" };

        Map<String, Double> result = new HashMap<>();
        for (String currencyFrom : currencies) {
            double czechCrowns = currencyConvertor.exchangeMoney(1, currencyFrom, "CZK");
            result.put(currencyFrom, czechCrowns);
        }

        CurrenciesRestRep cur = new CurrenciesRestRep();
        cur.setQuote(result);

        return cur;
    }



}

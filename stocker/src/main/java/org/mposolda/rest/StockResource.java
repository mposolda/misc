package org.mposolda.rest;

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

}

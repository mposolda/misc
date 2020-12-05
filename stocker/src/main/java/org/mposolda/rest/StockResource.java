package org.mposolda.rest;

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

}

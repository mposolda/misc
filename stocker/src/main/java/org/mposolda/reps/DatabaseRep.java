package org.mposolda.reps;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of the whole "database"
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DatabaseRep {

    @JsonProperty("companies")
    private List<CompanyRep> companies;

    @JsonProperty("currencies")
    private List<CurrencyRep> currencies;

    public List<CompanyRep> getCompanies() {
        return companies;
    }

    public void setCompanies(List<CompanyRep> companies) {
        this.companies = companies;
    }

    public List<CurrencyRep> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<CurrencyRep> currencies) {
        this.currencies = currencies;
    }
}

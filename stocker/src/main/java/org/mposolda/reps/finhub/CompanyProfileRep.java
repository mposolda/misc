package org.mposolda.reps.finhub;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.reps.BaseRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CompanyProfileRep extends BaseRep {

    @JsonProperty("ticker")
    protected String ticker;
    @JsonProperty("country")
    protected String country;
    @JsonProperty("currency")
    public String currency;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Company [ ticker=" + ticker + ", currency=" + currency + ", country=" + country + " ]";
    }
}

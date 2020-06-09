package org.mposolda.reps.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.reps.BaseRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CurrencyFullRep extends BaseRep {

    // Key is something like "EUR" or "USD" . Value is amount of CZK for one EUR/USD/whatever
    @JsonProperty("currencyTicker")
    private String currencyTicker;

    @JsonProperty("quotation")
    private Double quotation;

    public String getCurrencyTicker() {
        return currencyTicker;
    }

    public void setCurrencyTicker(String currencyTicker) {
        this.currencyTicker = currencyTicker;
    }

    public Double getQuotation() {
        return quotation;
    }

    public void setQuotation(Double quotation) {
        this.quotation = quotation;
    }
}

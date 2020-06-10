package org.mposolda.reps.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.reps.CurrencyRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CurrencyFullRep extends CurrencyRep {

    @JsonProperty("boughtTotal")
    private Double boughtTotal;

    @JsonProperty("boughtTotalPriceInCZK")
    private Double boughtTotalPriceInCZK;

    @JsonProperty("totalFeesInCZK")
    private Double totalFeesInCZK;

    @JsonProperty("investedTotal")
    private Double investedTotal;

    @JsonProperty("totalHold")
    private Double totalHold;

    @JsonProperty("quotation")
    private Double quotation;

    @JsonProperty("priceInHoldCZK")
    private Double priceInHoldCZK;


    public Double getBoughtTotal() {
        return boughtTotal;
    }

    public void setBoughtTotal(Double boughtTotal) {
        this.boughtTotal = boughtTotal;
    }

    public Double getBoughtTotalPriceInCZK() {
        return boughtTotalPriceInCZK;
    }

    public void setBoughtTotalPriceInCZK(Double boughtTotalPriceInCZK) {
        this.boughtTotalPriceInCZK = boughtTotalPriceInCZK;
    }

    public Double getTotalFeesInCZK() {
        return totalFeesInCZK;
    }

    public void setTotalFeesInCZK(Double totalFeesInCZK) {
        this.totalFeesInCZK = totalFeesInCZK;
    }

    public Double getInvestedTotal() {
        return investedTotal;
    }

    public void setInvestedTotal(Double investedTotal) {
        this.investedTotal = investedTotal;
    }

    public Double getTotalHold() {
        return totalHold;
    }

    public void setTotalHold(Double totalHold) {
        this.totalHold = totalHold;
    }

    public Double getQuotation() {
        return quotation;
    }

    public void setQuotation(Double quotation) {
        this.quotation = quotation;
    }

    public Double getPriceInHoldCZK() {
        return priceInHoldCZK;
    }

    public void setPriceInHoldCZK(Double priceInHoldCZK) {
        this.priceInHoldCZK = priceInHoldCZK;
    }
}

package org.mposolda.reps;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CompanyRep {

    @JsonProperty("name")
    protected String name;
    @JsonProperty("ticker")
    protected String ticker;
    @JsonProperty("currency")
    public String currency;

    @JsonProperty("expectedBackflows")
    public List<ExpectedBackflowRep> expectedBackflows;

    @JsonProperty("purchases")
    public List<PurchaseRep> purchases;

    @JsonProperty("disposals")
    public List<DisposalRep> disposals;

    @JsonProperty("dividends")
    public List<DividendRep> dividends;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<ExpectedBackflowRep> getExpectedBackflows() {
        return expectedBackflows;
    }

    public void setExpectedBackflows(List<ExpectedBackflowRep> expectedBackflows) {
        this.expectedBackflows = expectedBackflows;
    }

    public List<PurchaseRep> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<PurchaseRep> purchases) {
        this.purchases = purchases;
    }

    public List<DisposalRep> getDisposals() {
        return disposals == null ? Collections.emptyList() : disposals;
    }

    public void setDisposals(List<DisposalRep> disposals) {
        this.disposals = disposals;
    }

    public List<DividendRep> getDividends() {
        return dividends == null ? Collections.emptyList() : dividends;
    }

    public void setDividends(List<DividendRep> dividends) {
        this.dividends = dividends;
    }
}

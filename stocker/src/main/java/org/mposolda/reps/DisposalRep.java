package org.mposolda.reps;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DisposalRep extends BaseRep {

    @JsonProperty("date")
    public String date;

    @JsonProperty("stocksCount")
    public int stocksCount;

    @JsonProperty("pricePerStock")
    public double pricePerStock;

    @JsonProperty("fee")
    public double fee;

    @JsonProperty("currencyPriceToCZKAtTheDisposalTime")
    public double currencyPriceToCZKAtTheDisposalTime;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStocksCount() {
        return stocksCount;
    }

    public void setStocksCount(int stocksCount) {
        this.stocksCount = stocksCount;
    }

    public double getPricePerStock() {
        return pricePerStock;
    }

    public void setPricePerStock(double pricePerStock) {
        this.pricePerStock = pricePerStock;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getCurrencyPriceToCZKAtTheDisposalTime() {
        return currencyPriceToCZKAtTheDisposalTime;
    }

    public void setCurrencyPriceToCZKAtTheDisposalTime(double currencyPriceToCZKAtTheDisposalTime) {
        this.currencyPriceToCZKAtTheDisposalTime = currencyPriceToCZKAtTheDisposalTime;
    }
}

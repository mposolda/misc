package org.mposolda.reps;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Predecessor for purchase or disposal
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public abstract class AbstractTradeRep extends BaseRep {

    @JsonProperty("date")
    public String date;

    @JsonProperty("stocksCount")
    public int stocksCount;

    @JsonProperty("pricePerStock")
    public double pricePerStock;

    @JsonProperty("fee")
    public double fee;

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
}

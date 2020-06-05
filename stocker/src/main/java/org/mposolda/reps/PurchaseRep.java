package org.mposolda.reps;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class PurchaseRep extends BaseRep {

    @JsonProperty("date")
    public String date;

    @JsonProperty("stocksCount")
    public int stocksCount;

    @JsonProperty("pricePerStock")
    public double pricePerStock;

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
}

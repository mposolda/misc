package org.mposolda.reps;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CurrencyRep extends BaseRep {

    // Something like "EUR" or "USD"
    @JsonProperty("ticker")
    private String ticker;

    @JsonProperty("purchases")
    private List<CurrencyPurchaseRep> purchases;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public List<CurrencyPurchaseRep> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<CurrencyPurchaseRep> purchases) {
        this.purchases = purchases;
    }

    public static class CurrencyPurchaseRep extends BaseRep {

        @JsonProperty("date")
        private String date;

        @JsonProperty("countBought")
        private Double countBought;

        @JsonProperty("feeInCZK")
        private Double feeInCZK;

        @JsonProperty("pricePerUnit")
        private Double pricePerUnit;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Double getCountBought() {
            return countBought;
        }

        public void setCountBought(Double countBought) {
            this.countBought = countBought;
        }

        public Double getFeeInCZK() {
            return feeInCZK;
        }

        public void setFeeInCZK(Double feeInCZK) {
            this.feeInCZK = feeInCZK;
        }

        public Double getPricePerUnit() {
            return pricePerUnit;
        }

        public void setPricePerUnit(Double pricePerUnit) {
            this.pricePerUnit = pricePerUnit;
        }
    }
}

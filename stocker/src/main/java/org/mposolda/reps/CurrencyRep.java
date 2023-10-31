package org.mposolda.reps;

import java.util.List;
import java.util.Map;

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

    @JsonProperty("deposits")
    private List<CurrencyDepositRep> deposits;

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

    public List<CurrencyDepositRep> getDeposits() {
        return deposits;
    }

    public void setDeposits(List<CurrencyDepositRep> deposits) {
        this.deposits = deposits;
    }

    public static class CurrencyDepositRep extends BaseRep {

        @JsonProperty("date")
        private String date;

        @JsonProperty("countBought")
        private Double countBought;

        @JsonProperty("benchmarks")
        private DepositBenchmarksRep benchmarks;

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

        public DepositBenchmarksRep getBenchmarks() {
            return benchmarks;
        }

        public void setBenchmarks(DepositBenchmarksRep benchmarks) {
            this.benchmarks = benchmarks;
        }
    }

    public static class DepositBenchmarksRep extends BaseRep {

        @JsonProperty("sp500Price")
        private Double sp500Price;

        @JsonProperty("berkshireBPrice")
                    private Double berkshireBPrice;

        @JsonProperty("markelPrice")
        private Double markelPrice;

        @JsonProperty("czkToUsd")
        private Double czkToUsd;

        public Double getSp500Price() {
            return sp500Price;
        }

        public void setSp500Price(Double sp500Price) {
            this.sp500Price = sp500Price;
        }

        public Double getBerkshireBPrice() {
            return berkshireBPrice;
        }

        public void setBerkshireBPrice(Double berkshireBPrice) {
            this.berkshireBPrice = berkshireBPrice;
        }

        public Double getMarkelPrice() {
            return markelPrice;
        }

        public void setMarkelPrice(Double markelPrice) {
            this.markelPrice = markelPrice;
        }

        public Double getCzkToUsd() {
            return czkToUsd;
        }

        public void setCzkToUsd(Double czkToUsd) {
            this.czkToUsd = czkToUsd;
        }
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

        @JsonProperty("currencyFrom")
        private String currencyFrom;

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

        public String getCurrencyFrom() {
            return currencyFrom;
        }

        public void setCurrencyFrom(String currencyFrom) {
            this.currencyFrom = currencyFrom;
        }
    }
}

package org.mposolda.reps.rest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.reps.CompanyRep;
import org.mposolda.reps.DisposalRep;
import org.mposolda.reps.DividendRep;
import org.mposolda.reps.PurchaseRep;

/**
 * All informations about company. Includes the hardcoded stuff as well as computed stuff
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CompanyFullRep extends CompanyRep {

    @JsonProperty("currentStockPrice")
    private double currentStockPrice;

    @JsonProperty("totalStocksInHold")
    private int totalStocksInHold;

    @JsonProperty("totalPricePayed")
    private double totalPricePayed;

    @JsonProperty("totalPriceSold")
    private double totalPriceSold;

    @JsonProperty("totalFeesPayed")
    private double totalFeesPayed;

    @JsonProperty("totalPricePayedCZK")
    private double totalPricePayedCZK;

    @JsonProperty("totalPriceSoldCZK")
    private double totalPriceSoldCZK;

    @JsonProperty("totalFeesPayedCZK")
    private double totalFeesPayedCZK;

    @JsonProperty("currentPriceOfAllStocksInHold")
    private double currentPriceOfAllStocksInHold;

    @JsonProperty("currentPriceOfAllStocksInHoldCZK")
    private double currentPriceOfAllStocksInHoldCZK;

    @JsonProperty("totalDividends")
    private double totalDividends;

    @JsonProperty("totalDividendsCZK")
    private double totalDividendsCZK;

    // Can be negative in case of loss
    @JsonProperty("earning")
    private double earning;

    @JsonProperty("earningCZK")
    private double earningCZK;

    @JsonProperty("totalBackflowInPercent")
    private double totalBackflowInPercent;

    @JsonProperty("averageYearBackflowInPercent")
    private double averageYearBackflowInPercent;

    @JsonProperty("expectedYearBackflowInPercentRightNow")
    private double expectedYearBackflowInPercentRightNow;

    @JsonProperty("purchasesFull")
    private List<PurchaseFull> purchasesFull;

    @JsonProperty("disposalsFull")
    private List<DisposalFull> disposalsFull;

    // This probably should be handled better... Having constructor this way is not so great...
    public CompanyFullRep(CompanyRep companyRep) {
        this.name = companyRep.getName();
        this.ticker = companyRep.getTicker();
        this.currency = companyRep.getCurrency();
        this.expectedBackflows = new LinkedList<>(companyRep.getExpectedBackflows());
    }

    public double getCurrentStockPrice() {
        return currentStockPrice;
    }

    public void setCurrentStockPrice(double currentStockPrice) {
        this.currentStockPrice = currentStockPrice;
    }

    // Count of total stocksBought - total stocksSold (So current count of stocks in hold) TODO:mposolda maybe also compute this dynamically when we have both purchases and disposals here?
    public int getTotalStocksInHold() {
        return totalStocksInHold;
    }

    public void setTotalStocksInHold(int totalStocksInHold) {
        this.totalStocksInHold = totalStocksInHold;
    }

    // Count of total stocksBought
    @JsonProperty("totalStocksBought")
    public int getTotalStocksBought() {
        int sum = 0;
        if (purchasesFull != null) {
            for (PurchaseFull purchase : purchasesFull) {
                sum += purchase.getStocksCount();
            }
        }
        return sum;
    }

    // Count of total stocksSold
    @JsonProperty("totalStocksSold")
    public int getTotalStocksSold() {
        int sum = 0;
        if (disposalsFull != null) {
            for (DisposalFull disposal : disposalsFull) {
                sum += disposal.getStocksCount();
            }
        }
        return sum;
    }

    public double getTotalPricePayed() {
        return totalPricePayed;
    }

    public void setTotalPricePayed(double totalPricePayed) {
        this.totalPricePayed = totalPricePayed;
    }

    public double getTotalPriceSold() {
        return totalPriceSold;
    }

    public void setTotalPriceSold(double totalPriceSold) {
        this.totalPriceSold = totalPriceSold;
    }

    public double getTotalFeesPayed() {
        return totalFeesPayed;
    }

    public void setTotalFeesPayed(double totalFeesPayed) {
        this.totalFeesPayed = totalFeesPayed;
    }

    public double getTotalPricePayedCZK() {
        return totalPricePayedCZK;
    }

    public void setTotalPricePayedCZK(double totalPricePayedCZK) {
        this.totalPricePayedCZK = totalPricePayedCZK;
    }

    public double getTotalPriceSoldCZK() {
        return totalPriceSoldCZK;
    }

    public void setTotalPriceSoldCZK(double totalPriceSoldCZK) {
        this.totalPriceSoldCZK = totalPriceSoldCZK;
    }

    // Total fees of all purchases and disposals in CZK TODO:mposolda maybe compute this here instead of set it?
    public double getTotalFeesPayedCZK() {
        return totalFeesPayedCZK;
    }

    public void setTotalFeesPayedCZK(double totalFeesPayedCZK) {
        this.totalFeesPayedCZK = totalFeesPayedCZK;
    }

    // Total fees of all purchases in the original currency (not disposals)+
    @JsonProperty("totalFeesOfPurchasesCZK")
    public double getTotalFeesOfPurchasesCZK() {
        double sum = 0;
        if (purchasesFull != null) {
            for (PurchaseFull purchase : purchasesFull) {
                sum += purchase.getFeeCZK();
            }
        }
        return sum;
    }

    // Total fees of all purchases in the original currency
    @JsonProperty("totalFeesOfPurchases")
    public double getTotalFeesOfPurchases() {
        double sum = 0;
        if (purchasesFull != null) {
            for (PurchaseFull purchase : purchasesFull) {
                sum += purchase.getFee();
            }
        }
        return sum;
    }

    // Total fees of all disposals in the original currency (not purchases)
    @JsonProperty("totalFeesOfDisposals")
    public double getTotalFeesOfDisposals() {
        double sum = 0;
        if (disposalsFull != null) {
            for (DisposalFull disposal : disposalsFull) {
                sum += disposal.getFee();
            }
        }
        return sum;
    }

    // Total fees of all disposals in the CZK (not purchases)
    @JsonProperty("totalFeesOfDisposalsCZK")
    public double getTotalFeesOfDisposalsCZK() {
        double sum = 0;
        if (disposalsFull != null) {
            for (DisposalFull disposal : disposalsFull) {
                sum += disposal.getFeeCZK();
            }
        }
        return sum;
    }

    public double getCurrentPriceOfAllStocksInHoldCZK() {
        return currentPriceOfAllStocksInHoldCZK;
    }

    public void setCurrentPriceOfAllStocksInHoldCZK(double currentPriceOfAllStocksInHoldCZK) {
        this.currentPriceOfAllStocksInHoldCZK = currentPriceOfAllStocksInHoldCZK;
    }

    public double getTotalDividends() {
        return totalDividends;
    }

    public void setTotalDividends(double totalDividends) {
        this.totalDividends = totalDividends;
    }

    public double getTotalDividendsCZK() {
        return totalDividendsCZK;
    }

    public void setTotalDividendsCZK(double totalDividendsCZK) {
        this.totalDividendsCZK = totalDividendsCZK;
    }

    public double getEarningCZK() {
        return earningCZK;
    }

    public void setEarningCZK(double earningCZK) {
        this.earningCZK = earningCZK;
    }

    public double getCurrentPriceOfAllStocksInHold() {
        return currentPriceOfAllStocksInHold;
    }

    public void setCurrentPriceOfAllStocksInHold(double currentPriceOfAllStocksInHold) {
        this.currentPriceOfAllStocksInHold = currentPriceOfAllStocksInHold;
    }

    public double getEarning() {
        return earning;
    }

    public void setEarning(double earning) {
        this.earning = earning;
    }

    public double getTotalBackflowInPercent() {
        return totalBackflowInPercent;
    }

    public void setTotalBackflowInPercent(double totalBackflowInPercent) {
        this.totalBackflowInPercent = totalBackflowInPercent;
    }

    public double getAverageYearBackflowInPercent() {
        return averageYearBackflowInPercent;
    }

    public void setAverageYearBackflowInPercent(double averageYearBackflowInPercent) {
        this.averageYearBackflowInPercent = averageYearBackflowInPercent;
    }

    public double getExpectedYearBackflowInPercentRightNow() {
        return expectedYearBackflowInPercentRightNow;
    }

    public void setExpectedYearBackflowInPercentRightNow(double expectedYearBackflowInPercentRightNow) {
        this.expectedYearBackflowInPercentRightNow = expectedYearBackflowInPercentRightNow;
    }

    public List<PurchaseFull> getPurchasesFull() {
        return purchasesFull;
    }

    public void setPurchasesFull(List<PurchaseFull> purchasesFull) {
        this.purchasesFull = purchasesFull;
    }

    public List<DisposalFull> getDisposalsFull() {
        return disposalsFull;
    }

    public void setDisposalsFull(List<DisposalFull> disposalsFull) {
        this.disposalsFull = disposalsFull;
    }

    @Override
    public List<PurchaseRep> getPurchases() {
        // There is better solution to this...
        return Collections.emptyList();
    }

    @Override
    public void setPurchases(List<PurchaseRep> purchases) {
        // Do nothing. There is better solution to this...
    }

    @Override
    public List<DividendRep> getDividends() {
        // There is better solution to this...
        return Collections.emptyList();
    }

    @Override
    public void setDividends(List<DividendRep> dividends) {
        // Do nothing. There is better solution to this...
    }

    public List<DisposalRep> getDisposals() {
        // There is better solution to this...
        return Collections.emptyList();
    }

    public void setDisposals(List<DisposalRep> disposals) {
        // Do nothing. There is better solution to this...
    }

    public static class PurchaseFull extends PurchaseRep {

        // Expected backflow at the time of purchase
        private int expectedBackflowInPercent;
        private double priceTotalCZK;
        private double feeCZK;

        public int getExpectedBackflowInPercent() {
            return expectedBackflowInPercent;
        }

        public void setExpectedBackflowInPercent(int expectedBackflowInPercent) {
            this.expectedBackflowInPercent = expectedBackflowInPercent;
        }

        public double getPriceTotal() {
            return (this.stocksCount * this.pricePerStock) + getFee();
        }

        public double getPriceTotalCZK() {
            return this.priceTotalCZK;
        }

        public void setPriceTotalCZK(double priceTotalCZK) {
            this.priceTotalCZK = priceTotalCZK;
        }

        public double getFeeCZK() {
            return this.feeCZK;
        }

        public void setFeeCZK(double feeCZK) {
            this.feeCZK = feeCZK;
        }

        public double getCurrencyQuotationDuringTransaction() {
            return this.priceTotalCZK / this.getPriceTotal();
        }
    }

    public static class DisposalFull extends DisposalRep {

        private double priceTotal;
        private double priceTotalCZK;
        private double feeCZK;

        @Override
        public double getPricePerStock() {
            return (this.getPriceTotal() + this.getFee()) / this.getStocksCount();
        }

        @Override
        public double getCurrencyPriceToCZKAtTheDisposalTime() {
            return this.priceTotalCZK / this.getPriceTotal();
        }

        public double getPriceTotal() {
            return priceTotal;
        }

        public void setPriceTotal(double priceTotal) {
            this.priceTotal = priceTotal;
        }

        public double getPriceTotalCZK() {
            return priceTotalCZK;
        }

        public void setPriceTotalCZK(double priceTotalCZK) {
            this.priceTotalCZK = priceTotalCZK;
        }

        public void setFeeCZK(double feeCZK) {
            this.feeCZK = feeCZK;
        }

        public double getFeeCZK() {
            return this.feeCZK;
        }
    }

}

package org.mposolda.reps.rest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.reps.CompanyRep;
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

    @JsonProperty("currentPriceOfAllStocksInHold")
    private double currentPriceOfAllStocksInHold;

    // Can be negative in case of loss
    @JsonProperty("earning")
    private double earning;

    @JsonProperty("totalBackflowInPercent")
    private int totalBackflowInPercent;

    @JsonProperty("averageYearBackflowInPercent")
    private int averageYearBackflowInPercent;

    @JsonProperty("expectedYearBackflowInPercentRightNow")
    private int expectedYearBackflowInPercentRightNow;

    @JsonProperty("purchasesFull")
    private List<PurchaseFull> purchasesFull;

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

    public int getTotalStocksInHold() {
        return totalStocksInHold;
    }

    public void setTotalStocksInHold(int totalStocksInHold) {
        this.totalStocksInHold = totalStocksInHold;
    }

    public double getTotalPricePayed() {
        return totalPricePayed;
    }

    public void setTotalPricePayed(double totalPricePayed) {
        this.totalPricePayed = totalPricePayed;
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

    public int getTotalBackflowInPercent() {
        return totalBackflowInPercent;
    }

    public void setTotalBackflowInPercent(int totalBackflowInPercent) {
        this.totalBackflowInPercent = totalBackflowInPercent;
    }

    public int getAverageYearBackflowInPercent() {
        return averageYearBackflowInPercent;
    }

    public void setAverageYearBackflowInPercent(int averageYearBackflowInPercent) {
        this.averageYearBackflowInPercent = averageYearBackflowInPercent;
    }

    public int getExpectedYearBackflowInPercentRightNow() {
        return expectedYearBackflowInPercentRightNow;
    }

    public void setExpectedYearBackflowInPercentRightNow(int expectedYearBackflowInPercentRightNow) {
        this.expectedYearBackflowInPercentRightNow = expectedYearBackflowInPercentRightNow;
    }

    public List<PurchaseFull> getPurchasesFull() {
        return purchasesFull;
    }

    public void setPurchasesFull(List<PurchaseFull> purchasesFull) {
        this.purchasesFull = purchasesFull;
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

    public static class PurchaseFull extends PurchaseRep {

        public PurchaseFull(PurchaseRep purchase) {
            this.date = purchase.getDate();
            this.pricePerStock = purchase.getPricePerStock();
            this.stocksCount = purchase.getStocksCount();
        }

        // Expected backflow at the time of purchase
        private int expectedBackflowInPercent;

        public int getExpectedBackflowInPercent() {
            return expectedBackflowInPercent;
        }

        public void setExpectedBackflowInPercent(int expectedBackflowInPercent) {
            this.expectedBackflowInPercent = expectedBackflowInPercent;
        }
    }

}

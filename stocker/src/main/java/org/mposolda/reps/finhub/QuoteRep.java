package org.mposolda.reps.finhub;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.reps.BaseRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class QuoteRep extends BaseRep {

    // Open price of the day
    @JsonProperty("o")
    public double openDayPrice;

    // Low price of the day
    @JsonProperty("l")
    public double lowDayPrice;

    // High price of the day
    @JsonProperty("h")
    public double highDayPrice;

    // Current price
    @JsonProperty("c")
    public double currentPrice;

    // Previous close price
    @JsonProperty("pc")
    public double previousClosePrice;


    public double getOpenDayPrice() {
        return openDayPrice;
    }

    public void setOpenDayPrice(double openDayPrice) {
        this.openDayPrice = openDayPrice;
    }

    public double getLowDayPrice() {
        return lowDayPrice;
    }

    public void setLowDayPrice(double lowDayPrice) {
        this.lowDayPrice = lowDayPrice;
    }

    public double getHighDayPrice() {
        return highDayPrice;
    }

    public void setHighDayPrice(double highDayPrice) {
        this.highDayPrice = highDayPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getPreviousClosePrice() {
        return previousClosePrice;
    }

    public void setPreviousClosePrice(double previousClosePrice) {
        this.previousClosePrice = previousClosePrice;
    }

    @Override
    public String toString() {
        return "Quote [ openDayPrice=" + openDayPrice + ", lowDayPrice=" + lowDayPrice + ", highDayPrice=" + highDayPrice
                + ", currentPrice=" + currentPrice + ", previousClosePrice=" + previousClosePrice + " ]";
    }

}

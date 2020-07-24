package org.mposolda.reps.finhub;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.reps.BaseRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StockCandleRep extends BaseRep {

    // Open price of the day
    @JsonProperty("o")
    public List<Double> openDayPrice;

    // Low price of the day
    @JsonProperty("l")
    public List<Double> lowDayPrice;

    // High price of the day
    @JsonProperty("h")
    public List<Double> highDayPrice;

    // Current price
    @JsonProperty("c")
    public List<Double> currentPrice;

    // List of timestamps
    @JsonProperty("t")
    public List<Long> timestamps;

    // Status can be either "ok" or "no_data"
    @JsonProperty("s")
    public String status;

    public List<Double> getOpenDayPrice() {
        return openDayPrice;
    }

    public void setOpenDayPrice(List<Double> openDayPrice) {
        this.openDayPrice = openDayPrice;
    }

    public List<Double> getLowDayPrice() {
        return lowDayPrice;
    }

    public void setLowDayPrice(List<Double> lowDayPrice) {
        this.lowDayPrice = lowDayPrice;
    }

    public List<Double> getHighDayPrice() {
        return highDayPrice;
    }

    public void setHighDayPrice(List<Double> highDayPrice) {
        this.highDayPrice = highDayPrice;
    }

    public List<Double> getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(List<Double> currentPrice) {
        this.currentPrice = currentPrice;
    }

    public List<Long> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<Long> timestamps) {
        this.timestamps = timestamps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StockCandle [ openDayPrice=" + openDayPrice + ", lowDayPrice=" + lowDayPrice + ", highDayPrice=" + highDayPrice
                + ", currentPrice=" + currentPrice + ", status=" + status + ", timestamps=" + timestamps + " ]";
    }
}

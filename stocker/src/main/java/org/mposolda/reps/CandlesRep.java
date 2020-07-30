package org.mposolda.reps;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.util.DateUtil;
import org.mposolda.util.NumberFormatUtil;

/**
 * Tracks candles (history) in the internal format. Used for both stock candles and currency candles
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CandlesRep {

    // Exactly one of the currencyTicker or stockTicker should be non-null.
    @JsonProperty("currencyTicker")
    private String currencyTicker;
    @JsonProperty("stockTicker")
    private String stockTicker;

    @JsonProperty("lastDateTimestampSec")
    private long lastDateTimestampSec;

    @JsonProperty("candles")
    private List<CandleRep> candles;

    public String getCurrencyTicker() {
        return currencyTicker;
    }

    public void setCurrencyTicker(String currencyTicker) {
        this.currencyTicker = currencyTicker;
    }

    public String getStockTicker() {
        return stockTicker;
    }

    public void setStockTicker(String stockTicker) {
        this.stockTicker = stockTicker;
    }

    public List<CandleRep> getCandles() {
        return candles;
    }

    public void setCandles(List<CandleRep> candles) {
        this.candles = candles;
    }

    public long getLastDateTimestampSec() {
        return lastDateTimestampSec;
    }

    public void setLastDateTimestampSec(long lastDateTimestampSec) {
        this.lastDateTimestampSec = lastDateTimestampSec;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CandlesRep [ ");
        if (currencyTicker != null) {
            sb.append("currencyTicker: " + currencyTicker);
        }
        if (stockTicker != null) {
            sb.append("stockTicker: " + stockTicker);
        }
        sb.append(", lastDate: " + DateUtil.numberInSecondsToDate(lastDateTimestampSec));
        sb.append(", candles: " + candles + " ]");
        return sb.toString();
    }

    public static class CandleRep {

        @JsonProperty("t")
        private long timestampInSeconds;

        @JsonProperty("o")
        private double value;

        public long getTimestampInSeconds() {
            return timestampInSeconds;
        }

        public void setTimestampInSeconds(long timestampInSeconds) {
            this.timestampInSeconds = timestampInSeconds;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return DateUtil.numberInSecondsToDate(timestampInSeconds) + " : " + NumberFormatUtil.format(value) + "\n";
        }
    }

}

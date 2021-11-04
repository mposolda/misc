package org.mposolda.reps.finhub;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.client.JsonSerialization;
import org.mposolda.reps.BaseRep;

/**
 * Representation for currency candles from fixer
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CurrencyCandlesRep extends BaseRep {

    private boolean success;

    private boolean timeseries;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    private String base;

    private Map<String, Map<String, Double>> rates;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isTimeseries() {
        return timeseries;
    }

    public void setTimeseries(boolean timeseries) {
        this.timeseries = timeseries;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Map<String, Map<String, Double>> getRates() {
        return rates;
    }

    public void setRates(Map<String, Map<String, Double>> rates) {
        this.rates = rates;
    }

    @Override
    public String toString() {
        try {
            return "CurrencyCandlesRep: " + JsonSerialization.writeValueAsPrettyString(this);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}

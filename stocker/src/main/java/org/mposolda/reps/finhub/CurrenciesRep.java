package org.mposolda.reps.finhub;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.reps.BaseRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CurrenciesRep extends BaseRep {

    @JsonProperty("base")
    private String base;

    @JsonProperty("rates")
    private Map<String, Double> rates;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}

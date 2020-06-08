package org.mposolda.reps.rest;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.reps.BaseRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CurrenciesRestRep extends BaseRep {

    // Key is something like "EUR" or "USD" . Value is amount of CZK for one EUR/USD/whatever
    @JsonProperty("quote")
    private Map<String, Double> quote;

    public Map<String, Double> getQuote() {
        return quote;
    }

    public void setQuote(Map<String, Double> quote) {
        this.quote = quote;
    }
}

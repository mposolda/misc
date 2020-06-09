package org.mposolda.reps.rest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.reps.BaseRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CurrenciesRep extends BaseRep {

    private List<CurrencyFullRep> currencies = new LinkedList<>();

    private final AtomicBoolean finished = new AtomicBoolean(false);

    @JsonProperty("currencies")
    public List<CurrencyFullRep> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<CurrencyFullRep> currencies) {
        this.currencies = currencies;
    }

    @JsonProperty("finished")
    public Boolean getFinished() {
        return finished.get();
    }

    public void setFinished(Boolean finished) {
        this.finished.set(finished);
    }
}

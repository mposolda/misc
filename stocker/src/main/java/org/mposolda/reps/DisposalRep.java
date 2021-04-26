package org.mposolda.reps;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DisposalRep extends AbstractTradeRep {

    @JsonProperty("currencyPriceToCZKAtTheDisposalTime")
    public double currencyPriceToCZKAtTheDisposalTime;

    public double getCurrencyPriceToCZKAtTheDisposalTime() {
        return currencyPriceToCZKAtTheDisposalTime;
    }

    public void setCurrencyPriceToCZKAtTheDisposalTime(double currencyPriceToCZKAtTheDisposalTime) {
        this.currencyPriceToCZKAtTheDisposalTime = currencyPriceToCZKAtTheDisposalTime;
    }
}

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

    @JsonProperty("depositTotalInCZK")
    private double depositTotalInCZK;

    @JsonProperty("currencies")
    private List<CurrencyFullRep> currencies = new LinkedList<>();

    @JsonProperty("finished")
    private final AtomicBoolean finished = new AtomicBoolean(false);

    public double getDepositTotalInCZK() {
        return depositTotalInCZK;
    }

    public void setDepositTotalInCZK(double depositTotalInCZK) {
        this.depositTotalInCZK = depositTotalInCZK;
    }

    public List<CurrencyFullRep> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<CurrencyFullRep> currencies) {
        this.currencies = currencies;
    }

    public Boolean getFinished() {
        return finished.get();
    }

    public void setFinished(Boolean finished) {
        this.finished.set(finished);
    }

    /**
     * @return Total amount of currently available money in CZK
     */
    @JsonProperty("priceInHoldCZK")
    public Double getPriceInHoldCZK() {

        double result = 0;
        for (CurrencyFullRep currency : currencies) {
            result += currency.getPriceInHoldCZK();
        }
        return result;
    }

    /**
     *
     * @return Total amount of fees payed in the forex exchanges (currency exchanges)
     */
    @JsonProperty("totalFeesCZK")
    public Double getTotalFeesCZK() {
        double result = 0;
        for (CurrencyFullRep currency : currencies) {
            result += currency.getTotalFeesInCZK();
        }
        return result;
    }
}

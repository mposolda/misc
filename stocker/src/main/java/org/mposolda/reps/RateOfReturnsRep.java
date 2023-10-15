package org.mposolda.reps;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RateOfReturnsRep {

    private List<CurrencyRep.CurrencyDepositRep> deposits = new ArrayList<>();

    // Absolute rate-of-return (Just simple computation by (totalValue)/(totalDeposits)
    private double rateOfReturnAbsolute;

    // Rate-of-return properly computed per year
    private double rateOfReturnPerYear;

    // Rate-of-return for S&P 500 index aligned to my deposits TODO:mposolda implement
    private double rateOfReturnPerYearSP500;

    public List<CurrencyRep.CurrencyDepositRep> getDeposits() {
        return deposits;
    }

    public void setDeposits(List<CurrencyRep.CurrencyDepositRep> deposits) {
        this.deposits = deposits;
    }

    public double getRateOfReturnAbsolute() {
        return rateOfReturnAbsolute;
    }

    public void setRateOfReturnAbsolute(double rateOfReturnAbsolute) {
        this.rateOfReturnAbsolute = rateOfReturnAbsolute;
    }

    public double getRateOfReturnPerYear() {
        return rateOfReturnPerYear;
    }

    public void setRateOfReturnPerYear(double rateOfReturnPerYear) {
        this.rateOfReturnPerYear = rateOfReturnPerYear;
    }

    public double getRateOfReturnPerYearSP500() {
        return rateOfReturnPerYearSP500;
    }

    public void setRateOfReturnPerYearSP500(double rateOfReturnPerYearSP500) {
        this.rateOfReturnPerYearSP500 = rateOfReturnPerYearSP500;
    }
}

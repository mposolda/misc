package org.mposolda.reps;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RateOfReturnsRep {

    // Absolute rate-of-return (Just simple computation by (totalValue)/(totalDeposits)
    private Double rateOfReturnAbsolute;

    // Rate-of-return properly computed per year
    private Double rateOfReturnPerYear;

    // Rate-of-return for S&P 500 index aligned to my deposits TODO:mposolda implement
    private Double rateOfReturnPerYearSP500;

    public Double getRateOfReturnAbsolute() {
        return rateOfReturnAbsolute;
    }

    public void setRateOfReturnAbsolute(Double rateOfReturnAbsolute) {
        this.rateOfReturnAbsolute = rateOfReturnAbsolute;
    }

    public Double getRateOfReturnPerYear() {
        return rateOfReturnPerYear;
    }

    public void setRateOfReturnPerYear(Double rateOfReturnPerYear) {
        this.rateOfReturnPerYear = rateOfReturnPerYear;
    }

    public Double getRateOfReturnPerYearSP500() {
        return rateOfReturnPerYearSP500;
    }

    public void setRateOfReturnPerYearSP500(Double rateOfReturnPerYearSP500) {
        this.rateOfReturnPerYearSP500 = rateOfReturnPerYearSP500;
    }
}

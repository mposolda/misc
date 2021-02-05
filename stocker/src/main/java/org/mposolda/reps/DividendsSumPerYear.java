package org.mposolda.reps;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sum of all dividends of particular company payed for single year
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DividendsSumPerYear {

    @JsonProperty("year")
    private int year;

    @JsonProperty("totalDividendsPaymentsInOriginalCurrency")
    private double totalDividendsPaymentsInOriginalCurrency;

    @JsonProperty("totalDividendsPaymentsInCZK")
    private double totalDividendsPaymentsInCZK;

    public DividendsSumPerYear() {
    }

    public DividendsSumPerYear(int year, double totalDividendsPaymentsInOriginalCurrency, double totalDividendsPaymentsInCZK) {
        this.year = year;
        this.totalDividendsPaymentsInOriginalCurrency = totalDividendsPaymentsInOriginalCurrency;
        this.totalDividendsPaymentsInCZK = totalDividendsPaymentsInCZK;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getTotalDividendsPaymentsInOriginalCurrency() {
        return totalDividendsPaymentsInOriginalCurrency;
    }

    public void setTotalDividendsPaymentsInOriginalCurrency(double totalDividendsPaymentsInOriginalCurrency) {
        this.totalDividendsPaymentsInOriginalCurrency = totalDividendsPaymentsInOriginalCurrency;
    }

    public double getTotalDividendsPaymentsInCZK() {
        return totalDividendsPaymentsInCZK;
    }

    public void setTotalDividendsPaymentsInCZK(double totalDividendsPaymentsInCZK) {
        this.totalDividendsPaymentsInCZK = totalDividendsPaymentsInCZK;
    }

    @JsonProperty("averageQuotationToCZK")
    public double getAverageQuotationToCZK() {
        return this.totalDividendsPaymentsInCZK / this.totalDividendsPaymentsInOriginalCurrency;
    }
}

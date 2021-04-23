package org.mposolda.reps;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sum of all dividends of particular company payed for single year
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DividendsSumPerYear {

    @JsonProperty("year")
    private int year;

    @JsonProperty("totalAmount")
    private double totalDividendsPaymentsInOriginalCurrency;

    @JsonProperty("totalAmountInCZK")
    private double totalDividendsPaymentsInCZK;

    @JsonProperty("dividendsOfYear")
    private List<SingleDividendRep> dividendsOfYear;

    public DividendsSumPerYear() {
    }

    public DividendsSumPerYear(int year, double totalDividendsPaymentsInOriginalCurrency, double totalDividendsPaymentsInCZK, List<SingleDividendRep> dividendsOfYear) {
        this.year = year;
        this.totalDividendsPaymentsInOriginalCurrency = totalDividendsPaymentsInOriginalCurrency;
        this.totalDividendsPaymentsInCZK = totalDividendsPaymentsInCZK;
        this.dividendsOfYear = dividendsOfYear;
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

    public List<SingleDividendRep> getDividendsOfYear() {
        return dividendsOfYear;
    }

    public void setDividendsOfYear(List<SingleDividendRep> dividendsOfYear) {
        this.dividendsOfYear = dividendsOfYear;
    }

    @JsonProperty("averageQuotationToCZK")
    public double getAverageQuotationToCZK() {
        return this.totalDividendsPaymentsInCZK / this.totalDividendsPaymentsInOriginalCurrency;
    }


    public static class SingleDividendRep {

        @JsonProperty("date")
        public String date;

        @JsonProperty("totalAmount")
        public double totalAmount;

        @JsonProperty("totalAmountInCZK")
        public double totalAmountInCZK;

        public SingleDividendRep() {
        }

        public SingleDividendRep(String date, double totalAmount, double totalAmountInCZK) {
            this.date = date;
            this.totalAmount = totalAmount;
            this.totalAmountInCZK = totalAmountInCZK;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public double getTotalAmountInCZK() {
            return totalAmountInCZK;
        }

        public void setTotalAmountInCZK(double totalAmountInCZK) {
            this.totalAmountInCZK = totalAmountInCZK;
        }

        @JsonProperty("averageQuotationToCZK")
        public double getAverageQuotationToCZK() {
            return this.totalAmountInCZK / this.totalAmount;
        }

    }


}

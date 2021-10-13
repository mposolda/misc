package org.mposolda.reps.rest;

/**
 * Sum of dividends payed in particular month of the year
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DividendsSumPerYearAndMonth {

    // True if this entry corresponds to the summary for the whole year
    private boolean yearSum;

    // Filled always
    private String year;

    // Filled if yearSum is false
    private String yearAndMonth;

    private Double sumCZK;

    public boolean isYearSum() {
        return yearSum;
    }

    public void setYearSum(boolean yearSum) {
        this.yearSum = yearSum;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYearAndMonth() {
        return yearAndMonth;
    }

    public void setYearAndMonth(String yearAndMonth) {
        this.yearAndMonth = yearAndMonth;
    }

    public Double getSumCZK() {
        return sumCZK;
    }

    public void setSumCZK(Double sumCZK) {
        this.sumCZK = sumCZK;
    }
}

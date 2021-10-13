package org.mposolda.reps.rest;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DividendsAllSumRep {

    @JsonProperty("dividendsByYearAndCompany")
    private Set<DividendsSumPerYear2> dividendsByYearAndCompany;

    @JsonProperty("dividendsByYearAndMonth")
    private Set<DividendsSumPerYearAndMonth> dividendsByYearAndMonth;

    public Set<DividendsSumPerYear2> getDividendsByYearAndCompany() {
        return dividendsByYearAndCompany;
    }

    public void setDividendsByYearAndCompany(Set<DividendsSumPerYear2> dividendsByYearAndCompany) {
        this.dividendsByYearAndCompany = dividendsByYearAndCompany;
    }

    public Set<DividendsSumPerYearAndMonth> getDividendsByYearAndMonth() {
        return dividendsByYearAndMonth;
    }

    public void setDividendsByYearAndMonth(Set<DividendsSumPerYearAndMonth> dividendsByYearAndMonth) {
        this.dividendsByYearAndMonth = dividendsByYearAndMonth;
    }

    public DividendsSumPerYear2 findOrAddYearSummaryInCompanyDividends(int year) {
        return dividendsByYearAndCompany.stream()
                .filter(summary -> year == summary.getYear() && summary.isYearSum())
                .findFirst().orElseGet(() -> {
                    DividendsSumPerYear2 result = new DividendsSumPerYear2();
                    result.setYear(year);
                    result.setYearSum(true);

                    dividendsByYearAndCompany.add(result);
                    return result;
                });
    }

    public DividendsSumPerYearAndMonth findOrAddYearAndMonthSummaryInYearAndMonthDividends(String year, String yearAndMonth) {
        return dividendsByYearAndMonth.stream()
                .filter(summary -> yearAndMonth.equals(summary.getYearAndMonth()))
                .findFirst().orElseGet(() -> {
                    DividendsSumPerYearAndMonth result = new DividendsSumPerYearAndMonth();
                    result.setYear(year);
                    result.setYearAndMonth(yearAndMonth);
                    result.setYearSum(false);
                    result.setSumCZK(0d);

                    dividendsByYearAndMonth.add(result);
                    return result;
                });
    }

    public DividendsSumPerYearAndMonth findOrAddYearSummaryInYearAndMonthDividends(String year) {
        return dividendsByYearAndMonth.stream()
                .filter(summary -> year.equals(summary.getYear()) && summary.isYearSum())
                .findFirst().orElseGet(() -> {
                    DividendsSumPerYearAndMonth result = new DividendsSumPerYearAndMonth();
                    result.setYear(year);
                    result.setYearSum(true);
                    result.setSumCZK(0d);

                    dividendsByYearAndMonth.add(result);
                    return result;
                });
    }

    public static class DividendsSumPerYear2 extends DividendsSumPerYear {

        private String companyTicker;

        private String companyName;

        private String currency;

        // True if this entry corresponds to the summary for the whole year
        private boolean yearSum;

        public String getCompanyTicker() {
            return companyTicker;
        }

        public void setCompanyTicker(String companyTicker) {
            this.companyTicker = companyTicker;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public boolean isYearSum() {
            return yearSum;
        }

        public void setYearSum(boolean yearSum) {
            this.yearSum = yearSum;
        }

    }


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

}

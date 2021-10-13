package org.mposolda.reps.rest;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DividendsAllSumRep {

    @JsonProperty("dividendsByYearAndCompany")
    private Set<DividendsSumPerYear2> dividendsByYearAndCompany;

    public Set<DividendsSumPerYear2> getDividendsByYearAndCompany() {
        return dividendsByYearAndCompany;
    }

    public void setDividendsByYearAndCompany(Set<DividendsSumPerYear2> dividendsByYearAndCompany) {
        this.dividendsByYearAndCompany = dividendsByYearAndCompany;
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

    public static class DividendsSumPerYear2 extends DividendsSumPerYear {

        private String companyTicker;

        private String currency;

        // True if this entry corresponds to the summary for the whole year
        private boolean yearSum;

        public String getCompanyTicker() {
            return companyTicker;
        }

        public void setCompanyTicker(String companyTicker) {
            this.companyTicker = companyTicker;
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

}

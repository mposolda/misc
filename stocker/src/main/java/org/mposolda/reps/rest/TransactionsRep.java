package org.mposolda.reps.rest;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class TransactionsRep {

    @JsonProperty("transactions")
    private List<CompanyFullRep.TradeFull> transactions;

    private Set<TransactionSummary> transactionSummaries = new TreeSet<>((summary1, summary2) -> {
        // Compare years first
        int yearCmp = summary1.getYear().compareTo(summary2.getYear());
        if (yearCmp != 0) return yearCmp;

        // Compare yearSum then - Add it to the end
        if (summary1.isYearSum()) return 1;
        if (summary2.isYearSum()) return -1;

        // Finally compare yearAndMonth
        return summary1.getYearAndMonth().compareTo(summary2.getYearAndMonth());
    });

    private TransactionSummary totalSummary = new TransactionSummary();

    public List<CompanyFullRep.TradeFull> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<CompanyFullRep.TradeFull> transactions) {
        this.transactions = transactions;
    }

    @JsonProperty("transactionSummaries")
    public List<TransactionSummary> getTransactionSummaries() {
        return new LinkedList<>(transactionSummaries);
    }

    public TransactionSummary getTotalSummary() {
        return totalSummary;
    }

    public TransactionSummary findOrAddYearSummary(String year) {
        return transactionSummaries.stream()
                .filter(summary -> year.equals(summary.getYear()) && summary.isYearSum())
                .findFirst().orElseGet(() -> {
                    TransactionSummary result = new TransactionSummary();
                    result.setYear(year);
                    result.setYearSum(true);

                    transactionSummaries.add(result);
                    return result;
                });
    }

    // param "yearAndMonth" is in the format like "2020-07"
    public TransactionSummary findOrAddMonthSummary(String yearAndMonth) {
        return transactionSummaries.stream()
                .filter(summary -> yearAndMonth.equals(summary.getYearAndMonth()))
                .findFirst().orElseGet(() -> {
                    TransactionSummary result = new TransactionSummary();
                    result.setYear(yearAndMonth.substring(0, 4));
                    result.setYearAndMonth(yearAndMonth);
                    result.setYearSum(false);

                    transactionSummaries.add(result);
                    return result;
                });
    }

    public static class TransactionSummary {

        // True if this entry corresponds to the summary for the whole year
        private boolean yearSum;

        // Filled always
        private String year;

        // Filled if yearSum is false
        private String yearAndMonth;

        private int purchasesCount;
        private int disposalsCount;
        private double totalPurchasesCZK;
        private double totalDisposalsCZK;

        private double totalGainInCZKIgnoringPurchaseCurrency;
        private double totalTaxFromDisposalInCZK;

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

        public int getPurchasesCount() {
            return purchasesCount;
        }

        public void setPurchasesCount(int purchasesCount) {
            this.purchasesCount = purchasesCount;
        }

        public int getDisposalsCount() {
            return disposalsCount;
        }

        public void setDisposalsCount(int disposalsCount) {
            this.disposalsCount = disposalsCount;
        }

        public double getTotalPurchasesCZK() {
            return totalPurchasesCZK;
        }

        public void setTotalPurchasesCZK(double totalPurchasesCZK) {
            this.totalPurchasesCZK = totalPurchasesCZK;
        }

        public double getTotalDisposalsCZK() {
            return totalDisposalsCZK;
        }

        public void setTotalDisposalsCZK(double totalDisposalsCZK) {
            this.totalDisposalsCZK = totalDisposalsCZK;
        }

        public double getTotalGainInCZKIgnoringPurchaseCurrency() {
            return totalGainInCZKIgnoringPurchaseCurrency;
        }

        public void setTotalGainInCZKIgnoringPurchaseCurrency(double totalGainInCZKIgnoringPurchaseCurrency) {
            this.totalGainInCZKIgnoringPurchaseCurrency = totalGainInCZKIgnoringPurchaseCurrency;
        }

        public double getTotalTaxFromDisposalInCZK() {
            return totalTaxFromDisposalInCZK;
        }

        public void setTotalTaxFromDisposalInCZK(double totalTaxFromDisposalInCZK) {
            this.totalTaxFromDisposalInCZK = totalTaxFromDisposalInCZK;
        }
    }
}

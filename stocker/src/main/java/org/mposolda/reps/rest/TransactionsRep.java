package org.mposolda.reps.rest;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class TransactionsRep {

    @JsonProperty("transactions")
    private List<CompanyFullRep.TradeFull> transactions;

    public List<CompanyFullRep.TradeFull> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<CompanyFullRep.TradeFull> transactions) {
        this.transactions = transactions;
    }
}

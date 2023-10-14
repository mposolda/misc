package org.mposolda.util.returner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates all input data for computing "rate of return"
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RateOfReturnInput {

    //
    private final long endDateMs;
    private final Double endValue;

    private final List<Deposit> deposits = new ArrayList<>();

    public RateOfReturnInput(long endDateMs, Double endValue, Deposit... deposits) {
        this.endDateMs = endDateMs;
        this.endValue = endValue;
        if (deposits != null) {
            Collections.addAll(this.deposits, deposits);
        }
    }

    public long getEndDateMs() {
        return endDateMs;
    }

    public Double getEndValue() {
        return endValue;
    }

    public List<Deposit> getDeposits() {
        return deposits;
    }

    public static class Deposit {

        private final long depositDateMs;
        private final Double depositValue;

        public Deposit(long depositDateMs, Double depositValue) {
            this.depositDateMs = depositDateMs;
            this.depositValue = depositValue;
        }

        public long getDepositDateMs() {
            return depositDateMs;
        }

        public Double getDepositValue() {
            return depositValue;
        }
    }
}

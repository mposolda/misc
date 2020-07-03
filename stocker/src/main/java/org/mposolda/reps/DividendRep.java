package org.mposolda.reps;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DividendRep extends BaseRep {

    @JsonProperty("date")
    public String date;

    @JsonProperty("totalAmount")
    public double totalAmount;

    @JsonProperty("totalAmountInCZK")
    public double totalAmountInCZK;

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
}

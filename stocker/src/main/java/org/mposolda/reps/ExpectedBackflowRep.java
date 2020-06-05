package org.mposolda.reps;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ExpectedBackflowRep extends BaseRep {

    @JsonProperty("date")
    public String date;

    @JsonProperty("price")
    public double price;

    @JsonProperty("backflowInPercent")
    public int backflowInPercent;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getBackflowInPercent() {
        return backflowInPercent;
    }

    public void setBackflowInPercent(int backflowInPercent) {
        this.backflowInPercent = backflowInPercent;
    }
}

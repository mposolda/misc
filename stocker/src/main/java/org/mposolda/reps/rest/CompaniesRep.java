package org.mposolda.reps.rest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mposolda.reps.BaseRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CompaniesRep extends BaseRep {

    private List<CompanyFullRep> companies = new LinkedList<>();

    private final AtomicBoolean finished = new AtomicBoolean(false);

    @JsonProperty("companies")
    public List<CompanyFullRep> getCompanies() {
        return companies;
    }

    public void setCompanies(List<CompanyFullRep> companies) {
        this.companies = companies;
    }

    @JsonProperty("finished")
    public Boolean getFinished() {
        return finished.get();
    }

    public void setFinished(Boolean finished) {
        this.finished.set(finished);
    }

    /**
     *
     * @return Total price payed for all stocks in CZK
     */
    @JsonProperty("totalPricePayedCZK")
    public Double getTotalPricePayedCZK() {
        double result = 0;
        for (CompanyFullRep company : companies) {
            result += company.getTotalPricePayedCZK();
        }
        return result;
    }

    /**
     *
     * @return Total price of all sold stocks in CZK
     */
    @JsonProperty("totalPriceSoldCZK")
    public Double getTotalPriceSoldCZK() {
        double result = 0;
        for (CompanyFullRep company : companies) {
            result += company.getTotalPriceSoldCZK();
        }
        return result;
    }

    /**
     * @return Current price of all currently holded stocks in CZK
     */
    @JsonProperty("currentPriceOfAllStocksInHoldCZK")
    public Double getCurrentPriceOfAllStocksInHoldCZK() {
        double result = 0;
        for (CompanyFullRep company : companies) {
            result += company.getCurrentPriceOfAllStocksInHoldCZK();
        }
        return result;
    }

    /**
     *
     * @return Total dividends granted from all companies in CZK
     */
    @JsonProperty("totalDividendsCZK")
    public Double getTotalDividendsCZK() {
        double result = 0;
        for (CompanyFullRep company : companies) {
            result += company.getTotalDividendsCZK();
        }
        return result;
    }

    /**
     *
     * @return Total earning in CZK
     */
    @JsonProperty("earningCZK")
    public Double getEarningCZK() {
        double result = 0;
        for (CompanyFullRep company : companies) {
            result += company.getEarningCZK();
        }
        return result;
    }

    /**
     * @return Total backflow in percent
     */
    @JsonProperty("totalBackflowInPercent")
    public Double getTotalBackflowInPercent() {
        return (((getCurrentPriceOfAllStocksInHoldCZK() + getTotalDividendsCZK() + getTotalPriceSoldCZK()) / getTotalPricePayedCZK()) - 1) * 100;
    }

    /**
     * @return Total fees in CZK
     */
    @JsonProperty("totalFeesCZK")
    public Double getTotalFeesCZK() {
        double result = 0;
        for (CompanyFullRep company : companies) {
            result += company.getTotalFeesPayedCZK();
        }
        return result;
    }

    // TODO:mposolda average year backflow in percent and expected year backflow in percent

}

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
}

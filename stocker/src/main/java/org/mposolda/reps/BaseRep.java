package org.mposolda.reps;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class BaseRep {

    protected Map<String, Object> others = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getOthers() {
        return others;
    }

    @JsonAnySetter
    public void setOthers(String name, Object value) {
        others.put(name, value);
    }
}

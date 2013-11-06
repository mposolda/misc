package org.mposolda.drools.uripolicytest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RequestInfo {

    private final String uri;

    private final Map<String, ParamValue> reqParams = new HashMap<String, ParamValue>();

    public RequestInfo(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public Collection<String> getRequestParamNames() {
        return reqParams.keySet();
    }

    public void addRequestParam(String paramName, String paramValue) {
        reqParams.put(paramName, new ParamValue(paramValue));
    }

    public ParamValue requestParam(String paramName) {
        return reqParams.get(paramName);
    }
}

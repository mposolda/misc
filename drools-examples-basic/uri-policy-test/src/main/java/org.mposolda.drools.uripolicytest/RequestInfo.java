package org.mposolda.drools.uripolicytest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RequestInfo {

    private final String uri;
    private final RequestType requestType;

    private final Map<String, ParamValue> reqParams = new HashMap<String, ParamValue>();

    public RequestInfo(String uri, RequestType requestType) {
        this.uri = uri;
        this.requestType = requestType;
    }

    public String getUri() {
        return uri;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public Collection<String> getRequestParamNames() {
        return reqParams.keySet();
    }

    public void addRequestParam(String paramName, String paramValue) {
        reqParams.put(paramName, new ParamValue(paramValue));
    }

    public ParamValue requestParam(String paramName) {
        ParamValue paramValue = reqParams.get(paramName);

        // TODO: Temporary
        if (paramValue == null) {
            return new ParamValue(null);
        }  else {
            return paramValue;
        }
    }

    @Override
    public String toString() {
        return "Request [ uri=" + uri + ", requestType=" + requestType + " ]";
    }
}

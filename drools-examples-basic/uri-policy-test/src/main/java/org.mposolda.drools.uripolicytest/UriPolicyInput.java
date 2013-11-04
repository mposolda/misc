package org.mposolda.drools.uripolicytest;

import java.util.Map;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class UriPolicyInput {

    private final String uri;

    private final Map<String, String> reqParams;

    public UriPolicyInput(String uri, Map<String, String> reqParams) {
        this.uri = uri;
        this.reqParams = reqParams;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getReqParams() {
        return reqParams;
    }
}

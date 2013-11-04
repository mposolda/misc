package org.mposolda.drools.uripolicytest.template;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class UriTemplate {

    private final int priority;
    private final String uriPattern;
    private final String queryParamsCondition;

    public UriTemplate(int priority, String uriPattern, String queryParamsCondition) {
        this.priority = priority;
        this.uriPattern = uriPattern;
        this.queryParamsCondition = queryParamsCondition;
    }

    public int getPriority() {
        return priority;
    }

    public String getUriPattern() {
        return uriPattern;
    }

    public String getQueryParamsCondition() {
        return queryParamsCondition;
    }
}

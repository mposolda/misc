package org.mposolda.drools.uripolicytest.template;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class URIPolicyEntry {

    private final int priority;
    private final String uriPattern;
    private final String queryParamsCondition;

    private final String allowedRealmRoles;
    private final String deniedRealmRoles;
    private final String allowedApplicationRoles;
    private final String deniedApplicationRoles;
    private final String allowedUsers;
    private final String deniedUsers;

    public URIPolicyEntry(int priority, String uriPattern, String queryParamsCondition, String allowedRealmRoles, String deniedRealmRoles,
                          String allowedApplicationRoles, String deniedApplicationRoles, String allowedUsers, String deniedUsers) {
        this.priority = priority;
        this.uriPattern = uriPattern;
        this.queryParamsCondition = queryParamsCondition;

        this.allowedRealmRoles = allowedRealmRoles;
        this.allowedApplicationRoles = allowedApplicationRoles;
        this.allowedUsers = allowedUsers;
        this.deniedRealmRoles = deniedRealmRoles;
        this.deniedApplicationRoles = deniedApplicationRoles;
        this.deniedUsers = deniedUsers;
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

    public String getAllowedRealmRoles() {
        return allowedRealmRoles;
    }

    public String getDeniedRealmRoles() {
        return deniedRealmRoles;
    }

    public String getAllowedApplicationRoles() {
        return allowedApplicationRoles;
    }

    public String getDeniedApplicationRoles() {
        return deniedApplicationRoles;
    }

    public String getAllowedUsers() {
        return allowedUsers;
    }

    public String getDeniedUsers() {
        return deniedUsers;
    }
}

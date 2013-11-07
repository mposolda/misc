package org.mposolda.drools.uripolicytest;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Token {

    private final String username;
    private final List<String> applicationRoles;
    private final List<String> realmRoles;

    public Token(String username, List<String> realmRoles, List<String> applicationRoles) {
        this.username = username;
        this.realmRoles = realmRoles;
        this.applicationRoles = applicationRoles;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRealmRoles() {
        return realmRoles;
    }

    public List<String> getApplicationRoles() {
        return applicationRoles;
    }

    @Override
    public String toString() {
        return new StringBuilder("Token [ username=")
                .append(username)
                .append(", realmRoles=")
                .append(realmRoles)
                .append(", applicationRoles=")
                .append(applicationRoles)
                .append(" ]").toString();
    }
}

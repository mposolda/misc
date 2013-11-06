package org.mposolda.drools.uripolicytest;

import java.util.Collection;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Token {

    private final String username;
    private final Collection<String> applicationRoles;
    private final Collection<String> realmRoles;

    public Token(String username, Collection<String> realmRoles, Collection<String> applicationRoles) {
        this.username = username;
        this.realmRoles = realmRoles;
        this.applicationRoles = applicationRoles;
    }

    public String getUsername() {
        return username;
    }

    public Collection<String> getRealmRoles() {
        return realmRoles;
    }

    public Collection<String> getApplicationRoles() {
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

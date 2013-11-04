package org.mposolda.drools.uripolicytest;

import java.util.List;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Token {

    private final String username;
    private final List<String> realmRoles;

    public Token(String username, List<String> realmRoles) {
        this.username = username;
        this.realmRoles = realmRoles;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRealmRoles() {
        return realmRoles;
    }
}

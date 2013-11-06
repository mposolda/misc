package org.mposolda.drools.uripolicytest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RolesContainer {

    private Set<String> allowedRealmRoles;
    private Set<String> allowedApplicationRoles;
    private Set<String> deniedRealmRoles;
    private Set<String> deniedApplicationRoles;
    private Set<String> allowedUsers;
    private Set<String> deniedUsers;

    // METHODS FOR ADDING ROLES INTO CONTAINER

    public RolesContainer addAllowedRealmRole(String roleName) {
        if (allowedRealmRoles == null) {
            allowedRealmRoles = new HashSet<String>();
        }
        allowedRealmRoles.add(roleName);
        return this;
    }

    public RolesContainer addAllowedApplicationRole(String roleName) {
        if (allowedApplicationRoles == null) {
            allowedApplicationRoles = new HashSet<String>();
        }
        allowedApplicationRoles.add(roleName);
        return this;
    }

    public RolesContainer addDeniedRealmRole(String roleName) {
        if (deniedRealmRoles == null) {
            deniedRealmRoles = new HashSet<String>();
        }
        deniedRealmRoles.add(roleName);
        return this;
    }

    public RolesContainer addDeniedApplicationRole(String roleName) {
        if (deniedApplicationRoles == null) {
            deniedApplicationRoles = new HashSet<String>();
        }
        deniedApplicationRoles.add(roleName);
        return this;
    }

    public RolesContainer addAllowedUser(String username) {
        if (allowedUsers == null) {
            allowedUsers = new HashSet<String>();
        }
        allowedUsers.add(username);
        return this;
    }

    public RolesContainer addDeniedUser(String username) {
        if (deniedUsers == null) {
            deniedUsers = new HashSet<String>();
        }
        deniedUsers.add(username);
        return this;
    }

    public RolesContainer addAllAllowedRealmRoles(Collection<String> coll) {
        if (allowedRealmRoles == null) {
            allowedRealmRoles = new HashSet<String>();
        }
        allowedRealmRoles.addAll(coll);
        return this;
    }

    public RolesContainer addAllAllowedApplicationRoles(Collection<String> coll) {
        if (allowedApplicationRoles == null) {
            allowedApplicationRoles = new HashSet<String>();
        }
        allowedApplicationRoles.addAll(coll);
        return this;
    }

    public RolesContainer addAllDeniedRealmRoles(Collection<String> coll) {
        if (deniedRealmRoles == null) {
            deniedRealmRoles = new HashSet<String>();
        }
        deniedRealmRoles.addAll(coll);
        return this;
    }

    public RolesContainer addAllDeniedApplicationRoles(Collection<String> coll) {
        if (deniedApplicationRoles == null) {
            deniedApplicationRoles = new HashSet<String>();
        }
        deniedApplicationRoles.addAll(coll);
        return this;
    }

    public RolesContainer addAllAllowedUsers(Collection<String> coll) {
        if (allowedUsers == null) {
            allowedUsers = new HashSet<String>();
        }
        allowedUsers.addAll(coll);
        return this;
    }

    public RolesContainer addAllDeniedUsers(Collection<String> coll) {
        if (deniedUsers == null) {
            deniedUsers = new HashSet<String>();
        }
        deniedUsers.addAll(coll);
        return this;
    }

    // GETTERS

    public Set<String> getAllowedRealmRoles() {
        return Collections.unmodifiableSet(allowedRealmRoles);
    }

    public Set<String> getAllowedApplicationRoles() {
        return Collections.unmodifiableSet(allowedApplicationRoles);
    }

    public Set<String> getDeniedRealmRoles() {
        return Collections.unmodifiableSet(deniedRealmRoles);
    }

    public Set<String> getDeniedApplicationRoles() {
        return Collections.unmodifiableSet(deniedRealmRoles);
    }

    public Set<String> getAllowedUsers() {
        return Collections.unmodifiableSet(allowedUsers);
    }

    public Set<String> getDeniedUsers() {
        return Collections.unmodifiableSet(deniedUsers);
    }

    // CHECKS

    public Decision isRealmRoleAllowed(String roleName) {
        if (deniedRealmRoles != null && deniedRealmRoles.contains(roleName)) {
            return Decision.REJECT;
        } else if (allowedRealmRoles != null && (allowedRealmRoles.contains(roleName) || allowedRealmRoles.contains("*"))) {
            return Decision.ACCEPT;
        }

        return Decision.IGNORE;
    }

    public Decision isApplicationRoleAllowed(String roleName) {
        if (deniedApplicationRoles != null && deniedApplicationRoles.contains(roleName)) {
            return Decision.REJECT;
        } else if (allowedApplicationRoles != null && (allowedApplicationRoles.contains(roleName) || allowedApplicationRoles.contains("*"))) {
            return Decision.ACCEPT;
        }

        return Decision.IGNORE;
    }

    public Decision isRealmRolesAllowed(Collection<String> roles) {
        boolean anyAllowed = false;
        for (String role : roles) {
            Decision authDecision = isRealmRoleAllowed(role);
            if (authDecision == Decision.REJECT) {
                // REJECT always wins
                return Decision.REJECT;
            } else if (authDecision == Decision.ACCEPT) {
                anyAllowed = true;
            }
        }

        return anyAllowed ? Decision.ACCEPT : Decision.IGNORE;
    }

    public Decision isApplicationRolesAllowed(Collection<String> roles) {
        boolean anyAllowed = false;
        for (String role : roles) {
            Decision authDecision = isApplicationRoleAllowed(role);
            if (authDecision == Decision.REJECT) {
                // REJECT always wins
                return Decision.REJECT;
            } else if (authDecision == Decision.ACCEPT) {
                anyAllowed = true;
            }
        }

        return anyAllowed ? Decision.ACCEPT : Decision.IGNORE;
    }

    public Decision isTokenAllowed(Token token) {
        Decision realmDecision = isRealmRolesAllowed(token.getRealmRoles());
        Decision appRolesDecision = isApplicationRolesAllowed(token.getApplicationRoles());
        Decision usernameDecision = isUserAllowed(token.getUsername());
        return realmDecision.mergeDecision(appRolesDecision).mergeDecision(usernameDecision);
    }

    public Decision isUserAllowed(String username) {
        if (deniedUsers != null && deniedUsers.contains(username)) {
            return Decision.REJECT;
        } else if (allowedUsers != null && (allowedUsers.contains(username))) {
            return Decision.ACCEPT;
        }

        return Decision.IGNORE;
    }

    // HELPER METHODS

    @Override
    public String toString() {
        return new StringBuilder("RolesContainer [ allowedRealmRoles=")
                .append(allowedRealmRoles)
                .append(", allowedApplicationRoles=")
                .append(allowedApplicationRoles)
                .append(", deniedRealmRoles=")
                .append(deniedRealmRoles)
                .append(", deniedApplicationRoles=")
                .append(deniedApplicationRoles)
                .append(", allowedUsers=")
                .append(allowedUsers)
                .append(", deniedUsers=")
                .append(deniedUsers)
                .append(" ]").toString();
    }
}

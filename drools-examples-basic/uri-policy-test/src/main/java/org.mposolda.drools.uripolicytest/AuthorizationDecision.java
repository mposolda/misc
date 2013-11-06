package org.mposolda.drools.uripolicytest;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public enum AuthorizationDecision {
    ACCEPT, REJECT, IGNORE;

    public AuthorizationDecision mergeDecision(AuthorizationDecision newDecision) {
        if (newDecision == AuthorizationDecision.REJECT || this == AuthorizationDecision.REJECT) {
            return AuthorizationDecision.REJECT;
        } else if (newDecision == AuthorizationDecision.ACCEPT || this == AuthorizationDecision.ACCEPT) {
            return AuthorizationDecision.ACCEPT;
        } else {
            return AuthorizationDecision.IGNORE;
        }
    }
}

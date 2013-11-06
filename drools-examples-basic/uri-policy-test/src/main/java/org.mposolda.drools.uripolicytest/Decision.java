package org.mposolda.drools.uripolicytest;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public enum Decision {
    ACCEPT, REJECT, IGNORE;

    public Decision mergeDecision(Decision newDecision) {
        if (newDecision == Decision.REJECT || this == Decision.REJECT) {
            return Decision.REJECT;
        } else if (newDecision == Decision.ACCEPT || this == Decision.ACCEPT) {
            return Decision.ACCEPT;
        } else {
            return Decision.IGNORE;
        }
    }
}

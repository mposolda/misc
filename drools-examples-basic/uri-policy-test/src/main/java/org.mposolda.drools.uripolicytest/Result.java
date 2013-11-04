package org.mposolda.drools.uripolicytest;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Result {

    private Decision current = Decision.IGNORE;

    public void mergeDecision(Decision newDecision) {
        Decision old = current;
        if (newDecision == Decision.REJECT) {
            current = Decision.REJECT;
        } else if (newDecision == Decision.ACCEPT && current == Decision.IGNORE) {
            current = Decision.ACCEPT;
        }

        System.out.println("Merging decision: old=" + old + ", new=" + current);
    }

    public Decision getDecision() {
        return current;
    }
}

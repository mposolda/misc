package org.mposolda.drools.uripolicytest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Result {

    private Decision current = Decision.IGNORE;
    private Set<String> processedRules = new HashSet<String>();
    private int lastProcessedPriority;

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

    public String addProcessedRule(String rule) {
        processedRules.add(rule);
        return rule;
    }

    public boolean isAlreadyProcessedRule(String rule) {
        return processedRules.contains(rule);
    }

    public int getLastProcessedPriority() {
        return lastProcessedPriority;
    }

    public void setLastProcessedPriority(int lastProcessedPriority) {
        this.lastProcessedPriority = lastProcessedPriority;
    }
}

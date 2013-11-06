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
        System.out.println("Merging decision: old=" + current + ", new=" + newDecision);
        current = current.mergeDecision(newDecision);
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

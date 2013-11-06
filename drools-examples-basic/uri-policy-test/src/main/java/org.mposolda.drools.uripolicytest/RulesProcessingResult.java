package org.mposolda.drools.uripolicytest;

import java.util.HashSet;
import java.util.Set;

/**
 * Object bound to Drools engine, which encapsulate info about authorization decision, all processed rules and rule
 * with last processed priority
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RulesProcessingResult {

    private AuthorizationDecision current = AuthorizationDecision.IGNORE;
    private Set<String> processedRules = new HashSet<String>();
    private int lastProcessedPriority;

    public void mergeDecision(AuthorizationDecision newDecision) {
        System.out.println("Merging decision: old=" + current + ", new=" + newDecision);
        current = current.mergeDecision(newDecision);
    }

    public AuthorizationDecision getDecision() {
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

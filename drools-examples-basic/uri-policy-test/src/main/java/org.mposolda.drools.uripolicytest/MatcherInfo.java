package org.mposolda.drools.uripolicytest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MatcherInfo {

    private boolean processed;
    private boolean matched;
    private List<String> groups = new ArrayList<String>();

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public void addGroup(String group) {
        groups.add(group);
    }

    // Starting from 1
    public String group(int i) {
        if (groups.size() > i) {
            return groups.get(i);
        } else {
            return null;
        }
    }

    public String toString() {
        return new StringBuilder("MatcherInfo [processed=")
                .append(processed)
                .append(", matched=")
                .append(matched)
                .append(", groups=")
                .append(groups)
                .append("]").toString();
    }
}

package org.mposolda.drools.uripolicytest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MatcherInfo {

    private final boolean matched;
    private final List<String> groups = new ArrayList<String>();

    public MatcherInfo(boolean matched) {
        this.matched = matched;
    }

    public boolean getMatched() {
        return matched;
    }

    public void addGroup(String group) {
        groups.add(group);
    }

    public String get(int i) {
        return groups.get(i);
    }

    public String toString() {
        return new StringBuilder("MatcherInfo [matched=")
                .append(matched)
                .append(", groups=")
                .append(groups)
                .append("]").toString();
    }
}

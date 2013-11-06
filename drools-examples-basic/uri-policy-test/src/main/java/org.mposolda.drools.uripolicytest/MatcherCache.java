package org.mposolda.drools.uripolicytest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MatcherCache {

    private Map<String, MatcherInfo> cache = new HashMap<String, MatcherInfo>();

    public MatcherInfo matcherInfo(String key) {
        MatcherInfo mi = cache.get(key);
        if (mi == null) {
            mi = new MatcherInfo();
            cache.put(key, mi);
        }
        return mi;
    }
}

package org.mposolda.drools.uripolicytest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class PatternUtils {

    private static Pattern droolsNormalizationPattern = Pattern.compile("\\{[^{]*\\}");
    private static Pattern wildcardNormalizationPattern = Pattern.compile("\\([^(]*\\)");


    public static void main(String[] args) {
        assertEquals(normalizeString("/something/foo"), "\"/something/foo\"");
        assertEquals(normalizeString("/something/*"), "\"/something/(.*)\"");
        assertEquals(normalizeString("/something1/*/kokos/*"), "\"/something1/(.*)/kokos/(.*)\"");
        assertEquals(normalizeString("/something1/([abc].*)/part2/*"), "\"/something1/([abc].*)/part2/(.*)\"");
        assertEquals(normalizeString("*/something1/([abc].*)/part2/(.*)"), "\"(.*)/something1/([abc].*)/part2/(.*)\"");
        assertEquals(normalizeString("(.*)/something1/([abc].*)/part2/(.*)"), "\"(.*)/something1/([abc].*)/part2/(.*)\"");
        assertEquals(normalizeString("/something1/{$token.username}/foo"), "\"/something1/\" + $token.username + \"/foo\"");
        assertEquals(normalizeString("/something1/{$token.username}"), "\"/something1/\" + $token.username");
        assertEquals(normalizeString("{$token.app}/something1/{$token.username}"), "$token.app + \"/something1/\" + $token.username");
        assertEquals(normalizeString("{$token.app}/something1"), "$token.app + \"/something1\"");
        assertEquals(normalizeString("{$token.app}"), "$token.app");
        assertEquals(normalizeString("/something1/{ any($token.realmRoles)}/foo"), "\"/something1/\" +  any($token.realmRoles) + \"/foo\"");
        assertEquals(normalizeString("/something1/{ any($token.applicationRoles)}"), "\"/something1/\" +  any($token.applicationRoles)");
        assertEquals(normalizeString("/something1/{$token.username}/foo/*/bar/(.*)"), "\"/something1/\" + $token.username + \"/foo/(.*)/bar/(.*)\"");

        System.out.println("Everything correct!!!");
    }

    /**
     * Normalize pattern. so that it can be used in Drools.
     * Examples: see testsuite.
     * NOTE: Maybe it's not the best way to do it, but sufficient for now as this code is not critical for performance...
     *
     * @param input
     * @return
     */
    public static String normalizeString(String input) {
        String result = normalizeForDrools(input);
        result = normalizeWildcards(result);
        return result;
    }

    private static String normalizeForDrools(String input) {
        String[] innerAr = droolsNormalizationPattern.split(input);
        List<String> outer = new ArrayList<String>();

        Matcher m = droolsNormalizationPattern.matcher(input);
        boolean startWith = false;
        while (m.find()) {
            if (m.start() == 0) {
                startWith = true;
            }
            outer.add(m.group().replace("{", "").replace("}", ""));
        }

        List<String> inner = new ArrayList<String>();
        for (int i=0 ; i<innerAr.length ; i++) {
            if (i > 0 || !startWith) {
                inner.add(innerAr[i]);
            }
        }

        StringBuilder result = new StringBuilder();
        if (startWith) {
            result.append(outer.get(0));
            if (inner.size() > 0) {
                result.append(" + ");
            }
            outer.remove(0);
        }

        for (int i=0 ; i<inner.size() ; i++) {
            String currentInner = inner.get(i);
            String currentOuter = null;
            if (outer.size() > i) {
                currentOuter = outer.get(i);
            }

            result.append("\"").append(currentInner).append("\"");
            if (currentOuter != null) {
                result.append(" + ").append(currentOuter);
            }

            if (i != inner.size()-1) {
                result.append(" + ");
            }
        }

        return result.toString();
    }

    private static String normalizeWildcards(String input) {

        String[] inner = wildcardNormalizationPattern.split(input);

        List<String> outer = new ArrayList<String>();
        Matcher m = wildcardNormalizationPattern.matcher(input);
        while (m.find()) {
            outer.add(m.group());
        }

        StringBuilder result = new StringBuilder();

        for (int i=0 ; i<inner.length ; i++) {
            String currentInner = inner[i];
            String currentOuter = null;
            if (outer.size() > i) {
                currentOuter = outer.get(i);
            }

            result.append(currentInner.replaceAll("\\*", "(.*)"));
            if (currentOuter != null) {
                result.append(currentOuter);
            }
        }

        return result.toString();
    }

    private static void assertEquals(String actual, String expected) {
        if (!actual.equals(expected)) {
            throw new IllegalArgumentException("actual: " + actual + ", expected: " + expected);
        }
    }
}

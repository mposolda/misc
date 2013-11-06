package org.mposolda.drools.uripolicytest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to format URI strings from "user-friendly" form to "drools" form, which will be used in drools templates.
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DroolsFormattingUtils {

    private static Pattern droolsNormalizationPattern = Pattern.compile("\\{[^{]*\\}");
    private static Pattern wildcardNormalizationPattern = Pattern.compile("\\([^(]*\\)");
    private static Pattern wildcardReplacePattern = Pattern.compile("\\*");


    // TODO: Move to unit test
    public static void main(String[] args) {
        assertEquals(formatStringToDrools("/something/foo"), "\"/something/foo\"");
        assertEquals(formatStringToDrools("/something/*"), "\"/something/(.*)\"");
        assertEquals(formatStringToDrools("/something1/*/kokos/*"), "\"/something1/(.*)/kokos/(.*)\"");
        assertEquals(formatStringToDrools("/something1/([abc].*)/part2/*"), "\"/something1/([abc].*)/part2/(.*)\"");
        assertEquals(formatStringToDrools("*/something1/([abc].*)/part2/(.*)"), "\"(.*)/something1/([abc].*)/part2/(.*)\"");
        assertEquals(formatStringToDrools("(.*)/something1/([abc].*)/part2/(.*)"), "\"(.*)/something1/([abc].*)/part2/(.*)\"");
        assertEquals(formatStringToDrools("/something1/{$token.username}/foo"), "\"/something1/\" + $token.username + \"/foo\"");
        assertEquals(formatStringToDrools("/something1/{$token.username}"), "\"/something1/\" + $token.username");
        assertEquals(formatStringToDrools("{$token.app}/something1/{$token.username}"), "$token.app + \"/something1/\" + $token.username");
        assertEquals(formatStringToDrools("{$token.app}/something1"), "$token.app + \"/something1\"");
        assertEquals(formatStringToDrools("{$token.app}"), "$token.app");
        assertEquals(formatStringToDrools("/something1/{ any($token.realmRoles)}/foo"), "\"/something1/\" +  any($token.realmRoles) + \"/foo\"");
        assertEquals(formatStringToDrools("/something1/{ any($token.applicationRoles)}"), "\"/something1/\" +  any($token.applicationRoles)");
        assertEquals(formatStringToDrools("/something1/{$token.username}/foo/*/bar/(.*)"), "\"/something1/\" + $token.username + \"/foo/(.*)/bar/(.*)\"");

        System.out.println("Everything correct!!!");
    }

    /**
     * Format pattern from "user-friendly" form to Drools-friendly form. See testsuite for examples
     * NOTE: Maybe it's not the best way to do it, but sufficient for now as this code is not critical for performance...
     *
     * @param input
     * @return
     */
    public static String formatStringToDrools(String input) {
        String result = formatForDrools(input);
        result = formatWildcards(result);
        return result;
    }

    // Replace string like 'foo/${bar}/baz' with something like '"foo" + $bar + "baz"'
    private static String formatForDrools(String input) {
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

        // Move items from Array to List. Remove very first String if it is ""
        List<String> inner = new ArrayList<String>();
        for (int i=0 ; i<innerAr.length ; i++) {
            if (i > 0 || !startWith) {
                inner.add(innerAr[i]);
            }
        }

        // Case when whole input starts with {foo-like} prefix
        StringBuilder result = new StringBuilder();
        if (startWith) {
            result.append(outer.get(0));
            if (inner.size() > 0) {
                result.append(" + ");
            }
            outer.remove(0);
        }

        // Main algorithm
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

    // Replace string like "foo/*/bar/(.*)" with something like "foo/(.*)/bar/(.*)" (IE: * is replaced with (.*) but
    // only if it's not already part of regex.)
    private static String formatWildcards(String input) {

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

            result.append(wildcardReplacePattern.matcher(currentInner).replaceAll("(.*)"));
            if (currentOuter != null) {
                result.append(currentOuter);
            }
        }

        return result.toString();
    }

    // TODO: remove and use unit test
    private static void assertEquals(String actual, String expected) {
        if (!actual.equals(expected)) {
            throw new IllegalArgumentException("actual: " + actual + ", expected: " + expected);
        }
    }
}

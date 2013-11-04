package org.mposolda.drools.uripolicytest.regextest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RegexTest {

    public static void main(String[] args) {
        test("^/neco/(.*)/ml\\?ok$", "/neco/spmething/koleso/ml?ok");

        test("^/neco/(neco1|neco2)/neco3$", "/neco/neco2/neco3");
        test("^/neco/neco2/neco3$", "/neco/neco2/neco3");
    }

    private static void test(String regex, String textToTest) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(textToTest);
        while (m.find()) {
            System.out.println("Got it! START: " + m.start() + ", END: " + m.end() + ", GROUP: " + m.group());
            System.out.println(m.groupCount());
            System.out.println("Got it! START: " + m.start(1) + ", END: " + m.end(1) + ", GROUP: " + m.group(1));
        }
    }
}

package org.jboss;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RegexTest {


    private static final String HTML_1 = "<html><head><title>foo</title></HEAD><body></body></html>";

    public static void main(String[] args) {
        Pattern p = Pattern.compile("</[hH][eE][aA][dD]>");
        Matcher m = p.matcher(HTML_1);


        if (m.find()) {
            int startt = m.start();
            String javascript = getJavascriptText("auth", "456");

            String newResponse = new StringBuilder(HTML_1.substring(0, startt))
                    .append(javascript )
                    .append(HTML_1.substring(startt))
                    .toString();
            System.out.println(newResponse );

        }
    }

    private static String getJavascriptText(String flowPath, String execution) {
        return new StringBuilder("<SCRIPT>")
                .append(" if (typeof history.replaceState === 'function') {")
                .append("  history.replaceState({}, \"execution " + execution + "\", \"" + flowPath + "?execution=" + execution + "\");")
                .append(" }")
                .append("</SCRIPT>")
                .toString();
    }
}

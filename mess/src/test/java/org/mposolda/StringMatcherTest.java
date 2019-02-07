package org.mposolda;

import java.util.regex.Pattern;

import org.jboss.regex.StringMatcher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringMatcherTest {
    private StringMatcher m;

    @Before
    public void setup(){
        m = new StringMatcher();
    }

    @Test
    public void testIsTrue() {
        assertTrue(m.isTrue("true"));
        assertFalse(m.isTrue("true2"));
        assertFalse(m.isTrue("True"));
    }

    @Test
    public void testIsTrueVersion2() {
        assertTrue(m.isTrueVersion2("true"));
        assertFalse(m.isTrueVersion2("true2"));
        assertTrue(m.isTrueVersion2("True"));;
    }

    @Test
    public void testIsTrueOrYes() {
        assertTrue(m.isTrueOrYes("true"));
        assertTrue(m.isTrueOrYes("yes"));
        assertTrue(m.isTrueOrYes("Yes"));
        assertFalse(m.isTrueOrYes("no"));
    }

    @Test
    public void testContainsTrue() {
        assertTrue(m.containsTrue("thetruewithin"));
    }

    @Test
    public void testIsThreeLetters() {
        assertTrue(m.isThreeLetters("abc"));
        assertFalse(m.isThreeLetters("abcd"));
    }

    @Test
    public void testisNoNumberAtBeginning() {
        assertTrue(m.isNoNumberAtBeginning("abc"));
        assertFalse(m.isNoNumberAtBeginning("1abcd"));
        assertTrue(m.isNoNumberAtBeginning("a1bcd"));
        assertTrue(m.isNoNumberAtBeginning("asdfdsf"));
    }

    @Test
    public void testisIntersection() {
        assertTrue(m.isIntersection("1"));
        assertFalse(m.isIntersection("abcksdfkdskfsdfdsf"));
        assertTrue(m.isIntersection("skdskfjsmcnxmvjwque484242"));
    }

    @Test
    public void testLessThenThreeHundred() {
        assertTrue(m.isLessThenThreeHundred("288"));
        assertFalse(m.isLessThenThreeHundred("3288"));
        assertFalse(m.isLessThenThreeHundred("328 8"));
        assertTrue(m.isLessThenThreeHundred("1"));
        assertTrue(m.isLessThenThreeHundred("99"));
        assertFalse(m.isLessThenThreeHundred("300"));
    }


    // Other tests


    @Test
    public void testSimpleTrue() {
        String pattern = "\\d\\d\\d[,\\s]?\\d\\d\\d\\d";
        String s= "1233323322";
        assertFalse(s.matches(pattern));
        s = "1233323";
        assertTrue(s.matches(pattern));
        s = "123 3323";
        assertTrue(s.matches(pattern));
    }


    @Test
    public void testNumbers() {
        Pattern p = Pattern.compile("(.*[^0-9])?[0-9]{3}([^0-9].*)?");

        assertTrue(p.matcher("123").matches());
        assertTrue(p.matcher("123 some").matches());
        assertTrue(p.matcher("some 123 some").matches());
        assertTrue(p.matcher("some 123").matches());

        assertFalse(p.matcher("12").matches());
        assertFalse(p.matcher("some 12 some").matches());
        assertFalse(p.matcher("some 1234 some").matches());
        assertFalse(p.matcher("some 1234").matches());
    }

    @Test
    public void testBoundary() {
        Pattern p = Pattern.compile("<a\\b.*");

        assertFalse(p.matcher("<ab").matches());
        assertTrue(p.matcher("<a ").matches());
        assertTrue(p.matcher("<a sfsd").matches());
    }

}

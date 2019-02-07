package org.jboss.regex;

public class StringMatcher {

    // returns true if the string matches exactly "true"
    public boolean isTrue(String s){
        return s.matches("true");
    }
    // returns true if the string matches exactly "true" or "True"
    public boolean isTrueVersion2(String s){
        return s.matches("[tT]rue");
    }

    // returns true if the string matches exactly "true" or "True"
    // or "yes" or "Yes"
    public boolean isTrueOrYes(String s){
        return s.matches("[tT]rue|[yY]es");
    }

    // returns true if the string contains exactly "true"
    public boolean containsTrue(String s){
        return s.matches(".*true.*");
    }


    // returns true if the string contains of three letters
    public boolean isThreeLetters(String s){
        return s.matches("[a-zA-Z]{3}");
        // simpler from for
//      return s.matches("[a-Z][a-Z][a-Z]");
    }



    // returns true if the string does not have a number at the beginning
    public boolean isNoNumberAtBeginning(String s){
        return s.matches("^[^\\d].*");
    }
    // returns true if the string contains a arbitrary number of characters except b
    public boolean isIntersection(String s){
        return s.matches("[\\w&&[^b]]*");
    }
    // returns true if the string contains a number less than 300
    public boolean isLessThenThreeHundred(String s){
        return s.matches("[^0-9]*[12]?[0-9]{1,2}[^0-9]*");
    }

}

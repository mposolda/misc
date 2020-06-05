package org.mposolda;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Calculator {

    public static void main(String[] args) {
        System.out.println("Hellol");

        double startMoney = 1500000;
        double years = 20;

        List<FlatContext> flats = Arrays.asList(
                new FlatContext("Usti nad Labem", 700000, 9),
                new FlatContext("Brno", 2390000, 25),
                new FlatContext("Olomouc", 1650000, 19),
                new FlatContext("Ostrava", 970000, 11)
        );

        for (FlatContext flat : flats) {
            printCityReport(startMoney, flat, years);
        }

    }

    private static void printCityReport(double startMoney, FlatContext flat, double yearsFinal) {
        double finalMoney = computeMoneyInXYears(startMoney, flat, yearsFinal);
        String report = String.format("City: %s, returnity: %s, finalMoney: %s Kc", flat.city, String.valueOf(flat.getReturnity()), String.valueOf(Math.round(finalMoney)));
        System.out.println(report);
    }


    private static double computeMoneyInXYears(double startMoney, FlatContext flat, double yearsFinal) {
        double returnity = flat.getReturnity();
        double result = startMoney * Math.pow(returnity, yearsFinal);
        return result;
    }


    private static class FlatContext {

        private final String city;

        // Price of flat
        private final double flatPrice;

        // Years in which flat will be completely ours (mortgate will be completely payed by the money from "najem")
        private final double flatYears;

        public FlatContext(String city, double flatPrice, double flatYears) {
            this.city = city;
            this.flatPrice = flatPrice;
            this.flatYears = flatYears;
        }


        public double getReturnity() {
            double exp = 1d/flatYears;
            double result = Math.pow(5, exp); // 5 is here due the 20% initial "vklad" for the mortgage. This one is constant
            return result;
        }

    }
}

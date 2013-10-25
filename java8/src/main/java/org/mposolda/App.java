package org.mposolda;

import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        MathOperation addition = (p1, p2) -> {
            return p1+p2;
        };
        MathOperation subtraction = (p1, p2) -> p1 - p2;

        MathOperation multipl = Multiplicator::multiply;
        MathOperation divver = new Divver()::div;

        System.out.println(addition.execute(20, 10));
        System.out.println(subtraction.execute(20, 10));
        System.out.println(multipl.execute(10, 20));
        System.out.println(divver.execute(10, 2));
        System.out.println(generate(Integerr::new));

        Arrays.sort(new ComparableKokos[] {new ComparableKokos().setA(1), new ComparableKokos().setA(5)});
    }

    private static class Multiplicator {

        public static int multiply(int a, int b) {
            return a * b;
        }
    }

    private static class Divver {

        public int div(int a, int b) {
            return a/b;
        }
    }

    private static class ComparableKokos implements Comparable<ComparableKokos> {
        int a;

        ComparableKokos setA(int a) {
            this.a = a;
            return this;
        }

        @Override
        public int compareTo(ComparableKokos o) {
            return a-o.a;
        }
    }

    private static Integerr generate(IntGenerator gen) {
        return gen.generateNew();
    }
}

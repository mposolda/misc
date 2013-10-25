package org.mposolda.samples;

import java.util.function.Consumer;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ConsumerExample2 {

    public static int[] pole = {10, 20, 30, 40, 50};

    public static void main(String[] args) {
        Consumer<OpContext> outputter = (ctx) -> {
            System.out.println("" + ctx.arg1 + ctx.operator + ctx.arg2 + '=' + ctx.op.execute(ctx.arg1, ctx.arg2));
        };

        Consumer<RepeaterContext> repeater = (repCtx) -> {
            for (int i=0 ; i<pole.length ; i++) {
                for (int j=0 ; j<pole.length ; j++) {
                    repCtx.consumer.accept(new OpContext(pole[i], pole[j], repCtx.opChar, repCtx.operator));
                }
            }
        };

        repeater.accept(new RepeaterContext(outputter, '+', (a,b) -> a+b));
        repeater.accept(new RepeaterContext(outputter, '-', (a,b) -> a-b));
        repeater.accept(new RepeaterContext(outputter, '*', (a,b) -> a*b));
    }

    private static class OpContext {
        private int arg1;
        private int arg2;
        private char operator;
        private Operator op;

        public OpContext(int arg1, int arg2, char operator, Operator op) {
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.operator = operator;
            this.op = op;
        }
    }

    private static class RepeaterContext {

        private final Consumer<OpContext> consumer;
        private final char opChar;
        private final Operator  operator;

        public RepeaterContext(Consumer<OpContext> consumer, char opChar, Operator operator) {
            this.consumer = consumer;
            this.opChar = opChar;
            this.operator = operator;
        }
    }

    @FunctionalInterface
    private static interface Operator {

         int execute(int a, int b);
    }
}

package org.mposolda.samples;

import java.util.function.Consumer;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ConsumerExample {

    public static void main(String[] args) {
         Consumer<Integer> c = (a) -> {
             System.out.println(a);
         };
        c = c.andThen((b) -> System.out.println(b + b));

        c.accept(5);
    }
}

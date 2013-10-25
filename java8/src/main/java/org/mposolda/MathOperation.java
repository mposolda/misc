package org.mposolda;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@FunctionalInterface
public interface MathOperation {

    int execute(int a, int b);

    default int execute2(int a, int b, int c) {
        return execute(execute(a,b), c);
    }
}

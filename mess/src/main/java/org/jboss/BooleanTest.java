package org.jboss;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class BooleanTest {

    static Boolean managed = null;

    public static void main(String[] args) {
        if (managed != null && !managed) {
            System.out.println("I am here");
        }
    }
}

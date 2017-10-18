package org.jboss;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SecureRandomTest {

//    public static void main(String[] args) throws NoSuchAlgorithmException {
//        SecureRandom.getInstance("SHA1PRNG").nextBytes(new byte[16]);
//    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        for (int i=0 ; i<10 ; i++) {
            System.out.println(new Random().nextInt(2048));
        }
    }
}

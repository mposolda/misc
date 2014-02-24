package org.jboss;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SecureRandomTest {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        SecureRandom.getInstance("SHA1PRNG").nextBytes(new byte[16]);
    }
}

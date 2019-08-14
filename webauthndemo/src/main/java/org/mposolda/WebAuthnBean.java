package org.mposolda;

import java.util.UUID;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class WebAuthnBean {

    public static String test() {
        String random = UUID.randomUUID().toString().substring(0, 8);
        System.out.println("Generated secret: " + random);
        return "Generated random challenge: " + random;
    }


}

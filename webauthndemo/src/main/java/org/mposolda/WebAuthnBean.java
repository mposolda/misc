package org.mposolda;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.keycloak.common.util.Base64Url;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class WebAuthnBean {

    public static String test() {
        return "Generated random challenge: " + generateBase64UrlEncodedSecret();
    }


    public static String generateChallenge() {
        return generateBase64UrlEncodedSecret();
    }


    public static String generateUserId() {
        return generateBase64UrlEncodedSecret();
    }


    public static String getUsername() {
        return "john";
    }


    public static String getDisplayName() {
        return "John Doel";
    }


    private static String generateBase64UrlEncodedSecret() {
        try {
            String random = UUID.randomUUID().toString();
            String result = Base64Url.encode(random.getBytes("UTF-8"));

            System.out.println("Generated random challenge: " + result);
            return result;
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
    }


}

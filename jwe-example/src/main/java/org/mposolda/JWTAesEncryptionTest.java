package org.mposolda;

import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.keycloak.common.util.Base64Url;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JWTAesEncryptionTest {

    static {
        if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
    }


    private static final ObjectMapper mapper = new ObjectMapper();


    public static void main(String[] args) throws Exception {
        // Generate key
        byte[] aesKey = Base64Url.decode("GawgguFyGrWKav7AX4VKUg");
        SecretKeySpec aesKeySpec = new SecretKeySpec(aesKey, "AES");

        // USe this for testing output (functionality)
        testMe(aesKeySpec, 1, true);

        // Use this for testing performance
        //testMe(aesKeySpec, 100000, false);
    }

    public static void testMe(SecretKeySpec aesKeySpec, int iterations, boolean outputStuff) throws Exception {

        long time = System.currentTimeMillis();

        for (int i=0 ; i<iterations ; i++) {
            Map<String, Object> authenticatedSession = createCodeJWT();
            byte[] payloadBytes = mapper.writeValueAsBytes(authenticatedSession);

            byte[] initializationVector = generateSecret(16);

            String encrypted = encryptJWE(aesKeySpec, initializationVector, payloadBytes);

            byte[] decrypted = decryptJWE(aesKeySpec, initializationVector, encrypted);

            Map<Object, Object> decryptedSession = mapper.readValue(decrypted, Map.class);

            if (outputStuff) {
                String json = mapper.writeValueAsString(authenticatedSession);
                System.out.println("JSON: " + json);
                System.out.println("JSON Length: " + json.length());

                System.out.println("Encrypted: " + encrypted);
                System.out.println("Encrypted Length: " + encrypted.length());

                String decryptedSessionJson = mapper.writeValueAsString(decryptedSession);

                System.out.println("Decrypted JSON: " + decryptedSessionJson);
                if (!decryptedSessionJson.equals(json)) {
                    throw new IllegalStateException("They are not equals!!!");
                }
            }

        }

        long took = System.currentTimeMillis() - time;
        System.out.println("Iterations: " + iterations + ", took: " + took + " ms");
    }


    private static Map<String, Object> createCodeJWT() {
        Map<String, Object> result = new HashMap<>();

        List<String> protocolMappers = Arrays.asList("4b1b73fa-5ca4-424a-aca8-5251ea3789e6", "11166acd-7b9c-4e25-938e-5eba1068158e",
                "d8f13d6a-593f-4eb9-8614-0be29edd5787", "3320bfb9-24b6-4bb1-9f76-ae65a360c1e6", "907dcdcc-2974-4356-a172-16a87fae9855", "b22a2542-a9d6-4d44-9111-ce6e2d3869d3");
        result.put("protMappers", protocolMappers);

        List<String> roles = Arrays.asList("4c1b73fa-5ca4-424a-aca8-5251ea3789e6", "12166acd-7b9c-4e25-938e-5eba1068158e",
                "d8f13d6a-593f-4eb9-8614-0be29edd5797", "3320bfb9-24b6-4bb1-9f76-ae65b360c1e6", "907dcdcc-2984-4356-a172-16a87fae9855", "b22b2542-a9d6-4d44-9111-ce6e2d3869d3");

        result.put("roles", roles);

        result.put("uss", "d8f13d6a-593f-4eb9-8614-0be29edd5797");
        result.put("aud", "d8f13d6a-593f-4eb9-8614-0be29edd5798");

        result.put("redu", "http://localhost:8080/foo/bar/baz/dar");

        Map<String, String> notes = new HashMap<>();
        notes.put("scope","openid");
        notes.put("SSO_AUTH","true");
        notes.put("iss","http://localhost:8081/auth/realms/master");
        notes.put("response_type","code");
        notes.put("code_challenge_method" ,"plain");
        notes.put("redirect_uri","http://localhost:8081/auth/admin/master/console/");
        notes.put("state","0833cdef-f188-45aa-9eb3-7cbaf1fd9b99");
        notes.put("nonce","170bad50-c432-44ce-8939-b82d5bce67da");
        notes.put("response_mode","fragment");
        result.put("notes", notes);

        return result;
    }


    private static String encryptJWE(SecretKeySpec secretKeySpec, byte[] initializationVector, byte[] payloadBytes) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        AlgorithmParameterSpec ivParamSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParamSpec);
        byte[] encryptedBytes = cipher.doFinal(payloadBytes);

        return Base64Url.encode(encryptedBytes);
    }


    private static byte[] decryptJWE(SecretKeySpec secretKeySpec, byte[] initializationVector, String encryptedText) throws Exception {
        byte[] encryptedBytes = Base64Url.decode(encryptedText);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        AlgorithmParameterSpec ivParamSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParamSpec);
        byte[] payloadBytes = cipher.doFinal(encryptedBytes);

        return payloadBytes;
    }


    public static byte[] generateSecret(int bytes) {
        byte[] buf = new byte[bytes];
        new SecureRandom().nextBytes(buf);
        return buf;
    }
}

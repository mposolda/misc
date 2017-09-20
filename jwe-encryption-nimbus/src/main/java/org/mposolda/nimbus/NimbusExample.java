package org.mposolda.nimbus;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class NimbusExample {

    public static void main(String[] args) throws Exception {
        JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256);
        JWEObject jweObj = new JWEObject(header, new Payload("Hello world! How are you man? I hope you are fine. This is some quite a long text, which is much longer than just simple 'Hello World'"));

        byte[] key128 = new byte[] { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 13, 14, 15, 16 };
        byte[] key256 = new byte[] { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 13, 14, 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        //byte[] key = key128;
        byte[] key = key256;

        DirectEncrypter encrypter = new DirectEncrypter(key);

        // You can now use the encrypter on one or more JWE objects
        // that you wish to secure
        jweObj.encrypt(encrypter);
        String jweString = jweObj.serialize();

        System.out.println(jweString);


        DirectDecrypter decrypter = new DirectDecrypter(key);
        jweObj = JWEObject.parse(jweString);
        jweObj.decrypt(decrypter);
        System.out.println("Decrypted payload: " + jweObj.getPayload().toString());
    }
}

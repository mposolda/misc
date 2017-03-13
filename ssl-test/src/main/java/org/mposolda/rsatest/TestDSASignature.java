package org.mposolda.rsatest;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class TestDSASignature {

    static {
        if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
    }


    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "BC");
        //keyGen.initialize(512, new SecureRandom());
        keyGen.initialize(512);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();


        byte[] dsaSignature = sign(privateKey, "Hello Joe");
        System.out.println("dsaSignature length=" + dsaSignature.length);

        // This should pass
        boolean verify1 = verify(publicKey, "Hello Joe", dsaSignature);

        // This should fail
        boolean verify2 = verify(publicKey, "This will fail!!!", dsaSignature);

        System.out.println("verify1=" + verify1 + ", verify2=" + verify2);


    }

    private static byte[] sign(PrivateKey privateKey, String message) throws Exception {
        byte[] input = message.getBytes("UTF-8");

        Signature signature = Signature.getInstance("DSA", "BC");
        signature.initSign(privateKey);
        signature.update(input);
        return signature.sign();
    }

    private static boolean verify(PublicKey publicKey, String message, byte[] dsaSignature) throws Exception {
        byte[] input = message.getBytes("UTF-8");

        Signature signature = Signature.getInstance("DSA", "BC");
        signature.initVerify(publicKey);
        signature.update(input);
        return signature.verify(dsaSignature);
    }
}

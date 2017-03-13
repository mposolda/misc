package org.mposolda.rsatest;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.keycloak.common.util.Base64Url;

/**
 * Test RSA encryption and also playing with manually creating RSA signature
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class TestRSAEncrypt {

    static {
        if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
    }


    public static void main(String[] args) throws Exception {
        String input = "Hello Joe";

        byte[] bytes = input.getBytes("UTF-8");
        System.out.println("Bytes back: " + new String(bytes, "UTF-8"));

        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(2048);
        final KeyPair keys = keyGen.generateKeyPair();
        PublicKey publicKey = keys.getPublic();
        PrivateKey privateKey = keys.getPrivate();

        System.out.println("Hello");

        byte[] encryptedBytes = encrypt(publicKey, input.getBytes("UTF-8"));
        String encryptedText = Base64Url.encode(encryptedBytes);
        byte[] encryptedBytes2 = Base64Url.decode(encryptedText);
        System.out.println("ENCRYPTED: " + encryptedText);

        byte[] decryptedBytes = decrypt(privateKey, encryptedBytes2);
        System.out.println("DECRYPTED: " + new String(decryptedBytes, "UTF-8"));




        // Now try with the digest
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(input.getBytes("UTF-8"));
        byte[] digest = md.digest();

        // Adding some padding. In practice, this must be random!!!
        byte[] paddedDigest = Arrays.copyOf(digest, 256);
        byte[] signature = decrypt(privateKey, paddedDigest);

        byte[] encryptedSignature = encrypt(publicKey, signature);
        byte[] unpaddedEncryptedSignature = Arrays.copyOf(encryptedSignature, 32);
        System.out.println("Digest: " + Base64Url.encode(digest));
        System.out.println("unpaddedEncryptedSignature: " + Base64Url.encode(unpaddedEncryptedSignature));


    }

    private static byte[] decrypt(PrivateKey privateKey, byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encrypted);
    }

    private static byte[] encrypt(PublicKey publicKey, byte[] unencrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(unencrypted);
    }
}

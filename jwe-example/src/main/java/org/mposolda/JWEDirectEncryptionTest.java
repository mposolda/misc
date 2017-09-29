package org.mposolda;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.keycloak.common.util.Base64Url;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JWEDirectEncryptionTest {

    static {
        if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {
        String payload = "Hello world! How are you man? I hope you are fine. This is some quite a long text, which is much longer than just simple 'Hello World'";

        byte[] hmacSHA256key = new byte[] { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 13, 14, 15, 16 };
        byte[] aes128key =  new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

        //byte[] hmacSHA512key = new byte[] { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 13, 14, 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        //byte[] aes256key =  new byte[] { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 13, 14, 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

        String initializationVector = "85kfvBWwrkfyZ7Hb26DB3A";

        SecretKeySpec aesKeySpec = new SecretKeySpec(aes128key, "AES");
        SecretKeySpec hmacKeySpec = new SecretKeySpec(hmacSHA256key, "HMACSHA2");

        long start = System.currentTimeMillis();
        for (int i=0 ; i<1 ; i++) {
            //String encrypted = encrypt(payload, initializationVector, aesKeySpec, hmacKeySpec);
            String encrypted = "eyJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..JRHAnPiPbNhcel2zHmHc4A.u-uXO5PAcBLK1Q7f0MP6i_tIzBpDxUChVyNvdbf6te-bDlq96LvJqvPoesxSjnScVxGfO3jT8wQmUjc7k7xZ0vG_ssLJtjaizhuoME_d2EWRlHlJLd2tHIJFUZ7gK0f4SGgN4lnCMylypOzy4S0x9-m4sBkU9-982cQLEqbs6NLK7Vo52JvF39YcVJZqFajQ.xAEsLduTkL7HZHnXokc-JQ";
            String decrypted = decrypt(encrypted, aesKeySpec, hmacKeySpec);
        }
        long took = System.currentTimeMillis() - start;
        System.out.println(took + " ms");
        //String decrypted = decrypt(encrypted);

        //System.out.println(decrypted);
    }


    private static String encrypt(String payload, String initializationVector, SecretKeySpec aesKeySpec, SecretKeySpec hmacKeySpec) throws Exception {
        // FIXME
        //String header = "{\"alg\":\"dir\",\"enc\":\"A128CBC-HS256\"}";
        String header = "{\"enc\":\"A128CBC-HS256\",\"alg\":\"dir\"}";
        //System.out.println("Header: " + header);

        String encodedHeader = Base64Url.encode(header.getBytes("UTF-8"));
        //System.out.println("Encoded header: " + encodedHeader);

        byte[] payloadBytes = payload.getBytes("UTF-8");

        byte[] ivBytes = Base64Url.decode(initializationVector);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        AlgorithmParameterSpec ivParamSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, aesKeySpec, ivParamSpec);
        byte[] encryptedBytes = cipher.doFinal(payloadBytes);

        // A.3.5
        byte[] aadBytes = encodedHeader.getBytes("UTF-8");
       // System.out.println(aadBytes);

        // B.3
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
        int aadLengthInBits = aadBytes.length * 8;
        b.putInt(aadLengthInBits);
        byte[] result1 = b.array();
        byte[] aadBigEndian = new byte[8];
        System.arraycopy(result1, 0, aadBigEndian, 4, 4);

        // B.5
        byte[] concatenatedHmacInput = new byte[aadBytes.length + ivBytes.length + encryptedBytes.length + aadBigEndian.length];
        System.arraycopy(aadBytes, 0, concatenatedHmacInput, 0, aadBytes.length);
        System.arraycopy(ivBytes, 0, concatenatedHmacInput, aadBytes.length, ivBytes.length );
        System.arraycopy(encryptedBytes, 0, concatenatedHmacInput, aadBytes.length + ivBytes.length , encryptedBytes.length);
        System.arraycopy(aadBigEndian, 0, concatenatedHmacInput, aadBytes.length + ivBytes.length + encryptedBytes.length, aadBigEndian.length);


        // B.6
        // FIXME
        Mac macImpl = Mac.getInstance("HMACSHA256");
        macImpl.init(hmacKeySpec);
        macImpl.update(concatenatedHmacInput);
        byte[] macEncoded =  macImpl.doFinal();

        // FIXME
        byte[] aadOutput = Arrays.copyOf(macEncoded, 32);

        // A.3.6 - continue
        String cipherText = Base64Url.encode(encryptedBytes);
        //System.out.println("cipherText: " + cipherText);

        String aadOutputText= Base64Url.encode(aadOutput);
        //System.out.println("aadOutputText: " + aadOutputText);

        String jwe = encodedHeader + ".."
                + initializationVector + "."
                + cipherText + "."
                + aadOutputText;

        //System.out.println("JWE: " + jwe);

        return jwe;

    }





    private static String decrypt(String encrypted, SecretKeySpec aesKeySpec, SecretKeySpec hmacKeySpec) throws Exception {
        String[] parts = encrypted.split("\\.");
        String header = parts[0];
        String iv = parts[2];
        String cipherText = parts[3];
        String at = parts[4];

        // Check the key lengths TODO

        // Compare the hash
        byte[] aadBytes = header.getBytes("UTF-8");
        byte[] ivBytes = Base64Url.decode(iv);

        // B.3
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
        int aadLengthInBits = aadBytes.length * 8;
        b.putInt(aadLengthInBits);
        byte[] result1 = b.array();
        byte[] aadBigEndian = new byte[8];
        System.arraycopy(result1, 0, aadBigEndian, 4, 4);

        byte[] encryptedBytes = Base64Url.decode(cipherText);

        // B.5
        byte[] concatenatedHmacInput = new byte[aadBytes.length + ivBytes.length + encryptedBytes.length + aadBigEndian.length];
        System.arraycopy(aadBytes, 0, concatenatedHmacInput, 0, aadBytes.length);
        System.arraycopy(ivBytes, 0, concatenatedHmacInput, aadBytes.length, ivBytes.length );
        System.arraycopy(encryptedBytes, 0, concatenatedHmacInput, aadBytes.length + ivBytes.length , encryptedBytes.length);
        System.arraycopy(aadBigEndian, 0, concatenatedHmacInput, aadBytes.length + ivBytes.length + encryptedBytes.length, aadBigEndian.length);

        // B.6
        // FIXME
        Mac macImpl = Mac.getInstance("HMACSHA256");
        macImpl.init(hmacKeySpec);
        macImpl.update(concatenatedHmacInput);
        byte[] macEncoded =  macImpl.doFinal();

        // FIXME
        byte[] aadOutput = Arrays.copyOf(macEncoded, 16);
        byte[] atBytes = Base64Url.decode(at);
        boolean digitsEqual = MessageDigest.isEqual(aadOutput, atBytes);

        if (!digitsEqual) {
            // TODO
            throw new IllegalArgumentException("Signature validations failed");
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        AlgorithmParameterSpec ivParamSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, aesKeySpec, ivParamSpec);
        byte[] payloadBytes = cipher.doFinal(encryptedBytes);

        String payload = new String(payloadBytes, "UTF-8");
        System.out.println("P: " + payload);
        return payload;
    }
}

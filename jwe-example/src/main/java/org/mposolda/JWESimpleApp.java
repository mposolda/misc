package org.mposolda;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.AESWrapEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.keycloak.common.util.Base64Url;
import org.keycloak.jose.jws.JWSHeader;

/**
 * Encodes JWE payload with AES128KeyWrap and AES-CBC-HMAC-SHA-256
 *
 * See JWE specification and it's attachements for details.
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JWESimpleApp {

    static {
        if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {
        // JWE specs - example A.3
        String payload = "Live long and prosper.";
        System.out.println("Payload plaintext: " + payload);

        byte[] payloadBytes = payload.getBytes("UTF-8");
        for (byte b : payloadBytes) {
            System.out.print(b + ", ");
        }

        // A.3.1
        String header = "{\"alg\":\"A128KW\",\"enc\":\"A128CBC-HS256\"}";
        System.out.println("Header: " + header);

        String encodedHeader = Base64Url.encode(header.getBytes("UTF-8"));
        System.out.println("Encoded heaader: " + encodedHeader);

        // A.3.2 (NOTE: cek has 256 bits)
        int[] cek = new int[] { 4, 211, 31, 197, 84, 157, 252, 254, 11, 100, 157, 250, 63, 170, 106,
                206, 107, 124, 212, 45, 111, 107, 9, 219, 200, 177, 0, 240, 143, 156,
                44, 207};
        // Convert cek to byte array
        byte[] cekBytes = new byte[cek.length];
        for (int i=0 ; i<cek.length ; i++) {
            cekBytes[i] = (byte) cek[i];
        }

        // A.3.3
        String jwkKey = "{\"kty\":\"oct\",\"alg\":\"A128KW\",\"k\":\"GawgguFyGrWKav7AX4VKUg\"}";

        // (NOTE: aes key has 128 bits as requested)
        byte[] aesKey = Base64Url.decode("GawgguFyGrWKav7AX4VKUg");
        SecretKeySpec aesKeySpec = new SecretKeySpec(aesKey, "AES");

        Wrapper encrypter = new AESWrapEngine();
        encrypter.init(true, new KeyParameter(aesKeySpec.getEncoded()));
        byte[] wrappedCek = encrypter.wrap(cekBytes, 0, cekBytes.length);

        String base64EncryptedCEK = Base64Url.encode(wrappedCek);
        System.out.println("base64EncryptedCEK: " + base64EncryptedCEK);

        // A.3.4 (NOTE: initialization vector has 128 bits)
        byte[] initializationVector = new byte[] { 3, 22, 60, 12, 43, 67, 104, 105, 108, 108, 105, 99, 111, 116, 104, 101};
        String initializationVectorEncoded = Base64Url.encode(initializationVector);
        System.out.println("Initialization vector: " + initializationVectorEncoded);

        // A.3.5
        byte[] aadBytes = encodedHeader.getBytes("UTF-8");
        System.out.println(aadBytes);

        // A.3.6

        // B.1
        byte[] cekMacKey = Arrays.copyOf(cekBytes, 16);
        byte[] cekAesKey = Arrays.copyOfRange(cekBytes, 16, 32);

        // B.2
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        SecretKeySpec secretKeySpec = new SecretKeySpec(cekAesKey, "AES");
        AlgorithmParameterSpec ivParamSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParamSpec);
        byte[] encryptedBytes = cipher.doFinal(payloadBytes);

        // B.3
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
        int aadLengthInBits = aadBytes.length * 8;
        b.putInt(aadLengthInBits);
        byte[] result1 = b.array();
        byte[] aadBigEndian = new byte[8];
        System.arraycopy(result1, 0, aadBigEndian, 4, 4);

        // B.5
        byte[] concatenatedHmacInput = new byte[aadBytes.length + initializationVector.length + encryptedBytes.length + aadBigEndian.length];
        System.arraycopy(aadBytes, 0, concatenatedHmacInput, 0, aadBytes.length);
        System.arraycopy(initializationVector, 0, concatenatedHmacInput, aadBytes.length, initializationVector.length);
        System.arraycopy(encryptedBytes, 0, concatenatedHmacInput, aadBytes.length + initializationVector.length, encryptedBytes.length);
        System.arraycopy(aadBigEndian, 0, concatenatedHmacInput, aadBytes.length + initializationVector.length + encryptedBytes.length, aadBigEndian.length);

        // B.6
        Mac macImpl = Mac.getInstance("HMACSHA256");
        macImpl.init(new SecretKeySpec(cekMacKey, macImpl.getAlgorithm()));
        macImpl.update(concatenatedHmacInput);
        byte[] macEncoded =  macImpl.doFinal();

        byte[] aadOutput = Arrays.copyOf(macEncoded, 16);

        // A.3.6 - continue
        String cipherText = Base64Url.encode(encryptedBytes);
        System.out.println("cipherText: " + cipherText);

        String aadOutputText= Base64Url.encode(aadOutput);
        System.out.println("aadOutputText: " + aadOutputText);

        String jwe = encodedHeader + "."
                + base64EncryptedCEK + "."
                + initializationVectorEncoded + "."
                + cipherText + "."
                + aadOutputText;

        System.out.println("JWE: " + jwe);

    }
}

package org.mposolda.nimbus;

import com.nimbusds.jose.CompressionAlgorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.DeflateUtils;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class NimbusExample {

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        for (int i=0 ; i<50000 ; i++) {
            JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256);
            //header.setCompressionAlgorithm(CompressionAlgorithm.DEF);

            JWEObject jweObj = new JWEObject(header, new Payload("{\"jti\":\"09c71bb4-c281-41e9-85d3-a17c9b0b0965\",\"exp\":1506711874,\"nbf\":0,\"iat\":1506711814,\"azp\":\"b7ecce76-6324-4482-b992-15576acb8d98\",\"uss\":\"aab3cb03-0a4d-4303-bed8-43498f13b203\"}"));


            byte[] key128 = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 13, 14, 15, 16};
            byte[] key256 = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 13, 14, 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
            byte[] key512 = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 13, 14, 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 13, 14, 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
            //byte[] key = key128;
            byte[] key = key256;
            //byte[] key = key512;

            DirectEncrypter encrypter = new DirectEncrypter(key);

            // You can now use the encrypter on one or more JWE objects
            // that you wish to secure
            jweObj.encrypt(encrypter);
            String jweString = jweObj.serialize();

            //System.out.println(jweString);
            //System.out.println("jweString.length(): " + jweString.length());


            DirectDecrypter decrypter = new DirectDecrypter(key);
            jweObj = JWEObject.parse(jweString);
//        jweObj = JWEObject.parse("eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0..M2r_deQ_NT_Gvz_eKipkXg.rRgk1fmfdSR3LHJIOayHwxl82-ACiBJLgaPVJNmTz3gW-o_-fsQD2bIhx102EQN55J2g7xFtDJE6J0KpUeuI_kXYYJkssQNHcrbDM52tsacuUrNYN17xYfeRNrdNn8g9kv7HLfG89bcSocks1uhKLEeUNCQB6uIIhdddttpwjKMXme1J4hvFFn63bzNt2im3.z_uyUjqW-xcLD9iWZiUxXQ");

            jweObj.decrypt(decrypter);
            //System.out.println("Decrypted payload: " + jweObj.getPayload().toString());
        }
        long took = System.currentTimeMillis() - start;
        System.out.println("took: " + took + " ms");

        deflateTest();
    }


    public static void deflateTest() throws Exception {

        final String text = "{\"jti\":\"09c71bb4-c281-41e9-85d3-a17c9b0b0965\",\"exp\":1506711874,\"nbf\":0,\"iat\":1506711814,\"azp\":\"b7ecce76-6324-4482-b992-15576acb8d98\",\"uss\":\"aab3cb03-0a4d-4303-bed8-43498f13b203\"}";
        final byte[] textBytes = text.getBytes("UTF-8");
        String base64Text = Base64URL.encode(textBytes).toString();

        byte[] compressed = DeflateUtils.compress(textBytes);
        String base64TextCompressed = Base64URL.encode(compressed).toString();

        byte[] textBytesDecompressed = DeflateUtils.decompress(compressed);
        String textDecompressed = new String(textBytesDecompressed, "UTF-8");

        System.out.println("byte length check. textBytes.length: " + textBytes.length + ", textBytesDecompressed.length: " + textBytesDecompressed.length + ", compressed.length: " + compressed.length);

        System.out.println("Decompressed text: " + textDecompressed);

        System.out.println("Base64Text.length: " + base64Text.length() + ", Base64TextCompressed.length: " + base64TextCompressed.length());


    }
}

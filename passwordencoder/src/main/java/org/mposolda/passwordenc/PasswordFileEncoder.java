package org.mposolda.passwordenc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.keycloak.common.util.Base64Url;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class PasswordFileEncoder {

    static {
        if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
    }


    public static final String MODE_ENC = "encode";
    public static final String MODE_DEC = "decode";

    public static final void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.err.println("4 arguments are required. First argument is mode. Second argument is input file (must exists). " +
                    "Third argument is output file (Must not exist). Fourth argument is file with hash (must exists).");
            System.exit(1);
        }

        String mode = args[0];
        if (!mode.equals(MODE_ENC) && !mode.equals(MODE_DEC)) {
            System.err.println("First argument must be either '" + MODE_ENC + "' or '" + MODE_DEC + "'");
            System.exit(1);
        }

        System.out.println("Mode: " + mode);

        String inputFile = args[1];
        File f = new File(inputFile);
        if (!f.exists()) {
            System.err.println("File '" + inputFile + "' must exists.");
            System.exit(1);
        }
        System.out.println("Input file: " + inputFile);

        boolean stdout = true;
        File f2;

        String outputFile = args[2];
        if ("stdout".equals(outputFile)) {
            System.out.println("Writing to stdout");
            f2 = null;
        } else {
            stdout = false;
            f2 = new File(outputFile);
            if (f2.exists()) {
                System.err.println("File '" + outputFile + "' must not exists OR use value 'stdout' .");
                System.exit(1);
            }
            System.out.println("Output file: " + outputFile);
        }

        String hashFileName = args[3];
        File fileWithHash = new File(hashFileName);
        if (!fileWithHash.exists()) {
            System.err.println("File '" + hashFileName + "' must exists.");
            System.exit(1);
        }

        String passwd = PasswordReader.readPassword();
        //System.out.println("Password: " + passwd);

        // Compare password first
        byte[] doubleHash = sha256(sha256(passwd.getBytes("UTF-8")));
        String doubleHashStr = Base64Url.encode(doubleHash);

        String doubleHashFromFile = readFileSingleLine(fileWithHash);

        if (!MessageDigest.isEqual(doubleHashStr.getBytes("UTF-8"), doubleHashFromFile.getBytes("UTF-8"))) {
            System.err.println("Provided password don't match double-hashed password from file '" + hashFileName + "'.");
            System.exit(1);
        }


        if (mode.equals(MODE_ENC)) {
            // Read whole file
            String content = readFile(f);

            String encoded = encode(passwd, content);
            writeSingleLineToFile(stdout, f2, encoded);
            System.out.println("Successfully written encoded text to file: " + outputFile);
        } else {
            String content = readFileSingleLine(f);
            String decoded = decode(passwd, content);
            writeToFile(stdout, f2, decoded);
            System.out.println("Successfully written decoded text to file: " + outputFile);
        }
    }


    private static String readFileSingleLine(File f) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line = reader.readLine();

        reader.close();

        return line;
    }


    private static String readFile(File f) throws Exception {
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line + "\n");
        }

        reader.close();

        return builder.toString();
    }

    private static void writeSingleLineToFile(boolean stdout, File f, String encryptedText) throws Exception {
        if (stdout) {
            System.out.println("Encrypted:");
            System.out.println(encryptedText);
        } else {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(f)));
            writer.write(encryptedText);

            writer.flush();
            writer.close();
        }

    }

    private static void writeToFile(boolean stdout, File f, String text) throws Exception {
        String[] splits = text.split("\\n");

        PrintWriter writer;
        if (stdout) {
            writer = new PrintWriter(System.out);
        } else {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(f)));
        }

        for (String line : splits) {
            writer.write(line + "\n");
        }

        writer.flush();
        writer.close();
    }


    private static String encode(String password, String text) throws Exception {
        SecretKey secretKey = getSecretKey(password);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        AlgorithmParameterSpec ivParamSpec = new IvParameterSpec(getSalt()); // TODO
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParamSpec);
        byte[] encoded = cipher.doFinal(text.getBytes("UTF-8"));
        String base64Enc = Base64Url.encode(encoded);
        return base64Enc;
    }


    private static String decode(String password, String encoded) throws Exception {
        byte[] encodedBytes = Base64Url.decode(encoded);
        SecretKey secretKey = getSecretKey(password);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        AlgorithmParameterSpec ivParamSpec = new IvParameterSpec(getSalt()); // TODO
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParamSpec);
        byte[] decoded = cipher.doFinal(encodedBytes);
        String decodedStr = new String(decoded, "UTF-8");
        return decodedStr;
    }



    private static SecretKey getSecretKey(String password) throws Exception {
        byte[] shaHash = sha256(password.getBytes("UTF-8"));
        return new SecretKeySpec(shaHash, "AES");
    }


    private static byte[] sha256(byte[] input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(input);
        return md.digest();
    }


    private static byte[] getSalt() {
        // TODO
        byte[] buffer = { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 11, 12, 13, 14, 15, 16 };
        return buffer;
    }



}

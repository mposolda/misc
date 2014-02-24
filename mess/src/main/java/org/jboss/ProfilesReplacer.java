package org.jboss;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Hello world!
 *
 */
public class ProfilesReplacer {

    private static String separator = System.getProperty("line.separator");

    public static void main( String[] args ) throws Exception {
        String pomFileName = args[0];
        String profilesFileName = args[1];
        System.out.println("pomFileName: " + pomFileName);
        System.out.println("profilesFileName: " + profilesFileName);
        String profiles = readFile(profilesFileName);
        String pom = readFile(pomFileName);
        pom = pom.replace("<profiles>", profiles);
        writeToFile(pomFileName, pom);
    }

    private static String readFile(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            builder.append(line + separator);
        }
        br.close();
        return builder.toString();
    }

    private static void writeToFile(String fileName, String content) throws Exception {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        String[] str = content.split(separator);
        for (String line : str) {
            writer.println(line);
        }
        writer.close();
    }
}

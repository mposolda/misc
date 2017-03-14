package org.mposolda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FileParserImpl implements FileParser {

    public Map<String, Map<Integer, Integer>> getData() {
        try {
            Map<String, Map<Integer, Integer>> result = new HashMap<String, Map<Integer, Integer>>();
            result.put("total", new HashMap<Integer, Integer>());
            result.put("memory", new HashMap<Integer, Integer>());
            result.put("cpu", new HashMap<Integer, Integer>());

            File f = new File("jfreechart-test/cpu.txt");
            System.out.println(f.getAbsolutePath());

            BufferedReader reader = new BufferedReader(new FileReader(f));
            String s;
            while ((s = reader.readLine()) != null) {
                System.out.println(s);
                String[] parts = s.split(" ");
                int currentTime = 0;
                for (String part : parts) {
                    String[] sp = part.split(":");
                    String name = sp[0];
                    String val = sp[1];
                    Integer intVal = Integer.parseInt(val);

                    if (name.equals("time")) {
                        currentTime = intVal;
                    } else {
                        Map<Integer, Integer> myMap = result.get(name);
                        myMap.put(currentTime, intVal);
                    }
                }

            }
            return result;
        } catch (Exception fnfe) {
            //System.err.println("Nenalezl jsem soubor: " + fnfe.getMessage());
            fnfe.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        FileParser parser = new FileParserImpl();
        Map<String, Map<Integer, Integer>>  data = parser.getData();
        System.out.println("Data: " + data);
    }
}

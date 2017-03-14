package org.mposolda;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MapExample {

    public static void main(String[] args) {
        Map<Integer, Integer> myMap = new HashMap<Integer, Integer>();
        myMap.put(1, 10);
        myMap.put(2, 20);

        System.out.println("Size: " + myMap.size() + ", 1=" + myMap.get(1));
    }
}

package org.jboss;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Hello world!
 *
 */
public class ExecutorsTest {

    private static String separator = System.getProperty("line.separator");

    public static void main( String[] args ) throws Exception {
        ExecutorService s = Executors.newCachedThreadPool(new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                System.out.println("HELLo! I am creating new thread");
                return new Thread(r);
            }

        });

        Runnable r = new Runnable() {

            @Override
            public void run() {
                for (int i=0 ; i<10 ; i++) {
                    System.out.println(Thread.currentThread().getName() +  " - Going to sleep for 1000 ms");

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        };

        for (int i=0 ; i<4 ; i++) {
            s.submit(r);
            System.out.println("Going to sleep 10s before creating another thread.");
            Thread.sleep(5000);
            System.out.println("Sleep finished - creating another thread.");
        }
    }

}

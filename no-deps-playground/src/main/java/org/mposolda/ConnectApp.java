package org.mposolda;

import java.io.IOException;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class ConnectApp {

    public static void main(String[] args ) {
        Socket myClient;
        try {
            String host = args[0];
            String port = args[1];
            System.out.println("Connecting to: " + host + ":" + port);
            myClient = new Socket(host, Integer.parseInt(port));
            System.out.println("CONNECTED");

            myClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

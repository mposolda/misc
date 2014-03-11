package org.jboss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SimpleSocketClient {

    public static void main(String[] args) throws IOException {
        // final Socket socket = new Socket("localhost", 8675);
        final Socket socket = new Socket("localhost", 8080);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromUser;

        try {
            new Thread() {

                @Override
                public void run() {
                    try {
                        String fromServer;
                        while ((fromServer = in.readLine()) != null) {
                            System.out.println(fromServer);
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

            }.start();

            while (true) {
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    if ("send".contains(fromUser)) {
                        out.println(new String(new byte[] { 0x00 }, "UTF-8"));
                        out.println();
                        //out = new PrintWriter(socket.getOutputStream(), true);
                    } else {
                        out.println(fromUser);
                    }
                }
            }
        } finally {
            out.close();
            in.close();
            stdIn.close();
            socket.close();
        }
    }

    // SAMPLE STOMP MESSAGES TO SEND TO SERVER
    /*
    CONNECT
    accept-version:1.2
    host:localhost

            send



    SUBSCRIBE
    id:145
    destination:/people/bob

            send


    SEND
    destination:/people/bob
    content-type:text/plain

    hello queue aaaaaaaaaaaa

    send
    */
}

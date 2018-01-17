package org.jboss.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MulticastTest {

    public static void main(String[] args) throws Exception {
        //System.out.println("Using hostname: " + HostResolveUtil.getHostname() + ", port: " + HostResolveUtil.getPort());

        MulticastReceiver server = new MulticastReceiver();
        server.start();

        System.out.println("Multicast server started on port " + HostResolveUtil.getPort() + ". Sending messages");

        clientSendingMessagesFromStdin(server);

        server.join();
    }

    private static void clientSendingMessagesFromStdin(MulticastReceiver server) throws Exception {
        MulticastPublisher publisher = new MulticastPublisher();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        System.out.print("$ ");
        try {
            while ((line = reader.readLine()) != null) {
                publisher.multicast(line);
                Thread.sleep(1000);

                if ("end".equals(line)) {
                    return;
                } else {
                    System.out.print("$ ");
                }

            }
        } finally {
            System.out.println("Exit client");
            reader.close();
        }
    }


    public static class MulticastPublisher {
        private DatagramSocket socket;
        private InetAddress group;
        private byte[] buf;

        public void multicast(String multicastMessage) throws IOException {
            socket = new DatagramSocket();
            group = InetAddress.getByName("224.0.0.10");
            buf = multicastMessage.getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
            socket.send(packet);
            socket.close();
        }
    }


    public static class MulticastReceiver extends Thread {

        protected MulticastSocket socket = null;
        protected byte[] buf = new byte[256];

        public void run() {
            try {
                // TODO: Investigate why it doesn't work
                //MulticastSocket socket = new MulticastSocket(new InetSocketAddress("192.168.0.101", 4446));
                MulticastSocket socket = new MulticastSocket(4446);

                InetAddress group = InetAddress.getByName("224.0.0.10");
                socket.joinGroup(group);
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String received = new String(
                            packet.getData(), 0, packet.getLength());
                    if ("end".equals(received)) {
                        break;
                    } else {
                        System.out.println("Server received: " + received);
                    }
                }
                socket.leaveGroup(group);
                socket.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}

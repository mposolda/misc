package org.jboss.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Start from command line with: java -cp target/mess-0.1-SNAPSHOT.jar -Dmy.host=127.0.0.1 -Dmulticast.host=224.0.0.8 -Dmulticast.port=4446 org.jboss.network.MulticastTest
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MulticastTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Using hostname: " + HostResolveUtil.getHostname() +
                ", multicast host: " + HostResolveUtil.getMulticastHost() + ", multicast port: " + HostResolveUtil.getMulticastPort());

        MulticastReceiver server = new MulticastReceiver();
        server.start();

        System.out.println("Multicast server started. Sending messages");

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
            group = InetAddress.getByName(HostResolveUtil.getMulticastHost());
            buf = multicastMessage.getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, HostResolveUtil.getMulticastPort());
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
                MulticastSocket socket = new MulticastSocket(HostResolveUtil.getMulticastPort());

                InetAddress group = InetAddress.getByName(HostResolveUtil.getMulticastHost());
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

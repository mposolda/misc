package org.jboss.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Execute this application with something like: -Dmy.host=localhost -Dmy.port=4445
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class UdpTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Using hostname: " + HostResolveUtil.getHostname() + ", port: " + HostResolveUtil.getPort());

        UdpServer server = new UdpServer();
        server.start();

        System.out.println("Udp server started on port " + HostResolveUtil.getPort() + ". Sending messages");

        clientSendingMessagesFromStdin(server);

        server.join();
    }

    private static void clientSendingMessagesFromStdin(UdpServer server) throws Exception {
        UdpClient client = new UdpClient();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        System.out.print("$ ");
        try {
            while ((line = reader.readLine()) != null) {
                String line2 = client.sendEcho(line);
                Thread.sleep(1000);

                System.out.println("client received: " + line2);

                if (!server.running) {
                    break;
                } else {
                    System.out.print("$ ");
                }
            }
        } finally {
            System.out.println("Exit client");
            reader.close();
        }
    }

    private static class UdpClient {
        private DatagramSocket socket;
        private InetAddress address;

        private byte[] buf;

        public UdpClient() throws Exception {
            socket = new DatagramSocket();
            address = InetAddress.getByName(HostResolveUtil.getHostname());
        }

        public String sendEcho(String msg) throws Exception {
            buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, HostResolveUtil.getPort());
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            return received;
        }

        public void close() {
            socket.close();
        }
    }

    private static class UdpServer extends Thread {

        private DatagramSocket socket;
        private boolean running;
        private byte[] buf = new byte[256];

        public UdpServer() throws Exception {
            InetAddress hostAddr = InetAddress.getByName(HostResolveUtil.getHostname());
            socket = new DatagramSocket(HostResolveUtil.getPort(), hostAddr);
        }

        public void run() {
            running = true;

            while (running) {

                System.out.println("Udp server receiving messages");

                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    String received = new String(packet.getData(), 0, packet.getLength());

                    System.out.println("Server received: " + received + " from " + address + " and port: " + port);
                    if (received.equals("end")) {
                        running = false;
                    }

                    DatagramPacket packet2 = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(packet2);
                } catch (IOException ioe) {
                    System.err.println("Received exception, but continue. Details: ");
                    ioe.printStackTrace();
                }
            }

            System.out.println("Closing server");

            socket.close();
        }
    }
}

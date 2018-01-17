package org.jboss.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class BroadcastTest {

    public static void main(String[] args) throws Exception {
        dumpInterfaces();


        broadcastMessage("Hello", InetAddress.getByName("255.255.255.255"));


        List<InetAddress> broadcasts = listAllBroadcastAddresses();
        for (InetAddress addr : broadcasts) {
            System.out.println("br: " + addr);
        }
    }


    private static void dumpInterfaces() throws Exception {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            System.out.println(networkInterface.getName() + ", parent: " + networkInterface.getParent() +
                    ", inetAddresses: " + dumpAddresses(networkInterface.getInetAddresses()) +
                    ", supportsMulticast: " + networkInterface.supportsMulticast());
        }
    }

    private static String dumpAddresses(Enumeration<InetAddress> inetAddresses) {
        StringBuilder b = new StringBuilder();
        while (inetAddresses.hasMoreElements()) {
            b.append(" addr: " + inetAddresses.nextElement().toString());
        }
        return b.toString();
    }


    private static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }


    public static void broadcastMessage(String broadcastMessage, InetAddress address) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
        socket.send(packet);
        socket.close();
    }


}

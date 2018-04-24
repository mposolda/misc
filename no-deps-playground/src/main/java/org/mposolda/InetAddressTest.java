package org.mposolda;

import java.net.InetSocketAddress;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class InetAddressTest {

    public static void main(String[] args) {
        InetSocketAddress addr = new InetSocketAddress("www.seznam.cz", 80);
        InetSocketAddress addrUnresolved = InetSocketAddress.createUnresolved("www.seznam.cz", 80);

        System.out.println("addr: " + addr);
        System.out.println("addrUnresolved: " + addrUnresolved);
        System.out.println("equals: " + addr.equals(addrUnresolved));
    }
}

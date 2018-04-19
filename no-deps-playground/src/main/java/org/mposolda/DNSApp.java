package org.mposolda;

import java.io.IOException;
import java.net.InetAddress;

/**
 * java -classpath target/no-deps-playground-0.1-SNAPSHOT.jar org.mposolda.DNSApp jdg-app-hotrod.datagrid.svc
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DNSApp {

    public static void main(String[] args) throws IOException {
        String host = args[0];
        System.out.println("Trying host: " + host);

        System.out.println("IP: " + InetAddress.getByName(host).getHostAddress());
    }
}

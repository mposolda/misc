package org.mposolda;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;


/**
 *
 * COMMAND TO IMPORT CLIENT CERTIFICATE FROM THE FILE keycloak.jks TO PKCS12, WHICH CAN BE IMPORTED TO BROWSER
 * keytool -importkeystore -srckeystore keycloak-client.jks -destkeystore keycloak-client.p12 -srcstoretype JKS -deststoretype PKCS12
 * -srcstorepass secret -deststorepass secret -srcalias foo -destalias foo -srckeypass secret -destkeypass secret -noprompt
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SSLSocketClient {

    public static final String TRUSTSTORE_PATH = "/home/mposolda/IdeaProjects/misc/ssl-test/certs/keycloak-jbrown.jks";
    // public static final String TRUSTSTORE_PATH = "/home/mposolda/IdeaProjects/misc/ssl-test/certs/keycloak-bwilson.jks";

    public static void main(String[] args) throws Exception {
        // Enable to remove details
        //System.setProperty("javax.net.debug", "ssl.trustmanager");


        // readSeznam();
        readSecuredServer(true);
    }

    // Doesn't require anything
    public static void readSeznam() throws Exception {
        readHost(null, null, "www.seznam.cz", 443);
    }

    // Requires keycloak running on http://localhost:8443
    public static void readSecuredServer(boolean addClientCertificate) throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(TRUSTSTORE_PATH), "secret".toCharArray());

        TrustManagerFactory tmf =
                TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);
        TrustManager[] trustManagers = tmf.getTrustManagers();

        KeyManager[] keyManagers = null;
        if (addClientCertificate) {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, "secret".toCharArray());
            keyManagers = kmf.getKeyManagers();
        }

        readHost(keyManagers, trustManagers, "localhost", 8443);
    }

    public static void readHost(KeyManager[] keyManagers, TrustManager[] trustManagers, String host, int port) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket();

        InetSocketAddress seznamAddress = new InetSocketAddress(host, port);
        sslSocket.connect(seznamAddress, 0);

        sslSocket.startHandshake();

        new SessionDump().dumpSSLSession(sslSocket.getSession());

        readSocket(sslSocket, host);
    }

    protected static void readSocket(Socket socket, String host) throws Exception {
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(os, true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        writer.println("GET /auth/version HTTP/1.1");
        writer.println("Host: " + host);
        writer.println("");
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            System.out.println(line);

            // 0 is specific to seznam
            if (line.equals("0") || line.equals("</html>")) {
                break;
            }
        }

        System.out.println("SOCKET READ SUCCESSFULLY");

        is.close();
        os.close();
        socket.close();
    }
}

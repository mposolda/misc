package org.mposolda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.ssl.SSLInitializationException;

/**
 * COMMANDS TO CREATE KEYSTORE WITH PRIVATE KEY ENTRY
 * keytool -genkey -alias localhost -keyalg RSA -keystore keycloak.jks -validity 10950
 *
 * COMMANDS TO IMPORT CERTIFICATE FROM THE FILE keycloak.jks WITH PRIVATE KEY
 * keytool -exportcert -keystore keycloak.jks -alias localhost -file foo.crt
 * keytool -importcert -keystore keycloak-client.jks -alias localhost -file foo.crt
 * rm foo.crt
 * keytool -list -keystore keycloak-client.jks
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SSLSocketClient {

    public static final String TRUSTSTORE_PATH = "/home/mposolda/IdeaProjects/misc/ssl-test/keycloak-client.jks";

    public static void main(String[] args) throws Exception {
        // Enable to remove details
        //System.setProperty("javax.net.debug", "ssl.trustmanager");


        // readSeznam();
        readSecuredServer();
    }

    // Doesn't require anything
    public static void readSeznam() throws Exception {
        readHost(null, "www.seznam.cz", 443);
    }

    // Requires keycloak running on http://localhost:8443
    public static void readSecuredServer() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(TRUSTSTORE_PATH), "secret".toCharArray());

        TrustManagerFactory tmf =
                TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);
        TrustManager[] trustManagers = tmf.getTrustManagers();

        readHost(trustManagers, "localhost", 8543);
    }

    public static void readHost(TrustManager[] trustManagers, String host, int port) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, null);

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket();

        InetSocketAddress seznamAddress = new InetSocketAddress(host, port);
        sslSocket.connect(seznamAddress, 0);

        sslSocket.startHandshake();

        SSLSession session = sslSocket.getSession();
        final Certificate[] certs = session.getPeerCertificates();
        for (Certificate cert : certs) {
            X509Certificate x509Cert = (X509Certificate) certs[0];
            String subjectPrincipal = x509Cert.getSubjectX500Principal().toString();
            System.out.println("Certificate principal: " + subjectPrincipal);
        }

        readSocket(sslSocket, host);
    }

    protected static void readSocket(Socket socket, String host) throws Exception {
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(os, true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        writer.println("GET / HTTP/1.1");
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

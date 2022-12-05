package org.mposolda;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.HashSet;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SSLSocketServer {

    public static final String KEYSTORE_PATH = "/home/mposolda/IdeaProjects/misc/ssl-test/certs/keycloak-server.jks";

    public static void main(String[] args) throws Exception {
        // plainServer();
        securedServer();
    }

    public static void plainServer() throws Exception {
        final ServerSocket ss = new ServerSocket(8543);

        while (true) {
            Socket socket = ss.accept();
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(os, true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            while (!socket.isClosed()) {
                String line = reader.readLine();
                if (line != null) {
                    line = line.trim();
                    System.out.println(line);

                    if (line.isEmpty()) {
                        System.out.println("Sending Response!");
                        writer.println("HTTP/1.1 200 OK");
                        writer.println("Content-Type: text/html");
                        writer.println("");
                        writer.println("<html><head><title>My Server</title></head><body><h1>It really works!</h1></body>");
                        writer.println("</html>");
                        writer.flush();
                        writer.close();
                    }
                }
            }
        }
    }

    public static void securedServer() throws Exception {
        String ksType = System.getProperty("keystoreType", "JKS");
        System.out.println("Configured keystore type by 'keystoreType' system property: " + ksType);
        SSLConfig cfg = Enum.valueOf(SSLConfig.class, ksType);

        KeyStore ks = KeyStore.getInstance(cfg.toString());
        InputStream ksInputStream = cfg.getKeystoreFileLocation() == null ? null : new FileInputStream(cfg.getKeystoreFileLocation());
        char[] ksSecret = cfg.getKeystorePassword() == null ? null : cfg.getKeystorePassword().toCharArray();
        ks.load(ksInputStream, ksSecret);

        listKeystore(ks, cfg, ksSecret);

        KeyManagerFactory kfm = KeyManagerFactory.getInstance("SunX509");
        kfm.init(ks, ksSecret);
        KeyManager[] keyManagers = kfm.getKeyManagers();

        TrustManagerFactory tmf =
                TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);
        TrustManager[] trustManagers = tmf.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);
        SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();

        SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(8543);

        // Requesting client auth here
        ss.setWantClientAuth(true);
        //ss.setNeedClientAuth(true);

        while (true) {
            System.out.println("Accepting connections on 8543");
            SSLSocket socket = (SSLSocket) ss.accept();
            //socket.setSoTimeout(10000);

            try {
                socket.startHandshake();
            } catch (SSLException sslhe) {
                System.out.println("Handshake failed, maybe because browser doesn't trust us at this moment");
                sslhe.printStackTrace();
                socket.close();
                continue;
            }

            new SessionDump().dumpSSLSession(socket.getSession());

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(os, true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            try {
                while (!socket.isClosed()) {
                    String line = reader.readLine();

                    if (line != null) {
                        line = line.trim();
                        System.out.println(line);

                        if (line.isEmpty()) {
                            System.out.println("Sending Response!");
                            writer.println("HTTP/1.1 200 OK");
                            writer.println("Content-Type: text/html");
                            writer.println("");
                            writer.println("<html><head><title>My Server</title></head><body><h1>It really works!</h1></body>");
                            writer.println("</html>");
                            writer.flush();
                            writer.close();
                        }
                    } else {
                        System.out.println("Line is null. Closing");
                        writer.close();
                    }
                }
            } catch (SocketException se) {
                System.out.println("Socket exception! Maybe connection closed already");
            }
        }
    }

    private static void listKeystore(KeyStore ks, SSLConfig cfg, char[] ksSecret) throws Exception {
        System.out.println("");
        System.out.println("Loaded keystore. Keystore aliases: " + aliases(ks));
        System.out.println("Key: " + ks.getKey(cfg.getKeyAliasInKs(), ksSecret));
        System.out.println("Certificate: " + ks.getCertificate(cfg.getKeyAliasInKs()));
        System.out.println("");
    }

    private static String aliases(KeyStore ks) throws Exception {
        Enumeration<String> en = ks.aliases();
        StringBuilder sb = null;
        while (en.hasMoreElements()) {
            if (sb == null) {
                sb = new StringBuilder(en.nextElement());
            } else {
                sb.append(", " + en.nextElement());
            }
        }
        return sb.toString();
    }
}

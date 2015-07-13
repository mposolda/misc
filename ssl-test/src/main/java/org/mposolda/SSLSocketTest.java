package org.mposolda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.conn.ssl.SSLInitializationException;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SSLSocketTest {

    public static final String SEZNAM_HOST = "www.seznam.cz";
    public static final Integer SEZNAM_PORT = 443;

    public static void main(String[] args) throws Exception {
        readSeznam();
    }

    public static void readSeznam() throws Exception {
        SSLContext sslContext = createDefault();
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket();

        InetSocketAddress seznamAddress = new InetSocketAddress(SEZNAM_HOST, SEZNAM_PORT);
        sslSocket.connect(seznamAddress, 0);

        sslSocket.startHandshake();

        SSLSession session = sslSocket.getSession();
        final Certificate[] certs = session.getPeerCertificates();
        final X509Certificate x509Cert = (X509Certificate) certs[0];
        String subjectPrincipal = x509Cert.getSubjectX500Principal().toString();
        System.out.println("Certificate principal: " + subjectPrincipal);
        readSocket(sslSocket, SEZNAM_HOST);
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
            System.out.println(line);

            // Specific to seznam probably
            if (line.equals("0")) {
                break;
            }
        }

        System.out.println("Going to exit");

        is.close();
        os.close();
        socket.close();
    }

    /**
     * Creates default factory based on the standard JSSE trust material
     * (<code>cacerts</code> file in the security properties directory). System properties
     * are not taken into consideration.
     *
     * @return the default SSL socket factory
     */
    public static SSLContext createDefault() throws SSLInitializationException {
        try {
            final SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            return sslcontext;
        } catch (final NoSuchAlgorithmException ex) {
            throw new SSLInitializationException(ex.getMessage(), ex);
        } catch (final KeyManagementException ex) {
            throw new SSLInitializationException(ex.getMessage(), ex);
        }
    }
}

package org.jboss;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ClientCertificateTest {

    public static final String BASE_DIR = "/home/mposolda/work/examples/tomcat-client-cert-example/apache-tomcat-7.0.41";

    public static void main(String[] args) {

        // trustStore has the certificates that are presented by the server that
        // this application is to trust
        System.setProperty("javax.net.ssl.trustStore", BASE_DIR + "/keys/client.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");

        // keystore has the certificates presented to the server when a server
        // requests one to authenticate this application to the server
        System.setProperty("javax.net.ssl.keyStore", BASE_DIR + "/keys/client.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");

        try {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod();
            method.setURI(new URI("https://localhost:8443", false));
            client.executeMethod(method);

            System.out.println(method.getResponseBodyAsString());

        } catch (Exception e) {
            System.out.println(e.getCause());
            e.printStackTrace();
        }
    }
}

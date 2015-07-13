package org.mposolda;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Connect to seznam with Apache HTTP client
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SeznamHttpClientTest {

    public static void main(String[] args) {
        HttpClient client = new DefaultHttpClient();
        try {
            HttpGet get = new HttpGet("https://www.seznam.cz");
            try {
                HttpResponse response = client.execute(get);
                System.out.println("Response status: " + response.getStatusLine().getStatusCode());

                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                String respString;
                try {
                    respString = StreamUtil.readString(is);
                    System.out.println("Response: " + respString);
                } finally {
                    is.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

package org.mposolda;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.keycloak.OAuth2Constants;
import org.keycloak.jose.jws.JWSBuilder;
import org.keycloak.util.JsonSerialization;
import org.keycloak.util.PemUtils;
import org.keycloak.util.StreamUtil;
import org.keycloak.util.Time;
import sun.misc.IOUtils;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class GoogleSATest {

    public static void main(String[] args) throws Exception {
        FileInputStream fs = new FileInputStream("/home/mposolda/work/sa-google-example/service-account-sample-79d536890d8a.json");
        GooglePrivateKey gpk = JsonSerialization.readValue(fs, GooglePrivateKey.class);

        // PrivateKey pk = decodePrivateKeyFromJson(gpk);
        PrivateKey pk = decodePrivateKeyFromP12();

        GoogleRequestToken token = createRequestToken(gpk);

        String signedToken = new JWSBuilder()
                .type("JWT")
                .jsonContent(token)
                .rsa256(pk);

        System.out.println("Signed JWT input token: " + signedToken);

        GoogleAccessToken googleAccessToken = retrieveServiceAccountTokenFromGoogle(signedToken);
        System.out.println("Google access token: " + googleAccessToken.getAccessToken());

        String myProfile = retrieveMyProfile(googleAccessToken.getAccessToken());
        System.out.println(myProfile);
    }

    private static PrivateKey decodePrivateKeyFromJson(GooglePrivateKey gpk) throws Exception {
        String privateKey = gpk.getPrivateKeyPem();
        return PemUtils.decodePrivateKey(privateKey);
    }

    private static PrivateKey decodePrivateKeyFromP12() throws Exception {
        char[] p12FilePassword = "notasecret".toCharArray();
        FileInputStream fs = new FileInputStream("/home/mposolda/work/sa-google-example/service-account-sample-5f7ee2b3cb7e.p12");
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(fs, p12FilePassword);
        PrivateKey key = (PrivateKey)keystore.getKey("privatekey", p12FilePassword);
        return key;
    }

    private static GoogleRequestToken createRequestToken(GooglePrivateKey gpk) {
        GoogleRequestToken token = new GoogleRequestToken();
        token.setIssuer(gpk.getClientEmail());
        token.setScope("https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.me");
        token.setAudience("https://www.googleapis.com/oauth2/v3/token");
        long currentTime = ((Integer) Time.currentTime()).longValue();
        token.setIssuedAt(currentTime);
        token.setExpiration(currentTime + 3600);
        return token;
    }

    private static GoogleAccessToken retrieveServiceAccountTokenFromGoogle(String signedToken) throws Exception {
        CloseableHttpClient client = new DefaultHttpClient();
        try {
            HttpPost post = new HttpPost("https://www.googleapis.com/oauth2/v3/token");

            List<NameValuePair> parameters = new LinkedList<NameValuePair>();
            parameters.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, "urn:ietf:params:oauth:grant-type:jwt-bearer"));
            parameters.add(new BasicNameValuePair("assertion", signedToken));

            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
            post.setEntity(formEntity);

            CloseableHttpResponse response = client.execute(post);
            System.out.println("Access token response status: " + response.getStatusLine().getStatusCode());
            InputStream respStream = response.getEntity().getContent();
            return JsonSerialization.readValue(respStream, GoogleAccessToken.class);
        } finally {
            try {
                client.close();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    private static String retrieveMyProfile(String accessToken) {
        CloseableHttpClient client = new DefaultHttpClient();
        try {
            HttpGet post = new HttpGet("https://www.googleapis.com/plus/v1/people/+MarekPosolda?access_token=" + accessToken);

            try {
                CloseableHttpResponse response = client.execute(post);
                System.out.println("My profile request response status: " + response.getStatusLine().getStatusCode());
                InputStream respStream = response.getEntity().getContent();
                return StreamUtil.readString(respStream);
            } catch (Exception e) {
                throw new RuntimeException("Failed to retrieve access token", e);
            }
        } finally {
            try {
                client.close();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }
}

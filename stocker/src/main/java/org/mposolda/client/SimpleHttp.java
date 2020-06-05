package org.mposolda.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author Vlastimil Elias (velias at redhat dot com)
 * @author David Klassen (daviddd.kl@gmail.com)
 */
public class SimpleHttp {

    private static final ObjectMapper mapper = new ObjectMapper();

    private HttpClient client;

    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> params;
    private Object entity;

    protected SimpleHttp(String url, String method, HttpClient client) {
        this.client = client;
        this.url = url;
        this.method = method;
    }



    public static SimpleHttp doDelete(String url, HttpClient client) {
        return new SimpleHttp(url, "DELETE", client);
    }



    public static SimpleHttp doGet(String url, HttpClient client) {
        return new SimpleHttp(url, "GET", client);
    }



    public static SimpleHttp doPost(String url, HttpClient client) {
        return new SimpleHttp(url, "POST", client);
    }

    public static SimpleHttp doPut(String url, HttpClient client) {
        return new SimpleHttp(url, "PUT", client);
    }

    public SimpleHttp header(String name, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(name, value);
        return this;
    }

    public SimpleHttp json(Object entity) {
        this.entity = entity;
        return this;
    }

    public SimpleHttp param(String name, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(name, value);
        return this;
    }

    public SimpleHttp auth(String token) {
        header("Authorization", "Bearer " + token);
        return this;
    }

    public SimpleHttp authBasic(final String username, final String password) {
        final String basicCredentials = String.format("%s:%s", username, password);
        header("Authorization", "Basic " + Base64.getEncoder().encodeToString(basicCredentials.getBytes()));
        return this;
    }

    public SimpleHttp acceptJson() {
        if (headers == null || !headers.containsKey("Accept")) {
            header("Accept", "application/json");
        }
        return this;
    }

    public JsonNode asJson() throws IOException {
        if (headers == null || !headers.containsKey("Accept")) {
            header("Accept", "application/json");
        }
        return mapper.readTree(asString());
    }

    public <T> T asJson(Class<T> type) throws IOException {
        if (headers == null || !headers.containsKey("Accept")) {
            header("Accept", "application/json");
        }
        return JsonSerialization.readValue(asString(), type);
    }

    public <T> T asJson(TypeReference<T> type) throws IOException {
        if (headers == null || !headers.containsKey("Accept")) {
            header("Accept", "application/json");
        }
        return JsonSerialization.readValue(asString(), type);
    }

    public String asString() throws IOException {
        return asResponse().asString();
    }

    public int asStatus() throws IOException {
        return asResponse().getStatus();
    }

    public Response asResponse() throws IOException {
        return makeRequest();
    }

    private Response makeRequest() throws IOException {
        boolean get = method.equals("GET");
        boolean post = method.equals("POST");
        boolean put = method.equals("PUT");
        boolean delete = method.equals("DELETE");

        HttpRequestBase httpRequest = new HttpPost(url);

        if (get) {
            httpRequest = new HttpGet(appendParameterToUrl(url));
        }

        if (delete) {
            httpRequest = new HttpDelete(appendParameterToUrl(url));
        }

        if (put) {
            httpRequest = new HttpPut(appendParameterToUrl(url));
        }

        if (post || put) {
            if (params != null) {
                ((HttpEntityEnclosingRequestBase) httpRequest).setEntity(getFormEntityFromParameter());
            } else if (entity != null) {
                if (headers == null || !headers.containsKey("Content-Type")) {
                    header("Content-Type", "application/json");
                }
                ((HttpEntityEnclosingRequestBase) httpRequest).setEntity(getJsonEntity());
            } else {
                throw new IllegalStateException("No content set");
            }
        }

        if (headers != null) {
            for (Map.Entry<String, String> h : headers.entrySet()) {
                httpRequest.setHeader(h.getKey(), h.getValue());
            }
        }

        return new Response(client.execute(httpRequest));
    }

    private URI appendParameterToUrl(String url) throws IOException {
        URI uri = null;

        try {
            URIBuilder uriBuilder = new URIBuilder(url);

            if (params != null) {
                for (Map.Entry<String, String> p : params.entrySet()) {
                    uriBuilder.setParameter(p.getKey(), p.getValue());
                }
            }

            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
        }

        return uri;
    }

    private StringEntity getJsonEntity() throws IOException {
        return new StringEntity(JsonSerialization.writeValueAsString(entity));
    }

    private UrlEncodedFormEntity getFormEntityFromParameter() throws IOException{
        List<NameValuePair> urlParameters = new ArrayList<>();

        if (params != null) {
            for (Map.Entry<String, String> p : params.entrySet()) {
                urlParameters. add(new BasicNameValuePair(p.getKey(), p.getValue()));
            }
        }

        return new UrlEncodedFormEntity(urlParameters);
    }

    public static class Response {

        private HttpResponse response;
        private int statusCode = -1;
        private String responseString;

        public Response(HttpResponse response) {
            this.response = response;
        }

        private void readResponse() throws IOException {
            if (statusCode == -1) {
                statusCode = response.getStatusLine().getStatusCode();

                InputStream is;
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    is = entity.getContent();
                    ContentType contentType = ContentType.getOrDefault(entity);
                    Charset charset = contentType.getCharset();
                    try {
                        HeaderIterator it = response.headerIterator();
                        while (it.hasNext()) {
                            Header header = it.nextHeader();
                            if (header.getName().equals("Content-Encoding") && header.getValue().equals("gzip")) {
                                is = new GZIPInputStream(is);
                            }
                        }

                        InputStreamReader reader = charset == null ? new InputStreamReader(is) :
                                new InputStreamReader(is, charset);

                        StringWriter writer = new StringWriter();

                        char[] buffer = new char[1024 * 4];
                        for (int n = reader.read(buffer); n != -1; n = reader.read(buffer)) {
                            writer.write(buffer, 0, n);
                        }

                        responseString = writer.toString();
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            }
        }

        public int getStatus() throws IOException {
            readResponse();
            return response.getStatusLine().getStatusCode();
        }

        public JsonNode asJson() throws IOException {
            return mapper.readTree(asString());
        }

        public <T> T asJson(Class<T> type) throws IOException {
            return JsonSerialization.readValue(asString(), type);
        }

        public <T> T asJson(TypeReference<T> type) throws IOException {
            return JsonSerialization.readValue(asString(), type);
        }

        public String asString() throws IOException {
            readResponse();
            return responseString;
        }

        public String getFirstHeader(String name) throws IOException {
            readResponse();
            Header[] headers = response.getHeaders(name);

            if (headers != null && headers.length > 0) {
                return headers[0].getValue();
            }

            return null;
        }

        public List<String> getHeader(String name) throws IOException {
            readResponse();
            Header[] headers = response.getHeaders(name);

            if (headers != null && headers.length > 0) {
                return Stream.of(headers).map(Header::getValue).collect(Collectors.toList());
            }

            return null;
        }

        public void close() throws IOException {
            readResponse();
        }
    }

}

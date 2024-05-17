package org.mposolda.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;
import org.jboss.logging.Logger;

/**
 * Downloading info from google
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class GoogleClient implements Closeable {

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    private static final double SP500_FALLBACK = 5297.1;

    private final CloseableHttpClient httpClient;

    public GoogleClient() {
        this.httpClient = new HttpClientBuilder().build();
    }

    public double getSp500() {
        try {
            // Original URL, which is incorrect according to Java, but works in the browser
            // String url = "https://www.google.com/async/finance_wholepage_price_updates?ei=goTgZfGcJ9L7i-gP8euW8Ac&opi=89978449&sca_esv=7fdb6e941e7712e0&yv=3&cs=0&async=mids:%2Fm%2F016yss|%2Fm%2F0cqyw|%2Fm%2F02853rl|%2Fm%2F04zvfw|%2Fm%2F02pjjn9,currencies:,_fmt:jspb";
            String url = "https://www.google.com/async/finance_wholepage_price_updates?ei=goTgZfGcJ9L7i-gP8euW8Ac&opi=89978449&sca_esv=7fdb6e941e7712e0&yv=3&cs=0&async=mids:%2Fm%2F016yss";

            String rawData = SimpleHttp.doGet(url, httpClient)
                    .asString();
            // Some strange stuff at the beginning...
            rawData = rawData.substring(5);
            Map<String, Object> data = JsonSerialization.readValue(rawData, Map.class);

            data = getNestedMap(data, "PriceUpdate");
            data = getNestedMap(data, "entities");
            data = getNestedMap(data, "financial_entity");
            data = getNestedMap(data, "common_entity_data");

            double value = (Double) data.get("last_value_dbl");
            log.infof("S&P 500 value from google: %s", value);
            return value;
        } catch (IOException ioe) {
            log.errorf(ioe, "Exception getting S&P 500 from google. Fallback to value %s", SP500_FALLBACK);
            return SP500_FALLBACK;
        }
    }

    private Map<String, Object> getNestedMap(Map<String, Object> inputMap, String key) {
        Object o = inputMap.get(key);
        if (o instanceof Map) {
            return (Map<String, Object>) o;
        } else if (o instanceof List) {
            List l = (List) o;
            return (Map<String, Object>) l.get(0);
        } else {
            throw new IllegalArgumentException("Key " + key + " is of unexpected type " + o.getClass() + ". O is " + o);
        }
    }

    @Override
    public void close() throws IOException {
        log.info("Closing Google HTTP Client");
        httpClient.close();
    }
}

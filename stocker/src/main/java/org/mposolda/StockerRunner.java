package org.mposolda;

import org.apache.http.client.HttpClient;
import org.mposolda.cli.StockerCli;
import org.mposolda.client.FinnhubHttpClient;
import org.mposolda.client.HttpClientBuilder;
import org.mposolda.services.CompanyInfoManager;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StockerRunner {

    public static void main(String[] args) throws Exception {
        String token = System.getProperty("token");
        if (token == null) {
            throw new IllegalArgumentException("Need to provide system property 'token' with the finnhub API token");
        }
        String companiesJsonFileLocation = System.getProperty("companiesJson");
        if (companiesJsonFileLocation == null) {
            throw new IllegalArgumentException("Need to provide system property 'companiesJson' with the JSON information about companies");
        }

        FinnhubHttpClient finhubClient = new FinnhubHttpClient(token);

        CompanyInfoManager mgr = new CompanyInfoManager(finhubClient, companiesJsonFileLocation);
        mgr.run();

        StockerCli cli = new StockerCli(finhubClient);
        cli.start();
    }
}

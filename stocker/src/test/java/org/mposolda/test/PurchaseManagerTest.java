package org.mposolda.test;


import org.junit.Assert;
import org.junit.Test;
import org.mposolda.services.PurchaseManager;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class PurchaseManagerTest {

    @Test
    public void testStocks1() {
        String jsonFile = getJsonFilesDir() + "/stocks-1-usd-single-company-no-fees.json";

        PurchaseManager mgr = new PurchaseManager(jsonFile);
        mgr.start();

        PurchaseManager.CompanyPurchasesPrice companies = mgr.getCompanyPurchases("FOO");
        assertPurchases(companies, 205000, 100000, 105000);

        assertCurrency(mgr.getCurrenciesInfo(), "USD", 500);
        assertCurrency(mgr.getCurrenciesInfo(), "CZK", 290000);
    }


    private String getJsonFilesDir() {
        return System.getProperty("user.dir") + "/src/test/resources";
    }

    private void assertPurchases(PurchaseManager.CompanyPurchasesPrice companies, double expectedTotalCZK, double... expectedPurchasesPriceCZK) {
        Assert.assertEquals(expectedTotalCZK, companies.getTotalCZKPriceOfAllPurchases(), 0.1);

        Assert.assertEquals(expectedPurchasesPriceCZK.length, companies.getPurchases().size());

        int i = 0;
        for (PurchaseManager.CompanyPurchaseInternal companyPurchase : companies.getPurchases()) {
            Assert.assertEquals(expectedPurchasesPriceCZK[i], companyPurchase.getTotalPriceInCZK(), 0.1);
            i++;
        }
    }

    private void assertCurrency(PurchaseManager.CurrenciesInfo currencies, String currencyTicker, double expectedRemaining) {
        double remaining = currencies.getCurrencyRemainingAmount().get(currencyTicker);
        Assert.assertEquals(remaining, expectedRemaining, 0.1);
    }
}

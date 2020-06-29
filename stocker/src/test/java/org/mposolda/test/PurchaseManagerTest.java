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

    @Test
    public void testStocks2() {
        String jsonFile = getJsonFilesDir() + "/stocks-2-usd-single-company-fees.json";

        PurchaseManager mgr = new PurchaseManager(jsonFile);
        mgr.start();

        PurchaseManager.CompanyPurchasesPrice companies = mgr.getCompanyPurchases("FOO");
        assertPurchases(companies, 4340, 2200, 2140);
        assertPurchaseFees(companies, 340, 200, 140);

        assertCurrency(mgr.getCurrenciesInfo(), "USD", 1783);
        assertCurrency(mgr.getCurrenciesInfo(), "CZK", 459800);

        Assert.assertEquals(200, mgr.getCurrenciesInfo().getCzkFeesTotal(), 0.1);
    }

    @Test
    public void testStocks3() {
        String jsonFile = getJsonFilesDir() + "/stocks-3-usd-cad-more-companies-no-fees.json";

        PurchaseManager mgr = new PurchaseManager(jsonFile);
        mgr.start();

        PurchaseManager.CompanyPurchasesPrice companies = mgr.getCompanyPurchases("FOOCAD");
        assertPurchases(companies, 11960, 11000, 960);
        assertPurchaseFees(companies, 0, 0, 0);

        assertCurrency(mgr.getCurrenciesInfo(), "CZK", 480000);
        assertCurrency(mgr.getCurrenciesInfo(), "USD", 400);
        assertCurrency(mgr.getCurrenciesInfo(), "CAD", 20);

        Assert.assertEquals(0, mgr.getCurrenciesInfo().getCzkFeesTotal(), 0.1);
    }

    @Test
    public void testStocks4() {
        String jsonFile = getJsonFilesDir() + "/stocks-4-usd-cad-more-companies-fees.json";

        PurchaseManager mgr = new PurchaseManager(jsonFile);
        mgr.start();

        PurchaseManager.CompanyPurchasesPrice companies = mgr.getCompanyPurchases("FOOCAD");
        assertPurchases(companies, 11980, 11010, 970);
        assertPurchaseFees(companies, 46.58, 36.58, 10);

        assertCurrency(mgr.getCurrenciesInfo(), "CZK", 479700);
        assertCurrency(mgr.getCurrenciesInfo(), "USD", 400);
        assertCurrency(mgr.getCurrenciesInfo(), "CAD", 10);

        Assert.assertEquals(300, mgr.getCurrenciesInfo().getCzkFeesTotal(), 0.1);
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

    private void assertPurchaseFees(PurchaseManager.CompanyPurchasesPrice companies, double expectedTotalFeesCZK, double... expectedFeesCZK) {
        Assert.assertEquals(expectedTotalFeesCZK, companies.getTotalCZKPriceOfAllFees(), 0.1);

        Assert.assertEquals(expectedFeesCZK.length, companies.getPurchases().size());

        int i = 0;
        for (PurchaseManager.CompanyPurchaseInternal companyPurchase : companies.getPurchases()) {
            Assert.assertEquals(expectedFeesCZK[i], companyPurchase.getTotalFeeInCZK(), 0.1);
            i++;
        }
    }

    private void assertCurrency(PurchaseManager.CurrenciesInfo currencies, String currencyTicker, double expectedRemaining) {
        double remaining = currencies.getCurrencyRemainingAmount().get(currencyTicker);
        Assert.assertEquals(remaining, expectedRemaining, 0.1);
    }
}

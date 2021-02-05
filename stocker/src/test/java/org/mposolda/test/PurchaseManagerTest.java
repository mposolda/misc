package org.mposolda.test;


import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mposolda.reps.DividendsSumPerYear;
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

        PurchaseManager.CompanyPurchasesPrice company = mgr.getCompanyPurchases("FOO");
        assertPurchases(company, 205000, 100000, 105000);

        assertCurrency(mgr.getCurrenciesInfo(), "USD", 500);
        assertCurrency(mgr.getCurrenciesInfo(), "CZK", 290000);
    }

    @Test
    public void testStocks2() {
        String jsonFile = getJsonFilesDir() + "/stocks-2-usd-single-company-fees.json";

        PurchaseManager mgr = new PurchaseManager(jsonFile);
        mgr.start();

        PurchaseManager.CompanyPurchasesPrice company = mgr.getCompanyPurchases("FOO");
        assertPurchases(company, 4340, 2200, 2140);
        assertPurchaseAndDisposalFees(company, 340, 200, 140);

        assertCurrency(mgr.getCurrenciesInfo(), "USD", 1783);
        assertCurrency(mgr.getCurrenciesInfo(), "CZK", 459800);

        Assert.assertEquals(200, mgr.getCurrenciesInfo().getCzkFeesTotal(), 0.1);
    }

    @Test
    public void testStocks3() {
        String jsonFile = getJsonFilesDir() + "/stocks-3-usd-cad-more-companies-no-fees.json";

        PurchaseManager mgr = new PurchaseManager(jsonFile);
        mgr.start();

        PurchaseManager.CompanyPurchasesPrice company = mgr.getCompanyPurchases("FOOCAD");
        assertPurchases(company, 11960, 11000, 960);
        assertPurchaseAndDisposalFees(company, 0, 0, 0);

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

        PurchaseManager.CompanyPurchasesPrice company = mgr.getCompanyPurchases("FOOCAD");
        assertPurchases(company, 11980, 11010, 970);
        assertPurchaseAndDisposalFees(company, 46.58, 36.58, 10);

        assertCurrency(mgr.getCurrenciesInfo(), "CZK", 479700);
        assertCurrency(mgr.getCurrenciesInfo(), "USD", 400);
        assertCurrency(mgr.getCurrenciesInfo(), "CAD", 10);

        Assert.assertEquals(300, mgr.getCurrenciesInfo().getCzkFeesTotal(), 0.1);
    }

    @Test
    public void testStocks5() {
        String jsonFile = getJsonFilesDir() + "/stocks-5-usd-single-company-no-fees-dividends.json";

        PurchaseManager mgr = new PurchaseManager(jsonFile);
        mgr.start();

        PurchaseManager.CompanyPurchasesPrice company = mgr.getCompanyPurchases("FOO");
        assertPurchases(company, 225000, 150000, 75000);
        //assertPurchaseAndDisposalFees(companies, 46.58, 36.58, 10);

        assertCurrency(mgr.getCurrenciesInfo(), "CZK", 300000);
        assertCurrency(mgr.getCurrenciesInfo(), "USD", 0);

        Assert.assertEquals(0, mgr.getCurrenciesInfo().getCzkFeesTotal(), 0.1);

        Assert.assertEquals(2500, company.getTotalDividendsPaymentsInOriginalCurrency(), 0.1);
        Assert.assertEquals(25000, company.getTotalDividendsPaymentsInCZK(), 0.1);
    }


    @Test
    public void testStocks6() {
        String jsonFile = getJsonFilesDir() + "/stocks-6-usd-cad-more-companies-fees-dividends.json";

        PurchaseManager mgr = new PurchaseManager(jsonFile);
        mgr.start();

        PurchaseManager.CompanyPurchasesPrice company = mgr.getCompanyPurchases("FOOCAD");
        assertPurchases(company, 12280, 11010, 970, 20, 280);
        assertPurchaseAndDisposalFees(company, 139.91, 36.58, 10, 0, 93.33);

        assertCurrency(mgr.getCurrenciesInfo(), "CZK", 479700);
        assertCurrency(mgr.getCurrenciesInfo(), "USD", 400);
        assertCurrency(mgr.getCurrenciesInfo(), "CAD", 0);

        Assert.assertEquals(300, mgr.getCurrenciesInfo().getCzkFeesTotal(), 0.1);

        Assert.assertEquals(30, company.getTotalDividendsPaymentsInOriginalCurrency(), 0.1);
        Assert.assertEquals(280, company.getTotalDividendsPaymentsInCZK(), 0.1);
    }


    @Test
    public void testStocks7() {
        String jsonFile = getJsonFilesDir() + "/stocks-7-usd-single-company-fees-sold.json";

        PurchaseManager mgr = new PurchaseManager(jsonFile);
        mgr.start();

        PurchaseManager.CompanyPurchasesPrice company = mgr.getCompanyPurchases("FOO");
        assertPurchases(company, 4340, 2200, 2140);
        assertPurchaseAndDisposalFees(company, 550, 200, 140, 210);

        assertDisposals(company, 210, 4410, 4410);

        assertCurrency(mgr.getCurrenciesInfo(), "USD", 1993);
        assertCurrency(mgr.getCurrenciesInfo(), "CZK", 459800);

        Assert.assertEquals(200, mgr.getCurrenciesInfo().getCzkFeesTotal(), 0.1);
    }


    @Test
    public void testStocks8() {
        String jsonFile = getJsonFilesDir() + "/stocks-8-usd-single-company-fees-dividends-sold.json";

        PurchaseManager mgr = new PurchaseManager(jsonFile);
        mgr.start();

        PurchaseManager.CompanyPurchasesPrice company = mgr.getCompanyPurchases("FOO");
        assertPurchases(company, 4340, 2200, 2140);
        assertPurchaseAndDisposalFees(company, 550, 200, 140, 210);

        assertDisposals(company, 210, 4410, 4410);

        assertCurrency(mgr.getCurrenciesInfo(), "USD", 2013);
        assertCurrency(mgr.getCurrenciesInfo(), "CZK", 459800);

        Assert.assertEquals(200, mgr.getCurrenciesInfo().getCzkFeesTotal(), 0.1);

        List<DividendsSumPerYear> dividendSums = company.getDividendsSumsPerYear();
        Assert.assertEquals(2, dividendSums.size());
        assertDividendsSum(dividendSums.get(0), 2020, 15, 150, 10);
        assertDividendsSum(dividendSums.get(1), 2021, 5, 50, 10);
    }

    @Test
    public void testStocks9() {
        String jsonFile = getJsonFilesDir() + "/stocks-9-gbp-single-company-disposals-fees.json";

        PurchaseManager mgr = new PurchaseManager(jsonFile);
        mgr.start();

        PurchaseManager.CompanyPurchasesPrice company = mgr.getCompanyPurchases("DREC.L");
        assertPurchases(company, 45150, 45150);
        assertPurchaseAndDisposalFees(company, 3150, 150, 1500, 1500);

        assertDisposals(company, 1550, 46500, 22500, 24000);

        assertCurrency(mgr.getCurrenciesInfo(), "GBP", 2045);
        assertCurrency(mgr.getCurrenciesInfo(), "CZK", 439900);

        Assert.assertEquals(100, mgr.getCurrenciesInfo().getCzkFeesTotal(), 0.1);
    }


    private String getJsonFilesDir() {
        return System.getProperty("user.dir") + "/src/test/resources";
    }

    private void assertPurchases(PurchaseManager.CompanyPurchasesPrice company, double expectedTotalCZK, double... expectedPurchasesPriceCZK) {
        Assert.assertEquals(expectedTotalCZK, company.getTotalCZKPriceOfAllPurchases(), 0.1);

        Assert.assertEquals(expectedPurchasesPriceCZK.length, company.getPurchases().size());

        int i = 0;
        for (PurchaseManager.CompanyPurchaseInternal companyPurchase : company.getPurchases()) {
            Assert.assertEquals(expectedPurchasesPriceCZK[i], companyPurchase.getTotalPriceInCZK(), 0.1);
            i++;
        }
    }

    private void assertDisposals(PurchaseManager.CompanyPurchasesPrice company, double expectedTotalInOrigCurrency,
                                 double expectedTotalInCZK, double... expectedDisposalsCZK) {
        Assert.assertEquals(expectedTotalInOrigCurrency, company.getTotalDisposalsPaymentsInOriginalCurrency(), 0.1);
        Assert.assertEquals(expectedTotalInCZK, company.getTotalDisposalsPaymentsInCZK(), 0.1);

        Assert.assertEquals(expectedDisposalsCZK.length, company.getDisposals().size());

        int i = 0;
        for (PurchaseManager.DisposalInternal stockDisposal : company.getDisposals()) {
            Assert.assertEquals(expectedDisposalsCZK[i], stockDisposal.getTotalAmountInCZK(), 0.1);
            i++;
        }
    }

    private void assertPurchaseAndDisposalFees(PurchaseManager.CompanyPurchasesPrice companies, double expectedTotalFeesCZK, double... expectedFeesCZK) {
        Assert.assertEquals(expectedTotalFeesCZK, companies.getTotalCZKPriceOfAllFees(), 0.1);

        Assert.assertEquals(expectedFeesCZK.length, companies.getPurchases().size() + companies.getDisposals().size());

        // Test all purchases first. Then test all disposals. Hence it does not depend on the exact time
        int i = 0;
        for (PurchaseManager.CompanyPurchaseInternal companyPurchase : companies.getPurchases()) {
            Assert.assertEquals(expectedFeesCZK[i], companyPurchase.getTotalFeeInCZK(), 0.1);
            i++;
        }

        for (PurchaseManager.DisposalInternal companyDisposal : companies.getDisposals()) {
            Assert.assertEquals(expectedFeesCZK[i], companyDisposal.getTotalFeeInCZK(), 0.1);
            i++;
        }
    }

    private void assertCurrency(PurchaseManager.CurrenciesInfo currencies, String currencyTicker, double expectedRemaining) {
        double remaining = currencies.getCurrencyRemainingAmount().get(currencyTicker);
        Assert.assertEquals(remaining, expectedRemaining, 0.1);
    }

    private void assertDividendsSum(DividendsSumPerYear dividendsSum, int expectedYear, double expectedOrigCurrencySum, double expectedCZKSum, double expectedQuotationToCZK) {
        Assert.assertEquals(dividendsSum.getYear(), expectedYear);
        Assert.assertEquals(dividendsSum.getTotalDividendsPaymentsInOriginalCurrency(), expectedOrigCurrencySum, 0.1);
        Assert.assertEquals(dividendsSum.getTotalDividendsPaymentsInCZK(), expectedCZKSum, 0.1);
        Assert.assertEquals(dividendsSum.getAverageQuotationToCZK(), expectedQuotationToCZK, 0.1);
    }
}

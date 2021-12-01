package org.mposolda.services;

import java.util.ArrayList;
import java.util.List;

import org.mposolda.util.DateUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class PurchaseTaxManager {

    // 3 years
    private static final int TAX_HOLDING_INTERVAL_DAYS = 366 + 365 + 365;

    // NOTE: Hardcoded to 15%, but it may be good to add info to the disposal about how much it should be taxed
    private static final double TAX_RATIO = 0.15;

    // Compute tax for all disposals of the company and call "setGainInCZK()" and "setTaxFromDisposalInCZK()" for all disposals
    public void computeTaxPurchases(PurchaseManager.CompanyPurchasesPrice companyPurchases) {
        // Add all company purchases
        Stack st = new Stack(companyPurchases.getCompanyTicker());
        for (PurchaseManager.CompanyPurchaseInternal prInt : companyPurchases.getPurchases()) {
            st.addPurchase(prInt);
        }

        // Iterate over disposals and compute tax for each
        for (PurchaseManager.DisposalInternal dispInternal : companyPurchases.getDisposals()) {
            st.processDisposal(dispInternal);
        }

    }


    private class Stack {

        private final String companyTicker;
        private List<PurchaseEntry> remainingPurchases = new ArrayList<>();

        private Stack(String companyTicker) {
            this.companyTicker = companyTicker;
        }

        private void addPurchase(PurchaseManager.CompanyPurchaseInternal prInternal) {
            this.remainingPurchases.add(new PurchaseEntry(prInternal.getDate(), prInternal.getStocksCount(), prInternal.getPricePerStock(), prInternal.getFeeInOriginalCurrency()));
        }

        private boolean isEmpty() {
            return this.remainingPurchases.isEmpty();
        }

        private PurchaseEntry getFirst() {
            return remainingPurchases.get(0);
        }

        private void removeFirst() {
            remainingPurchases.remove(0);
        }

        private void replaceFirst(PurchaseEntry entry) {
            removeFirst();
            remainingPurchases.add(0, entry);
        }

        private void processDisposal(PurchaseManager.DisposalInternal disposal) {
            int soldStocks = disposal.getSoldStocksCount();
            int remainingSoldStocks = soldStocks;

            // Compute used purchases
            List<PurchaseEntry> usedPurchases = new ArrayList<>();
            while (remainingSoldStocks > 0) {
                if (isEmpty()) {
                    throw new IllegalStateException("Not enough purchases for process disposal of company " + companyTicker);
                }

                PurchaseEntry currentPurchase = getFirst();
                if (currentPurchase.stocksCount <= remainingSoldStocks) {
                    // Purchase can be fully used
                    usedPurchases.add(currentPurchase);
                    removeFirst();
                    remainingSoldStocks -= currentPurchase.stocksCount;
                } else {
                    // Purchase cannot be fully used, just partially
                    PurchaseEntry usedEntry = new PurchaseEntry(currentPurchase.date, remainingSoldStocks, currentPurchase.pricePerStock, 0);
                    usedPurchases.add(usedEntry);
                    PurchaseEntry entryToAdd = new PurchaseEntry(currentPurchase.date, currentPurchase.stocksCount - remainingSoldStocks, currentPurchase.pricePerStock, currentPurchase.feeInOrigCurrency);
                    replaceFirst(entryToAdd);
                    break;
                }
            }

            // Compute tax from used purchases
            double totalTaxBasedGainInOrigCurrency = 0;
            double totalGainInOrigCurrency = 0;
            for (PurchaseEntry purchase : usedPurchases) {

                double soldPricePerStock = (disposal.getTotalAmountInOriginalCurrency() + disposal.getTotalFeeInOriginalCurrency()) / disposal.getSoldStocksCount();
                double currentPurchaseTaxOrig = purchase.stocksCount * (soldPricePerStock - purchase.pricePerStock);
                currentPurchaseTaxOrig -= purchase.feeInOrigCurrency;

                totalGainInOrigCurrency += currentPurchaseTaxOrig;

                // Time-test passed. Hence won't take into consideration
                if (purchase.isTaxNeeded(disposal.getDate())) {
                    totalTaxBasedGainInOrigCurrency += currentPurchaseTaxOrig;
                }
            }

            // Remove disposal fee as well from the tax-based gain
            totalGainInOrigCurrency -= disposal.getTotalFeeInOriginalCurrency();
            totalTaxBasedGainInOrigCurrency -= disposal.getTotalFeeInOriginalCurrency();

            double totalTaxBasedGainInCZK = totalGainInOrigCurrency * disposal.getCzkAmountForOneUnit();

            // We can add the tax to the disposal
            disposal.setGainForTaxInCZK(totalTaxBasedGainInCZK);
            disposal.setTaxFromDisposalInCZK(totalTaxBasedGainInOrigCurrency * disposal.getCzkAmountForOneUnit() * TAX_RATIO);
        }
    }

    private class PurchaseEntry {

        private String date;
        private int stocksCount;
        private double pricePerStock;
        private double feeInOrigCurrency;

        public PurchaseEntry(String date, int stocksCount, double pricePerStock, double feeInOrigCurrency) {
            this.date = date;
            this.stocksCount = stocksCount;
            this.pricePerStock = pricePerStock;
            this.feeInOrigCurrency = feeInOrigCurrency;
        }

        private boolean isTaxNeeded(String dateOfDisposal) {
            long datePurchaseSecs = DateUtil.dateToNumberSeconds(date);
            long dateDisposalSecs = DateUtil.dateToNumberSeconds(dateOfDisposal);

            long interval = dateDisposalSecs - datePurchaseSecs;
            return TAX_HOLDING_INTERVAL_DAYS * 86400 > interval;
        }
    }

}

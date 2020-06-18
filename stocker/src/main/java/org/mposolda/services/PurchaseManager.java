package org.mposolda.services;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.logging.Logger;
import org.mposolda.reps.CompanyRep;
import org.mposolda.reps.CurrencyRep;
import org.mposolda.reps.DatabaseRep;
import org.mposolda.reps.PurchaseRep;
import org.mposolda.util.DateUtil;
import org.mposolda.util.JsonUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class PurchaseManager {

    // TODO:mposolda this should not be hardcoded!
    public static final double CZK_DEPOSIT = 1790000;

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    private final String  companiesJsonFileLocation;

    // All purchases of all companies
    private Map<String, CompanyPurchasesPrice> companiesPurchases = new HashMap<>();

    PurchaseManager(String companiesJsonFileLocation) {
        this.companiesJsonFileLocation = companiesJsonFileLocation;
    }


    /**
     *
     * @return amount of CZK, which corresponds to buying the
     */
    public CompanyPurchasesPrice getCompanyPurchases(String companyTicker) {
        return companiesPurchases.get(companyTicker);
    }

    public void start() {
        // Load company informations from JSON file
        DatabaseRep database = JsonUtil.loadDatabase(this.companiesJsonFileLocation);

        // 1 - Sort the company purchases by date
        Set<PurchaseInternalRep> sortedPurchases = new TreeSet<>(new Comparator<PurchaseInternalRep>() {

            @Override
            public int compare(PurchaseInternalRep o1, PurchaseInternalRep o2) {
                long diff = o1.dateNumber - o2.dateNumber;

                if (diff > 0) {
                    return 1;
                } else if (diff == 0) {
                    return o1.companyTicker.compareTo(o2.companyTicker);
                } else {
                    return -1;
                }
            }

        });

        for (CompanyRep company : database.getCompanies()) {
            List<PurchaseRep> purchases = company.getPurchases();
            for (PurchaseRep purchase : purchases) {
                PurchaseInternalRep pi = new PurchaseInternalRep(company.getTicker(), company.getCurrency(), purchase);
                sortedPurchases.add(pi);
            }
        }


        log.info("Created sorted purchases");

        // 2 - Sort currency purchases
        Set<CurrencyPurchaseInternal> sortedCurrencyPurchases = new TreeSet<>(new Comparator<CurrencyPurchaseInternal>() {

            @Override
            public int compare(CurrencyPurchaseInternal o1, CurrencyPurchaseInternal o2) {
                if (o1.equals(o2)) return 0;

                long diff = o1.dateNumber - o2.dateNumber;

                if (diff > 0) {
                    return 1;
                } else if (diff == 0) {
                    int diff2 = o1.currencyFrom.compareTo(o2.currencyFrom);
                    if (diff2 != 0) {
                        return diff2;
                    } else {
                        throw new IllegalStateException("Two boughts of the currency " + o1.currencyFrom + " done at the same day!");
                    }
                } else {
                    return -1;
                }
            }

        });

        for (CurrencyRep currency : database.getCurrencies()) {
            List<CurrencyRep.CurrencyPurchaseRep> purchases = currency.getPurchases();
            for (CurrencyRep.CurrencyPurchaseRep purchase : purchases) {
                CurrencyPurchaseInternal pi = new CurrencyPurchaseInternal(currency.getTicker(), purchase.getDate(),
                        purchase.getCountBought(), purchase.getPricePerUnit());
                sortedCurrencyPurchases.add(pi);
            }
        }

        // 3 Create currency stacks based on all currency purchases
        Map<String, CurrencyStack> currencyStacks = new HashMap<>();
        for (CurrencyPurchaseInternal currencyPurchaseInternal : sortedCurrencyPurchases) {
            String currencyFromTicker = currencyPurchaseInternal.currencyFrom;

            CurrencyStack stack = currencyStacks.get(currencyFromTicker);
            if (stack == null) {
                stack = new CurrencyStack(currencyFromTicker);
                currencyStacks.put(currencyFromTicker, stack);
            }

            stack.addCurrencyPurchase(currencyPurchaseInternal);
        }

        // 4 - Apply all company purchases. Compute CZK amount for each company purchase
        for (PurchaseInternalRep stockPurchase : sortedPurchases) {
            CurrencyStack stack = currencyStacks.get(stockPurchase.currency);

            // Total price of purchase in "currency from"
            double totalPriceOfPurchase = stockPurchase.stocksCount * stockPurchase.pricePerStock;

            // Now compute the total amount of CZK needed
            CurrencyPurchaseInternal currencyPurchase = stack.currencyPurchases.peek();
            double totalPriceOfPurchaseCZK = 0;
            double remainingPriceOfPurchase = totalPriceOfPurchase;
            while (currencyPurchase.currencyFromAmount < remainingPriceOfPurchase) {
                // The last currencyPurchase does not have sufficient money for buy this stock.
                remainingPriceOfPurchase -= currencyPurchase.currencyFromAmount;
                totalPriceOfPurchaseCZK += currencyPurchase.currencyFromAmount * currencyPurchase.czkAmountForOneFromUnit;

                // Try another purchase
                stack.currencyPurchases.remove();
                currencyPurchase = stack.currencyPurchases.peek();
            }

            currencyPurchase.currencyFromAmount = currencyPurchase.currencyFromAmount - remainingPriceOfPurchase;
            totalPriceOfPurchaseCZK += remainingPriceOfPurchase * currencyPurchase.czkAmountForOneFromUnit;

            stockPurchase.setTotalPriceInCZK(totalPriceOfPurchaseCZK);
        }

        // 5 - Create instance of CompanyPurchasesPrice
        for (PurchaseInternalRep stockPurchase : sortedPurchases) {
            String companyTicker = stockPurchase.companyTicker;

            CompanyPurchasesPrice companyPurchases = this.companiesPurchases.get(companyTicker);
            if (companyPurchases == null) {
                companyPurchases = new CompanyPurchasesPrice(companyTicker);
                companiesPurchases.put(companyTicker, companyPurchases);
            }

            companyPurchases.addPurchase(stockPurchase);
        }


    }

    // Class, which corresponds overal amount of CZK bought for ALL the purchases of the particular company
    public static class CompanyPurchasesPrice {

        private final String companyTicker;

        private final List<PurchaseInternalRep> purchases = new ArrayList<>();

        private CompanyPurchasesPrice(String companyTicker) {
            this.companyTicker = companyTicker;
        }

        public List<PurchaseInternalRep> getPurchases() {
            return purchases;
        }

        public void addPurchase(PurchaseInternalRep purchase) {
            this.purchases.add(purchase);
        }

        // Get total price of all purchases
        public double getTotalCZKPriceOfAllPurchases() {
            double sum = 0;
            for (PurchaseInternalRep purchase : purchases) {
                sum += purchase.getTotalPriceInCZK();
            }
            return sum;
        }
    }


    public static class PurchaseInternalRep {

        private final String companyTicker;
        private final String currency;

        private final String date;
        private final long dateNumber;
        private final int stocksCount;
        private final double pricePerStock;

        private double totalPriceInCZK;

        private PurchaseInternalRep(String companyTicker, String currency, PurchaseRep purchase) {
            this.companyTicker = companyTicker;
            this.currency = currency;
            this.date = purchase.getDate();
            this.stocksCount = purchase.getStocksCount();
            this.pricePerStock = purchase.getPricePerStock();

            // Compute date
            this.dateNumber = DateUtil.dateToNumber(date);
        }

        private void setTotalPriceInCZK(double totalPriceInCZK) {
            this.totalPriceInCZK = totalPriceInCZK;
        }

        // Get total price of this purchase in CZK
        public double getTotalPriceInCZK() {
            return this.totalPriceInCZK;
        }

    }


    private static class CurrencyStack {

        private final String currencyTicker;

        private final Queue<CurrencyPurchaseInternal> currencyPurchases = new LinkedList<>();

        public CurrencyStack(String currencyTicker) {
            this.currencyTicker = currencyTicker;
        }

        private void addCurrencyPurchase(CurrencyPurchaseInternal currencyPurchaseInternal) {
            currencyPurchases.add(currencyPurchaseInternal);
        }

    }

    private static class CurrencyPurchaseInternal {

        private final String date;
        private final long dateNumber;

        private final String currencyFrom;

        // private final String currencyTo;

        private double currencyFromAmount;

        // TODO:mposolda not sure if this is always be CZK? Or other property for "currencyTo" needed?
        private final double czkAmountForOneFromUnit;

        public CurrencyPurchaseInternal(String currencyFrom, String date, double currencyFromAmount, double czkAmountForOneFromUnit) {
            this.currencyFrom = currencyFrom;
            this.date = date;
            this.dateNumber = DateUtil.dateToNumber(date);
            this.currencyFromAmount = currencyFromAmount;
            this.czkAmountForOneFromUnit = czkAmountForOneFromUnit;
        }



    }
}

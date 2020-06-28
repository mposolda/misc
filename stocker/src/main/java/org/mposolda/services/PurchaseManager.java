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

    protected final Logger log = Logger.getLogger(this.getClass().getName());

    private final String  companiesJsonFileLocation;

    // All purchases of all companies
    private Map<String, CompanyPurchasesPrice> companiesPurchases = new HashMap<>();

    private CurrenciesInfo currenciesInfo = new CurrenciesInfo();

    public PurchaseManager(String companiesJsonFileLocation) {
        this.companiesJsonFileLocation = companiesJsonFileLocation;
    }


    /**
     *
     * @return amount of CZK, which corresponds to buying the
     */
    public CompanyPurchasesPrice getCompanyPurchases(String companyTicker) {
        return companiesPurchases.get(companyTicker);
    }

    /**
     * @return info about total CZK deposit and about the remaining amount of each currency
     */
    public CurrenciesInfo getCurrenciesInfo() {
        return currenciesInfo;
    }

    public void start() {
        // Load company informations from JSON file
        DatabaseRep database = JsonUtil.loadDatabase(this.companiesJsonFileLocation);

        // 1 - Sort the company purchases by date
        Comparator<CompanyPurchaseInternal> companyPurchaseComparator = new Comparator<CompanyPurchaseInternal>() {

            @Override
            public int compare(CompanyPurchaseInternal o1, CompanyPurchaseInternal o2) {
                if (o1.equals(o2)) return 0;

                long diff = o1.dateNumber - o2.dateNumber;

                if (diff > 0) {
                    return 1;
                } else if (diff == 0) {
                    return o1.companyTicker.compareTo(o2.companyTicker);
                } else {
                    return -1;
                }
            }

        };
        Set<CompanyPurchaseInternal> sortedCompanyPurchases = new TreeSet<>(companyPurchaseComparator);

        for (CompanyRep company : database.getCompanies()) {
            List<PurchaseRep> purchases = company.getPurchases();
            for (PurchaseRep purchase : purchases) {
                CompanyPurchaseInternal pi = new CompanyPurchaseInternal(company.getTicker(), company.getCurrency(), purchase);
                sortedCompanyPurchases.add(pi);
            }
        }


        log.info("Created sorted purchases");

        // 2 - Sort currency purchases
        Comparator<CurrencyPurchaseInternal> currencyPurchaseComparator = new Comparator<CurrencyPurchaseInternal>() {

            @Override
            public int compare(CurrencyPurchaseInternal o1, CurrencyPurchaseInternal o2) {
                if (o1.equals(o2)) return 0;

                long diff = o1.dateNumber - o2.dateNumber;

                if (diff > 0) {
                    return 1;
                } else if (diff == 0) {
                    int diff2 = o1.currencyTo.compareTo(o2.currencyTo);
                    if (diff2 != 0) {
                        return diff2;
                    } else {
                        throw new IllegalStateException("Two boughts of the currency " + o1.currencyTo + " done at the same day!");
                    }
                } else {
                    return -1;
                }
            }

        };
        Set<CurrencyPurchaseInternal> sortedCurrencyPurchases = new TreeSet<>(currencyPurchaseComparator);

        for (CurrencyRep currency : database.getCurrencies()) {
            List<CurrencyRep.CurrencyPurchaseRep> purchases = currency.getPurchases();
            for (CurrencyRep.CurrencyPurchaseRep purchase : purchases) {
                double currencyFromAmount = purchase.getCountBought() * purchase.getPricePerUnit();

                CurrencyPurchaseInternal pi = new CurrencyPurchaseInternal(purchase.getDate(), purchase.getCurrencyFrom(), currencyFromAmount,
                        currency.getTicker(), purchase.getCountBought());
                sortedCurrencyPurchases.add(pi);
            }
        }

        // 3 Create list of all purchases (currency + company purchases)
        Set<PurchaseInternal> allSortedPurchases = new TreeSet<>(new Comparator<PurchaseInternal>() {

            @Override
            public int compare(PurchaseInternal o1, PurchaseInternal o2) {
                if (o1.equals(o2)) return 0;

                long diff = o1.getDateNumber() - o2.getDateNumber();
                if (diff >0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }

                if (o1 instanceof CurrencyPurchaseInternal && o2 instanceof CurrencyPurchaseInternal)
                    return currencyPurchaseComparator.compare((CurrencyPurchaseInternal)o1, (CurrencyPurchaseInternal) o2);

                if (o1 instanceof CompanyPurchaseInternal && o2 instanceof CompanyPurchaseInternal)
                    return companyPurchaseComparator.compare((CompanyPurchaseInternal)o1, (CompanyPurchaseInternal) o2);

                // Currency purchases must go first
                if (o1 instanceof CurrencyPurchaseInternal && o2 instanceof CompanyPurchaseInternal) {
                    return -1;
                } else {
                    return 1;
                }

            }

        });
        allSortedPurchases.addAll(sortedCurrencyPurchases);
        allSortedPurchases.addAll(sortedCompanyPurchases);


        // 4 Create currency stacks.
        Map<String, CurrencyStack> currencyStacks = new HashMap<>();
        CurrencyRep czkRep = null;
        for (CurrencyRep currencyRep : database.getCurrencies()) {
            currencyStacks.put(currencyRep.getTicker(), new CurrencyStack(currencyRep.getTicker()));
            if ("CZK".equals(currencyRep.getTicker())) czkRep = currencyRep;
        }

        // 5 Add CZK deposits to the CZK stack and nothing else
        CurrencyStack czkStack = currencyStacks.get("CZK");
        double totalDepositCZK = 0;
        String dateOfFirstDeposit = null;
        for (CurrencyRep.CurrencyDepositRep deposit : czkRep.getDeposits()) {
            totalDepositCZK += deposit.getCountBought();
            if (dateOfFirstDeposit == null) dateOfFirstDeposit = deposit.getDate();
        }
        CurrencyPurchaseInternal czkPurchase = new CurrencyPurchaseInternal(dateOfFirstDeposit, "CZK", 0, "CZK", totalDepositCZK);
        czkPurchase.czkAmountForOneToUnit = 1;
        czkStack.addCurrencyPurchase(czkPurchase);

        // 5 - Apply all company purchases and currency purchases based on the time. Compute CZK amount for each company purchase
        // and each currency purchase
        for (PurchaseInternal purchase : allSortedPurchases) {

            if (purchase instanceof CompanyPurchaseInternal) {
                // 5.1 COMPANY PURCHASE
                CompanyPurchaseInternal stockPurchase = (CompanyPurchaseInternal) purchase;
                CurrencyStack stack = currencyStacks.get(stockPurchase.currency);

                // Total price of purchase in "currency from"
                double totalPriceOfPurchase = stockPurchase.stocksCount * stockPurchase.pricePerStock;

                // Now compute the total amount of CZK needed
                CurrencyPurchaseInternal currencyPurchase = stack.currencyPurchases.peek();
                double totalPriceOfPurchaseCZK = 0;
                double remainingPriceOfPurchase = totalPriceOfPurchase;
                while (currencyPurchase.currencyToAmount < remainingPriceOfPurchase) {
                    // The last currencyPurchase does not have sufficient money for buy this stock.
                    remainingPriceOfPurchase -= currencyPurchase.currencyToAmount;
                    totalPriceOfPurchaseCZK += currencyPurchase.currencyToAmount * currencyPurchase.czkAmountForOneToUnit;

                    // Try another purchase
                    stack.currencyPurchases.remove();
                    currencyPurchase = stack.currencyPurchases.peek();
                }

                currencyPurchase.currencyToAmount = currencyPurchase.currencyToAmount - remainingPriceOfPurchase;
                totalPriceOfPurchaseCZK += remainingPriceOfPurchase * currencyPurchase.czkAmountForOneToUnit;

                stockPurchase.setTotalPriceInCZK(totalPriceOfPurchaseCZK);
            } else {
                // 5.2 CURRENCY PURCHASE
                CurrencyPurchaseInternal currencyPurchaseTarget = (CurrencyPurchaseInternal) purchase;

                CurrencyStack stackFrom = currencyStacks.get(currencyPurchaseTarget.currencyFrom);
                CurrencyStack stackTo = currencyStacks.get(currencyPurchaseTarget.currencyTo);

                // Total price of purchase in "currency from"
                CurrencyPurchaseInternal currencyPurchase = stackFrom.currencyPurchases.peek();
                double totalPriceOfPurchaseCZK = 0;
                double remainingPriceOfPurchase = currencyPurchaseTarget.currencyFromAmount;
                while (currencyPurchase.currencyToAmount < remainingPriceOfPurchase) {
                    // The last currencyPurchase does not have sufficient money for buy this stock.
                    remainingPriceOfPurchase -= currencyPurchase.currencyToAmount;
                    totalPriceOfPurchaseCZK += currencyPurchase.currencyToAmount * currencyPurchase.czkAmountForOneToUnit;

                    // Try another purchase
                    stackFrom.currencyPurchases.remove();
                    currencyPurchase = stackFrom.currencyPurchases.peek();
                }

                currencyPurchase.currencyToAmount = currencyPurchase.currencyToAmount - remainingPriceOfPurchase;
                totalPriceOfPurchaseCZK += remainingPriceOfPurchase * currencyPurchase.czkAmountForOneToUnit;

                currencyPurchaseTarget.czkAmountForOneToUnit = totalPriceOfPurchaseCZK / currencyPurchaseTarget.currencyToAmount;

                stackTo.addCurrencyPurchase(currencyPurchaseTarget);
            }
        }


        // 6 - Create instance of CompanyPurchasesPrice
        for (CompanyPurchaseInternal stockPurchase : sortedCompanyPurchases) {
            String companyTicker = stockPurchase.companyTicker;

            CompanyPurchasesPrice companyPurchases = this.companiesPurchases.get(companyTicker);
            if (companyPurchases == null) {
                companyPurchases = new CompanyPurchasesPrice(companyTicker);
                companiesPurchases.put(companyTicker, companyPurchases);
            }

            companyPurchases.addPurchase(stockPurchase);
        }

        // 7 - Create instance of CurrenciesInfo
        currenciesInfo.czkDepositsTotal = totalDepositCZK;

        for (CurrencyStack currencyStack : currencyStacks.values()) {
            currenciesInfo.currencyRemainingAmount.put(currencyStack.currencyTicker, currencyStack.getRemainingTotalCurrencyToAmount());
        }
    }

    // Class, which corresponds overal amount of CZK bought for ALL the purchases of the particular company
    public static class CompanyPurchasesPrice {

        private final String companyTicker;

        private final List<CompanyPurchaseInternal> purchases = new ArrayList<>();

        private CompanyPurchasesPrice(String companyTicker) {
            this.companyTicker = companyTicker;
        }

        public List<CompanyPurchaseInternal> getPurchases() {
            return purchases;
        }

        public void addPurchase(CompanyPurchaseInternal purchase) {
            this.purchases.add(purchase);
        }

        // Get total price of all purchases
        public double getTotalCZKPriceOfAllPurchases() {
            double sum = 0;
            for (CompanyPurchaseInternal purchase : purchases) {
                sum += purchase.getTotalPriceInCZK();
            }
            return sum;
        }
    }

    // Either companmy purchase or currency purchase
    interface PurchaseInternal {

        long getDateNumber();

    }

    public static class CompanyPurchaseInternal implements PurchaseInternal {

        private final String companyTicker;
        private final String currency;

        private final String date;
        private final long dateNumber;
        private final int stocksCount;
        private final double pricePerStock;

        private double totalPriceInCZK;

        private CompanyPurchaseInternal(String companyTicker, String currency, PurchaseRep purchase) {
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

        @Override
        public long getDateNumber() {
            return dateNumber;
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

        private double getRemainingTotalCurrencyToAmount() {
            double result = 0;
            for (CurrencyPurchaseInternal purchase : currencyPurchases) {
                result += purchase.currencyToAmount;
            }
            return result;
        }

    }

    public static class CurrenciesInfo {

        private double czkDepositsTotal;

        // Key is currencyTicker. Value is the remaining amount of particular currency, which we have available
        private Map<String, Double> currencyRemainingAmount = new HashMap<>();

        public double getCzkDepositsTotal() {
            return czkDepositsTotal;
        }

        public Map<String, Double> getCurrencyRemainingAmount() {
            return currencyRemainingAmount;
        }
    }


    private static class CurrencyPurchaseInternal implements PurchaseInternal {

        private final String date;
        private final long dateNumber;

        private final String currencyFrom;

        private final String currencyTo;

        private double currencyFromAmount;

        private double currencyToAmount;

        private double czkAmountForOneToUnit;

        private CurrencyPurchaseInternal(String date, String currencyFrom, double currencyFromAmount, String currencyTo, double currencyToAmount) {
            this.currencyFrom = currencyFrom;
            this.currencyTo = currencyTo;
            this.date = date;
            this.dateNumber = DateUtil.dateToNumber(date);
            this.currencyFromAmount = currencyFromAmount;
            this.currencyToAmount = currencyToAmount;
        }

        @Override
        public long getDateNumber() {
            return dateNumber;
        }

    }
}

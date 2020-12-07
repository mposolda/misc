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
import org.mposolda.reps.DisposalRep;
import org.mposolda.reps.DividendRep;
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
                    int diff3 = o1.companyTicker.compareTo(o2.companyTicker);
                    if (diff3 != 0) {
                        return diff3;
                    } else {
                        throw new IllegalStateException("Two purchases of the same company in same day");
                    }
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

        // 2 - Sort currency purchases. Add real currency purchases and dividends and disposals
        Comparator<CurrencyPurchase> currencyPurchaseComparator = new Comparator<CurrencyPurchase>() {

            @Override
            public int compare(CurrencyPurchase o1, CurrencyPurchase o2) {
                if (o1.equals(o2)) return 0;

                long diff = o1.getDateNumber() - o2.getDateNumber();

                if (diff > 0) {
                    return 1;
                } else if (diff == 0) {
                    int diff2 = o1.getCurrencyTo().compareTo(o2.getCurrencyTo());
                    if (diff2 != 0) {
                        return diff2;
                    } else {
                        int diff3 = o1.getTypePriority() - o2.getTypePriority();
                        if (diff3 != 0) {
                            return diff3;
                        } else {
                            throw new IllegalStateException("Two purchases of the same currency in same day or two dividends in same day or two company disposals at same day. " +
                                    "Day is " + o1.getDate() + ". Currency is " + o1.getCurrencyTo() + ". Type is " + o1.getTypePriority());
                        }
                    }
                } else {
                    return -1;
                }
            }

        };
        Set<CurrencyPurchase> sortedCurrencyPurchases = new TreeSet<>(currencyPurchaseComparator);
        Set<DividendPaymentInternal> sortedDividendPayments = new TreeSet<>(currencyPurchaseComparator);
        Set<StockDisposalInternal> sortedDisposals = new TreeSet<>(currencyPurchaseComparator);

        // 2.1 Add currencies
        for (CurrencyRep currency : database.getCurrencies()) {
            List<CurrencyRep.CurrencyPurchaseRep> purchases = currency.getPurchases();
            for (CurrencyRep.CurrencyPurchaseRep purchase : purchases) {
                double currencyFromAmount = purchase.getCountBought() * purchase.getPricePerUnit();

                CurrencyPurchaseInternal pi = new CurrencyPurchaseInternal(purchase.getDate(), purchase.getCurrencyFrom(), currencyFromAmount,
                        currency.getTicker(), purchase.getCountBought(), purchase.getFeeInCZK());
                sortedCurrencyPurchases.add(pi);
            }
        }

        // 2.2 Add dividends to currencyPurchases and to dividendPayments
        for (CompanyRep company : database.getCompanies()) {

            for (DividendRep dividend : company.getDividends()) {
                DividendPaymentInternal dividendInternal = new DividendPaymentInternal(dividend.getDate(), company.getTicker(),
                        company.getCurrency(), dividend.getTotalAmount(), dividend.getTotalAmountInCZK());
                sortedCurrencyPurchases.add(dividendInternal);
                sortedDividendPayments.add(dividendInternal);
            }
        }

        // 2.3 Add disposals to currencyPurchases
        for (CompanyRep company : database.getCompanies()) {

            for (DisposalRep disposal : company.getDisposals()) {
                StockDisposalInternal disposalInternal = new StockDisposalInternal(disposal.getDate(), company.getTicker(),
                        disposal.getStocksCount(),
                        company.getCurrency(), (disposal.getPricePerStock() * disposal.getStocksCount()) - disposal.getFee(),
                        disposal.getCurrencyPriceToCZKAtTheDisposalTime(), disposal.getFee());
                sortedCurrencyPurchases.add(disposalInternal);
                sortedDisposals.add(disposalInternal);
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
        CurrencyPurchaseInternal czkPurchase = new CurrencyPurchaseInternal(dateOfFirstDeposit, "CZK", 0, "CZK",
                totalDepositCZK, 0);
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

                // Add the fee
                totalPriceOfPurchase += stockPurchase.feeInOriginalCurrency;

                // Now compute the total amount of CZK needed
                CurrencyPurchase currencyPurchase = stack.currencyPurchases.peek();
                double totalPriceOfPurchaseCZK = 0;
                double remainingPriceOfPurchase = totalPriceOfPurchase;

                if (currencyPurchase == null) {
                    throw new IllegalStateException("Not enough money for currency " + stockPurchase.currency + " for buy the company " +
                            stockPurchase.companyTicker + " on date " + stockPurchase.date);
                }
                while (currencyPurchase.getRemainingCurrencyToAmount() < remainingPriceOfPurchase) {
                    // The last currencyPurchase does not have sufficient money for buy this stock.
                    remainingPriceOfPurchase -= currencyPurchase.getRemainingCurrencyToAmount();
                    totalPriceOfPurchaseCZK += currencyPurchase.getRemainingCurrencyToAmount() * currencyPurchase.getCzkAmountForOneUnit();

                    // Try another purchase
                    stack.currencyPurchases.remove();
                    currencyPurchase = stack.currencyPurchases.peek();
                    if (currencyPurchase == null) {
                        throw new IllegalStateException("Not enough money for currency " + stockPurchase.currency + " for buy the company " +
                                stockPurchase.companyTicker + " on date " + stockPurchase.date);
                    }
                }

                currencyPurchase.setRemainingCurrencyToAmount(currencyPurchase.getRemainingCurrencyToAmount() - remainingPriceOfPurchase);
                totalPriceOfPurchaseCZK += remainingPriceOfPurchase * currencyPurchase.getCzkAmountForOneUnit();

                stockPurchase.setTotalPriceInCZK(totalPriceOfPurchaseCZK);

                // Compute the "fee" in CZK
                double feeInCZK = stockPurchase.feeInOriginalCurrency * totalPriceOfPurchaseCZK / totalPriceOfPurchase;
                stockPurchase.setTotalFeeInCZK(feeInCZK);

            } else if (purchase instanceof CurrencyPurchaseInternal) {
                // 5.2 CURRENCY PURCHASE
                CurrencyPurchaseInternal currencyPurchaseTarget = (CurrencyPurchaseInternal) purchase;

                CurrencyStack stackFrom = currencyStacks.get(currencyPurchaseTarget.currencyFrom);
                CurrencyStack stackTo = currencyStacks.get(currencyPurchaseTarget.currencyTo);

                // Total price of purchase in "currency from"
                CurrencyPurchase currencyPurchase = stackFrom.currencyPurchases.peek();
                double totalPriceOfPurchaseCZK = 0;
                double remainingPriceOfPurchase = currencyPurchaseTarget.currencyFromAmount;
                while (currencyPurchase.getRemainingCurrencyToAmount() < remainingPriceOfPurchase) {
                    // The last currencyPurchase does not have sufficient money for buy this stock.
                    remainingPriceOfPurchase -= currencyPurchase.getRemainingCurrencyToAmount();
                    totalPriceOfPurchaseCZK += currencyPurchase.getRemainingCurrencyToAmount() * currencyPurchase.getCzkAmountForOneUnit();

                    // Try another purchase
                    stackFrom.currencyPurchases.remove();
                    currencyPurchase = stackFrom.currencyPurchases.peek();
                }

                currencyPurchase.setRemainingCurrencyToAmount(currencyPurchase.getRemainingCurrencyToAmount() - remainingPriceOfPurchase);
                totalPriceOfPurchaseCZK += remainingPriceOfPurchase * currencyPurchase.getCzkAmountForOneUnit();

                currencyPurchaseTarget.czkAmountForOneToUnit = totalPriceOfPurchaseCZK / currencyPurchaseTarget.currencyToAmount;

                stackTo.addCurrencyPurchase(currencyPurchaseTarget);
            } else if (purchase instanceof DividendPaymentInternal) {
                CurrencyPurchase dividendPurchase = (DividendPaymentInternal) purchase;
                CurrencyStack stackTo = currencyStacks.get(dividendPurchase.getCurrencyTo());

                stackTo.addCurrencyPurchase(dividendPurchase);
            } else if (purchase instanceof StockDisposalInternal) {
                CurrencyPurchase disposalPurchase = (StockDisposalInternal) purchase;
                CurrencyStack stackTo = currencyStacks.get(disposalPurchase.getCurrencyTo());

                stackTo.addCurrencyPurchase(disposalPurchase);
            } else {
                // Should not happen
                throw new IllegalStateException("Invalid purchase: " + purchase);
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

        // 7 - Add dividends payed by companies
        for (CompanyPurchasesPrice company : companiesPurchases.values()) {

            for (DividendPaymentInternal dividend : sortedDividendPayments) {
                if (dividend.companyTicker.equals(company.companyTicker)) {
                    company.addDividendPayment(dividend);
                }
            }
        }

        // 8 - Add disposals payed by solding stock of some companies
        for (CompanyPurchasesPrice company : companiesPurchases.values()) {

            for (StockDisposalInternal disposal : sortedDisposals) {
                if (disposal.companyTicker.equals(company.companyTicker)) {
                    company.addDisposal(disposal);
                }
            }
        }

        // 9 - Create instance of CurrenciesInfo
        currenciesInfo.czkDepositsTotal = totalDepositCZK;

        for (CurrencyStack currencyStack : currencyStacks.values()) {
            currenciesInfo.currencyRemainingAmount.put(currencyStack.currencyTicker, currencyStack.getRemainingTotalCurrencyToAmount());
        }

        double czkFeesForCurrencyPurchasesTotal = 0;
        for (CurrencyRep currencyRep : database.getCurrencies()) {
            for (CurrencyRep.CurrencyPurchaseRep currencyPurchase : currencyRep.getPurchases()) {
                czkFeesForCurrencyPurchasesTotal += currencyPurchase.getFeeInCZK();
            }
        }
        currenciesInfo.czkFeesTotal = czkFeesForCurrencyPurchasesTotal;

        // Update remaining amount of CZK with the fees for currency purchases
        double czkRemaining = currenciesInfo.currencyRemainingAmount.get("CZK");
        czkRemaining -= czkFeesForCurrencyPurchasesTotal;
        currenciesInfo.currencyRemainingAmount.put("CZK", czkRemaining);
    }

    // Class, which corresponds overal amount of CZK bought for ALL the purchases of the particular company
    public static class CompanyPurchasesPrice {

        private final String companyTicker;

        private final List<CompanyPurchaseInternal> purchases = new ArrayList<>();

        private final List<DividendPaymentInternal> dividends = new ArrayList<>();

        private final List<DisposalInternal> disposals = new ArrayList<>();

        private CompanyPurchasesPrice(String companyTicker) {
            this.companyTicker = companyTicker;
        }

        public List<CompanyPurchaseInternal> getPurchases() {
            return purchases;
        }

        public List<DisposalInternal> getDisposals() {
            return new ArrayList<DisposalInternal>(disposals);
        }

        private void addPurchase(CompanyPurchaseInternal purchase) {
            this.purchases.add(purchase);
        }

        private void addDividendPayment(DividendPaymentInternal dividend) {
            this.dividends.add(dividend);
        }

        private void addDisposal(StockDisposalInternal disposal) {
            this.disposals.add(disposal);
        }

        // Get total price of all purchases. It includes fees
        public double getTotalCZKPriceOfAllPurchases() {
            double sum = 0;
            for (CompanyPurchaseInternal purchase : purchases) {
                sum += purchase.getTotalPriceInCZK();
            }
            return sum;
        }

        // Get total price of all fees of all company purchases and also of all disposals
        public double getTotalCZKPriceOfAllFees() {
            double sum = 0;
            for (CompanyPurchaseInternal purchase : purchases) {
                sum += purchase.getTotalFeeInCZK();
            }
            for (DisposalInternal disposal : disposals) {
                sum += disposal.getTotalFeeInCZK();
            }
            return sum;
        }

        /**
         * @return Sum of all moneys in original currency, which were payed to us in dividends
         */
        public double getTotalDividendsPaymentsInOriginalCurrency() {
            double sum = 0;
            for (DividendPaymentInternal dividend : dividends) {
                sum += dividend.currencyToAmount;
            }
            return sum;
        }

        /**
         * @return Sum of all moneys in CZK, which were payed to us in dividends
         */
        public double getTotalDividendsPaymentsInCZK() {
            double sum = 0;
            for (DividendPaymentInternal dividend : dividends) {
                sum += dividend.currencyToAmount * dividend.getCzkAmountForOneUnit();
            }
            return sum;
        }

        /**
         * @return Sum of all moneys in original currency, which were payed to us for disposals of this company
         */
        public double getTotalDisposalsPaymentsInOriginalCurrency() {
            double sum = 0;
            for (DisposalInternal disposal : disposals) {
                sum += disposal.getTotalAmountInOriginalCurrency();
            }
            return sum;
        }

        /**
         * @return Sum of all moneys in CZK, which were payed to us for disposals of this company
         */
        public double getTotalDisposalsPaymentsInCZK() {
            double sum = 0;
            for (DisposalInternal disposal : disposals) {
                sum += disposal.getTotalAmountInCZK();
            }
            return sum;
        }
    }

    // Either company purchase or currency purchase
    interface PurchaseInternal {

        String getDate();

        long getDateNumber();

    }

    public static class CompanyPurchaseInternal implements PurchaseInternal {

        private final String companyTicker;
        private final String currency;

        private final String date;
        private final long dateNumber;
        private final int stocksCount;
        private final double pricePerStock;
        private final double feeInOriginalCurrency;

        private double totalPriceInCZK;

        private double totalFeeInCZK;

        private CompanyPurchaseInternal(String companyTicker, String currency, PurchaseRep purchase) {
            this.companyTicker = companyTicker;
            this.currency = currency;
            this.date = purchase.getDate();
            this.stocksCount = purchase.getStocksCount();
            this.pricePerStock = purchase.getPricePerStock();
            this.feeInOriginalCurrency = purchase.getFee();

            // Compute date
            this.dateNumber = DateUtil.dateToNumber(date);
        }

        private void setTotalPriceInCZK(double totalPriceInCZK) {
            this.totalPriceInCZK = totalPriceInCZK;
        }

        // Get total price of this purchase in CZK including fees
        public double getTotalPriceInCZK() {
            return this.totalPriceInCZK;
        }

        private void setTotalFeeInCZK(double totalFeeInCZK) {
            this.totalFeeInCZK = totalFeeInCZK;
        }

        // Get price of this purchase in CZK including fees
        public double getTotalFeeInCZK() {
            return totalFeeInCZK;
        }

        public double getFeeInOriginalCurrency() {
            return feeInOriginalCurrency;
        }

        public int getStocksCount() {
            return stocksCount;
        }

        public double getPricePerStock() {
            return pricePerStock;
        }

        @Override
        public String getDate() {
            return date;
        }

        @Override
        public long getDateNumber() {
            return dateNumber;
        }
    }


    private static class CurrencyStack {

        private final String currencyTicker;

        private final Queue<CurrencyPurchase> currencyPurchases = new LinkedList<>();

        public CurrencyStack(String currencyTicker) {
            this.currencyTicker = currencyTicker;
        }

        private void addCurrencyPurchase(CurrencyPurchase currencyPurchase) {
            currencyPurchases.add(currencyPurchase);
        }

        private double getRemainingTotalCurrencyToAmount() {
            double result = 0;
            for (CurrencyPurchase purchase : currencyPurchases) {
                result += purchase.getRemainingCurrencyToAmount();
            }
            return result;
        }

    }

    public static class CurrenciesInfo {

        private double czkDepositsTotal;

        private double czkFeesTotal;

        // Key is currencyTicker. Value is the remaining amount of particular currency, which we have available
        private Map<String, Double> currencyRemainingAmount = new HashMap<>();

        public double getCzkDepositsTotal() {
            return czkDepositsTotal;
        }

        public double getCzkFeesTotal() {
            return czkFeesTotal;
        }

        public Map<String, Double> getCurrencyRemainingAmount() {
            return currencyRemainingAmount;
        }
    }


    // Represents everything, which gives us money of specified currency. It can be CurrencyPurchase or DividendPayment or StockDisposal
    private interface CurrencyPurchase extends PurchaseInternal {

        double getRemainingCurrencyToAmount();

        // During computation and buying stocks and currencies, we need to set new currencyToAmount as we used some "currencyToAmount" for buying stocks or other currencies
        void setRemainingCurrencyToAmount(double currencyToAmount);

        double getCzkAmountForOneUnit();

        String getCurrencyTo();

        // Just a helper for sorting to ensure that dividends are after currency purchases and stockDisposals are after dividends.
        int getTypePriority();
    }

    // Add currency purchases before dividends. But in reality, the order does not matter too much
    private static final int CURRENCY_PURCHASE_TYPE_PRIORITY = 1;
    private static final int DIVIDENT_PAYMENT_TYPE_PRIORITY = 2;
    private static final int STOCK_DISPOSAL_PRIORITY = 3;

    private static class CurrencyPurchaseInternal implements CurrencyPurchase {

        private final String date;
        private final long dateNumber;

        private final String currencyFrom;

        private final String currencyTo;

        private double currencyFromAmount;

        private final double currencyToAmount;

        private double remainingCurrencyToAmount;

        private double czkAmountForOneToUnit;

        private double feeInCZK;

        private CurrencyPurchaseInternal(String date, String currencyFrom, double currencyFromAmount, String currencyTo,
                                         double currencyToAmount, double feeInCZK) {
            this.currencyFrom = currencyFrom;
            this.currencyTo = currencyTo;
            this.date = date;
            this.dateNumber = DateUtil.dateToNumber(date);
            this.currencyFromAmount = currencyFromAmount;
            this.currencyToAmount = currencyToAmount;
            this.remainingCurrencyToAmount = currencyToAmount;
            this.feeInCZK = feeInCZK;
        }

        @Override
        public String getDate() {
            return date;
        }

        @Override
        public long getDateNumber() {
            return dateNumber;
        }

        @Override
        public double getRemainingCurrencyToAmount() {
            return remainingCurrencyToAmount;
        }

        @Override
        public void setRemainingCurrencyToAmount(double remainingCurrencyToAmount) {
            this.remainingCurrencyToAmount = remainingCurrencyToAmount;
        }

        @Override
        public double getCzkAmountForOneUnit() {
            return czkAmountForOneToUnit;
        }

        @Override
        public String getCurrencyTo() {
            return currencyTo;
        }

        @Override
        public int getTypePriority() {
            return 1;
        }
    }


    // Divident payment gives us money and hence it is considered as currencyPurchase
    private static class DividendPaymentInternal implements CurrencyPurchase {

        private final String date;

        private final long dateNumber;

        private final String currencyTo;

        private final String companyTicker;

        private final double currencyToAmount;

        private double remainingCurrencyToAmount;

        private final double currencyToAmountInCZK;

        private final double czkAmountForOneUnit;

        public DividendPaymentInternal(String date, String companyTicker, String currencyTo, double currencyToAmount, double currencyToAmountInCZK) {
            this.date = date;
            this.dateNumber = DateUtil.dateToNumber(date);
            this.companyTicker = companyTicker;
            this.currencyTo = currencyTo;
            this.currencyToAmount = currencyToAmount;
            this.remainingCurrencyToAmount = currencyToAmount;
            this.currencyToAmountInCZK = currencyToAmountInCZK;
            this.czkAmountForOneUnit = currencyToAmountInCZK / currencyToAmount;
        }

        @Override
        public String getDate() {
            return date;
        }

        @Override
        public long getDateNumber() {
            return dateNumber;
        }

        @Override
        public double getRemainingCurrencyToAmount() {
            return remainingCurrencyToAmount;
        }

        @Override
        public void setRemainingCurrencyToAmount(double remainingCurrencyToAmount) {
            this.remainingCurrencyToAmount = remainingCurrencyToAmount;
        }

        @Override
        public double getCzkAmountForOneUnit() {
            return czkAmountForOneUnit;
        }

        @Override
        public String getCurrencyTo() {
            return currencyTo;
        }

        @Override
        public int getTypePriority() {
            return DIVIDENT_PAYMENT_TYPE_PRIORITY;
        }
    }

    // Public interface, which allows some public parts of "Disposal" to be accessed
    public interface DisposalInternal {

        String getDate();

        String getCompanyTicker();

        String getCurrency();

        int getSoldStocksCount();

        double getTotalAmountInOriginalCurrency();

        double getTotalAmountInCZK();

        double getTotalFeeInOriginalCurrency();

        double getTotalFeeInCZK();

    }


    // Disposal of some stock payment gives us money and hence it is considered as currencyPurchase
    private static class StockDisposalInternal implements CurrencyPurchase, DisposalInternal {

        private final String date;

        private final long dateNumber;

        private final String currencyTo;

        private final String companyTicker;

        private final int soldStocksCount;

        private final double currencyToAmount;

        private double remainingCurrencyToAmount;

        private final double currencyToAmountInCZK;

        private final double czkAmountForOneUnitOfOrigCurrency;

        private final double totalFeeInOriginalCurrency;

        private final double totalFeeInCZK;

        private StockDisposalInternal(String date, String companyTicker, int soldStocksCount, String currencyTo, double currencyToAmount,
                                     double czkAmountForOneUnitOfOrigCurrency, double totalFeeInOriginalCurrency) {
            this.date = date;
            this.dateNumber = DateUtil.dateToNumber(date);
            this.companyTicker = companyTicker;
            this.soldStocksCount = soldStocksCount;
            this.currencyTo = currencyTo;
            this.currencyToAmount = currencyToAmount;
            // The gain from this CurrencyPurchase is total amount (The fee is clready included in that)
            this.remainingCurrencyToAmount = currencyToAmount;
            this.currencyToAmountInCZK = czkAmountForOneUnitOfOrigCurrency * currencyToAmount;
            this.czkAmountForOneUnitOfOrigCurrency = czkAmountForOneUnitOfOrigCurrency;
            this.totalFeeInOriginalCurrency = totalFeeInOriginalCurrency;
            this.totalFeeInCZK = totalFeeInOriginalCurrency * czkAmountForOneUnitOfOrigCurrency;
        }

        @Override
        public String getDate() {
            return date;
        }

        @Override
        public long getDateNumber() {
            return dateNumber;
        }

        @Override
        public double getRemainingCurrencyToAmount() {
            return remainingCurrencyToAmount;
        }

        @Override
        public void setRemainingCurrencyToAmount(double remainingCurrencyToAmount) {
            this.remainingCurrencyToAmount = remainingCurrencyToAmount;
        }

        @Override
        public double getCzkAmountForOneUnit() {
            return czkAmountForOneUnitOfOrigCurrency;
        }

        @Override
        public String getCurrencyTo() {
            return currencyTo;
        }

        @Override
        public int getTypePriority() {
            return DIVIDENT_PAYMENT_TYPE_PRIORITY;
        }

        public double getTotalFeeInCZK() {
            return totalFeeInCZK;
        }

        public double getTotalAmountInCZK() {
            return currencyToAmountInCZK;
        }

        // DISPOSAL INTERNAL METHODS


        @Override
        public String getCompanyTicker() {
            return companyTicker;
        }

        @Override
        public String getCurrency() {
            return currencyTo;
        }

        @Override
        public int getSoldStocksCount() {
            return soldStocksCount;
        }

        @Override
        public double getTotalAmountInOriginalCurrency() {
            return currencyToAmount;
        }

        @Override
        public double getTotalFeeInOriginalCurrency() {
            return totalFeeInOriginalCurrency;
        }
    }
}

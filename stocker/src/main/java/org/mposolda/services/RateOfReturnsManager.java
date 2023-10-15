package org.mposolda.services;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import org.mposolda.reps.CurrencyRep;
import org.mposolda.reps.DatabaseRep;
import org.mposolda.reps.RateOfReturnsRep;
import org.mposolda.reps.rest.CompaniesRep;
import org.mposolda.reps.rest.CurrenciesRep;
import org.mposolda.util.DateUtil;
import org.mposolda.util.JsonUtil;
import org.mposolda.util.returner.RateOfReturnInput;
import org.mposolda.util.returner.RateOfReturnUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class RateOfReturnsManager {

    protected static final Logger log = Logger.getLogger(RateOfReturnsManager.class);

    private final String  companiesJsonFileLocation;
    private RateOfReturnsRep rateOfReturnsRep;

    public RateOfReturnsManager() {
        this.companiesJsonFileLocation = Services.instance().getConfig().getCompaniesJsonFileLocation();
    }

    public void start() {
        rateOfReturnsRep = new RateOfReturnsRep();

        // Load company informations from JSON file
        DatabaseRep database = JsonUtil.loadDatabase(this.companiesJsonFileLocation);
        log.infof("Database loaded");

        // Find current total value of companies and currencies in CZK
        CompaniesRep companies = Services.instance().getCompanyInfoManager().getCompanies();
        CurrenciesRep currencies = Services.instance().getCompanyInfoManager().getCurrencies();
        double totalValue = companies.getCurrentPriceOfAllStocksInHoldCZK() + currencies.getPriceInHoldCZK();

        // Find all CZK deposits
        CurrencyRep czkCurrency = database.getCurrencies().stream()
                .filter(currencyRep -> "CZK".equals(currencyRep.getTicker()))
                .findFirst().orElseThrow();
        List<RateOfReturnInput.Deposit> deposits = czkCurrency.getDeposits().stream()
                .map(deposit -> {
                    return new RateOfReturnInput.Deposit(DateUtil.dateToNumber(deposit.getDate()), deposit.getCountBought());
                }).toList();

        // Compute rateOfReturn per year
        RateOfReturnInput input = new RateOfReturnInput(System.currentTimeMillis(), totalValue, deposits.toArray(new RateOfReturnInput.Deposit[0]));
        double rateOfReturnInPercent = RateOfReturnUtil.computeYearRateOfReturn(input);
        rateOfReturnsRep.setRateOfReturnPerYear(rateOfReturnInPercent);

        // Compute absolute rateOfReturn
        double absoluteRateOfReturnInPercent = ((totalValue / currencies.getDepositTotalInCZK()) - 1) * 100;
        rateOfReturnsRep.setRateOfReturnAbsolute(absoluteRateOfReturnInPercent);

        // TODO:mposolda S&P 500 and others...
    }

    public RateOfReturnsRep getRateOfReturnsRep() {
        if (rateOfReturnsRep == null) {
            throw new IllegalStateException("RateOfReturnsManager not started");
        }
        return rateOfReturnsRep;
    }
}

package org.mposolda.cli;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.mposolda.reps.CandlesRep;
import org.mposolda.reps.CompanyRep;
import org.mposolda.reps.CurrencyRep;
import org.mposolda.reps.DatabaseRep;
import org.mposolda.services.Services;
import org.mposolda.util.JsonUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class AllCandlesDownloadCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "allCandlesDownload";
    }

    @Override
    protected void doRunCommand() throws IOException {
        String companyJsonFileLocation = Services.instance().getCompaniesJsonFileLocation();
        DatabaseRep database = JsonUtil.loadDatabase(companyJsonFileLocation);

        for (CurrencyRep currency : database.getCurrencies()) {
            // Skip "EUR"
            if (currency.getTicker().equals("EUR")) continue;

            CandlesRep currencyCandle = services.getCandlesHistoryManager().getCurrencyCandles(currency.getTicker(), true);
            log.info("Loaded candles representation for currency " + currency.getTicker());
        }

        List<String> failedCompanies = new LinkedList<>();
        for (CompanyRep company : database.getCompanies()) {
            try {
                CandlesRep stockCandle = services.getCandlesHistoryManager().getStockCandles(company.getTicker(), true);
                log.info("Loaded candles representation for company " + company.getTicker());
            } catch (Exception e) {
                log.warn("Failed to download candles representation for company " + company.getTicker());
                failedCompanies.add(company.getTicker());
            }
        }

        log.infof("All failed company tickers " + failedCompanies);
    }


}

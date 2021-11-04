package org.mposolda.cli;

import java.io.IOException;
import java.util.Collections;

import org.mposolda.reps.finhub.CandleRep;
import org.mposolda.reps.finhub.CurrencyCandlesRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CurrencyCandleCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "currencyCandle";
    }

    @Override
    protected void doRunCommand() throws IOException {
        String currencyTicker = getArg(0);
        String startDate = getArg(1);
        String endDate = getArg(2);

        CurrencyCandlesRep currencyCandle = services.getFinhubClient().getCurrencyCandles(Collections.singletonList(currencyTicker), startDate, endDate);

        log.info("Info: " + currencyCandle);
    }

    @Override
    public String printUsage() {
        return getName() + " <target-currency-ticker> <start-date yyyy-mm-dd> <end-date yyy-mm-dd>";
    }
}

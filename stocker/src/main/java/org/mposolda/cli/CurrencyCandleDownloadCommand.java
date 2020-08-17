package org.mposolda.cli;

import java.io.IOException;

import org.mposolda.reps.CandlesRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CurrencyCandleDownloadCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "currencyCandleDownload";
    }

    @Override
    protected void doRunCommand() throws IOException {
        String currencyTicker = getArg(0);

        CandlesRep stockCandle = services.getCandlesHistoryManager().getCurrencyCandles(currencyTicker, true);

        log.info("Downloaded candles representation from EUR to " + currencyTicker);
    }

    @Override
    public String printUsage() {
        return getName() + " <target-currency-ticker>";
    }
}

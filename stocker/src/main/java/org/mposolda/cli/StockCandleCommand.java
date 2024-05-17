package org.mposolda.cli;

import java.io.IOException;

import org.mposolda.reps.QuoteLoaderRep;
import org.mposolda.reps.finhub.CandleRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StockCandleCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "stockCandle";
    }

    @Override
    protected void doRunCommand() throws IOException {
        String ticker = getArg(0);
        String startDate = getArg(1);
        String endDate = getArg(2);

        QuoteLoaderRep quoteLoader = QuoteLoaderRep.fromTicker(ticker);
        CandleRep stockCandle = services.getStockerHttpClient().getStockCandle(quoteLoader, startDate, endDate);

        log.info("Info: " + stockCandle);
    }

    @Override
    public String printUsage() {
        return getName() + " <ticker> <start-date yyyy-mm-dd> <end-date yyy-mm-dd>";
    }
}

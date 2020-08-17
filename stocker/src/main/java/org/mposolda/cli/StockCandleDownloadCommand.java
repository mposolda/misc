package org.mposolda.cli;

import java.io.IOException;

import org.mposolda.reps.CandlesRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StockCandleDownloadCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "stockCandleDownload";
    }

    @Override
    protected void doRunCommand() throws IOException {
        String stockTicker = getArg(0);

        CandlesRep stockCandle = services.getCandlesHistoryManager().getStockCandles(stockTicker, true);

        log.info("Downloaded candles representation " + stockTicker);
    }

    @Override
    public String printUsage() {
        return getName() + " <target-stock-ticker>";
    }
}

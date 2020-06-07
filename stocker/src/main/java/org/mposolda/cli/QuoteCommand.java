package org.mposolda.cli;

import java.io.IOException;

import org.mposolda.reps.finhub.QuoteRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class QuoteCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "quote";
    }

    @Override
    protected void doRunCommand() throws IOException {
        String ticker = getArg(0);

        QuoteRep quote = services.getFinhubClient().getQuoteRep(ticker);

        log.info("Info: " + quote);
    }

    @Override
    public String printUsage() {
        return getName() + " <ticker>";
    }
}

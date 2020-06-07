package org.mposolda.cli;

import java.io.IOException;

import org.mposolda.util.NumberFormatUtil;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CurrencyConvertCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "currencyConvert";
    }

    @Override
    protected void doRunCommand() throws IOException {
        double currencyFromAmount = getDoubleArg(0);
        String currencyFrom = getArg(1);
        String currencyTo = getArg(2);

        double currencyToAmount = services.getCurrencyConvertor().exchangeMoney(currencyFromAmount, currencyFrom, currencyTo);

        log.infof("%s %s = %s %s", NumberFormatUtil.format(currencyFromAmount), currencyFrom,
                NumberFormatUtil.format(currencyToAmount), currencyTo);
    }

    @Override
    public String printUsage() {
        return getName() + " <currencyFromAmount> <currencyFrom> <currencyTo>";
    }

}

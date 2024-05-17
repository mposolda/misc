package org.mposolda.cli;

import java.io.IOException;

import org.mposolda.reps.finhub.CompanyProfileRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CompanyProfileCommand extends AbstractCommand {

    @Override
    public String getName() {
        return "companyProfile";
    }

    @Override
    protected void doRunCommand() throws IOException {
        String ticker = getArg(0);

        CompanyProfileRep company = services.getStockerHttpClient().getCompanyProfile(ticker);

        log.info("Info: " + company);
    }

    @Override
    public String printUsage() {
        return getName() + " <ticker>";
    }
}

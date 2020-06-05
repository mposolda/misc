package org.mposolda.cli;

import java.io.IOException;

import org.mposolda.cli.AbstractCommand;
import org.mposolda.reps.rest.CompanyProfileRep;

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

        CompanyProfileRep company = services.getFinhubClient().getCompanyProfile(ticker);

        log.info("Info: " + company);
    }

    @Override
    public String printUsage() {
        return getName() + " <ticker>";
    }
}

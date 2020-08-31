package org.mposolda.cli;

import java.io.IOException;

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
        services.getCandlesHistoryManager().allCandlesDownload();
    }


}

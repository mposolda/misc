package org.jboss.sample;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class BackupCacheInitiator extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.err.println("BackupCacheInitiator: Initializing");
        super.init();
    }
}

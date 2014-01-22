package org.gatein.example.cdi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class GenericCDIPortlet extends GenericPortlet {
    private static final Logger log = Logger.getLogger(GenericCDIPortlet.class.getName());

    /**
     * Java EE Container injects a Request Scoped {@link DataBean} for us here.
     */
    @Inject
    public DataBean bean;

    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PrintWriter writer = response.getWriter();
        writer.write("<div style=\"margin: 10px;\">\n");
        writer.write("<h1 style=\"font-size:16px;background-color:#dedfdf;padding:2px 4px;margin-bottom:2px;margin-bottom:2px;\">CDI Generic Portlet</h1>\n");
        writer.write("<p style=\"margin-left: 4px;\">Message from DataBean is: " + bean.getMessage() +", Counter is: " + bean.getCounter() + "</p>\n");
        writer.write("</div>\n");
        log.info("doView() finished");

        bean.setCounter(bean.getCounter() + 1);
    }
}

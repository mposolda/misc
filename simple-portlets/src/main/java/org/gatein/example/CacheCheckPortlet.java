package org.gatein.example;

import java.io.IOException;
import java.util.Date;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CacheCheckPortlet extends GenericPortlet {

    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        response.getWriter().println("VIEW MODE: " + new Date());
    }

    @Override
    protected void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        response.getWriter().println("EDIT MODE");
    }

    @Override
    protected void doHelp(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        response.getWriter().println("HELP MODE");
    }
}

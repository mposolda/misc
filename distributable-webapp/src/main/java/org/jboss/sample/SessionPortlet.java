package org.jboss.sample;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SessionPortlet extends GenericPortlet {

    protected void doView(RenderRequest req, RenderResponse resp) throws PortletException, IOException {
        String some = (String)req.getPortletSession().getAttribute("some");
        if (some == null) {
            some = "12345679";
        }

        int increased = Integer.parseInt(some) + 1;
        some = String.valueOf(increased);
        req.getPortletSession().setAttribute("some", some);

        resp.getWriter().println("SessionPortlet triggered: " + some);
    }
}

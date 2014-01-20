package org.gatein.example;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SimpleSessionPortlet extends GenericPortlet {

    @Override
    public void processAction(ActionRequest req, ActionResponse resp) {
        PortletSession session = req.getPortletSession(true);

        Integer counter = (Integer)session.getAttribute("counter");
        if (counter == null) {
            counter = 0;
        }
        counter++;

        session.setAttribute("counter", counter);
    }

    @Override
    protected void doView(RenderRequest req, RenderResponse resp) throws IOException {
        PortletSession session = req.getPortletSession(true);

        Integer counter = (Integer)session.getAttribute("counter");
        if (counter == null) {
            counter = 0;
            session.setAttribute("counter", counter);
        }

        PrintWriter writer = resp.getWriter();
        writer.println("Current counter: " + counter + "<br>");
        writer.println("<a href='" + resp.createActionURL() + "'>Increase counter</a>");
        writer.close();
    }
}

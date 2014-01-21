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

        increaseAndSet(session, PortletSession.PORTLET_SCOPE, "counter");
        increaseAndSet(session, PortletSession.APPLICATION_SCOPE, "appCounter");
    }

    @Override
    protected void doView(RenderRequest req, RenderResponse resp) throws IOException {
        PortletSession session = req.getPortletSession(true);

        int counter = getOrInit(session, PortletSession.PORTLET_SCOPE, "counter");
        int appCounter = getOrInit(session, PortletSession.APPLICATION_SCOPE, "appCounter");

        PrintWriter writer = resp.getWriter();
        writer.println("Current counter: " + counter + "<br>");
        writer.println("Current app-scoped counter: " + appCounter + "<br>");
        writer.println("<a href='" + resp.createActionURL() + "'>Increase counter</a>");
        writer.close();
    }

    private void increaseAndSet(PortletSession session, int scope, String attrName) {
        Integer counter = (Integer)session.getAttribute(attrName, scope);
        if (counter == null) {
            counter = 0;
        }
        counter++;
        session.setAttribute(attrName, counter, scope);
    }

    private int getOrInit(PortletSession session, int scope, String attrName) {
        Integer counter = (Integer)session.getAttribute(attrName, scope);
        if (counter == null) {
            counter = 0;
            session.setAttribute(attrName, counter, scope);
        }
        return counter;
    }
}

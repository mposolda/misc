package org.gatein.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class PreferencesPortlet extends GenericPortlet {

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        int currentCounter = Integer.parseInt(request.getPreferences().getValue("counter", "-10"));
        request.getPreferences().setValue("counter", String.valueOf(++currentCounter));
        request.getPreferences().store();
    }

    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PrintWriter writer = response.getWriter();
        Map<String, String[]> prefs = request.getPreferences().getMap();
        for (String prefKey : prefs.keySet()) {
            writer.println("Preference key: " + prefKey + ", Preference value: " + Arrays.asList(prefs.get(prefKey)) + "<br>");
        }

        writer.println("<a href='" + response.createActionURL() + "'>Increase counter preference</a><br>");
        writer.close();
    }
}

package org.jboss.sample;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SessionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String some = (String)req.getSession().getAttribute("some");
        if (some == null) {
            some = "12345679";
        }

        int increased = Integer.parseInt(some) + 1;
        some = String.valueOf(increased);
        req.getSession().setAttribute("some", some);

        resp.getWriter().println("SessionServlet triggered: " + some);
    }
}

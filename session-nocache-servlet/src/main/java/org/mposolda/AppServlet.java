package org.mposolda;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet, which just redirect to the app.
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class AppServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirectUri = req.getScheme() + "://localhost:" + req.getLocalPort() + "/session-nocache-servlet/s?param1=foo&param2=bar";
        System.out.println("AppServlet: Redirecting to " + redirectUri);
        resp.sendRedirect(redirectUri);
    }

}

package org.mposolda;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SpnegoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().endsWith("/logout")) {
            HttpSession session = req.getSession();
            session.removeAttribute("principal");
            session.removeAttribute("auth");
            resp.sendRedirect("");
        } else {
            HttpSession session = req.getSession();
            String principal = (String) session.getAttribute("principal");
            String authType = (String) session.getAttribute("auth");

            resp.setContentType("text/html");
            PrintWriter writer = resp.getWriter();
            writer.println("I am authenticated!<br>");

            writer.println("Username: " + principal + "<br>");
            writer.println("Auth type: " + authType + "<br>");
            writer.println("<a href='" + req.getRequestURI() + "/logout'>Logout</a><br>");
            writer.flush();
            writer.close();
        }
    }
}

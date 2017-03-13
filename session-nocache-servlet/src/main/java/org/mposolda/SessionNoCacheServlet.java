package org.mposolda;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SessionNoCacheServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("POST received!");

        String username = req.getParameter("username");
        System.out.println("username from parameter: " + username);

        HttpSession session = req.getSession();
        session.setAttribute("username", username);

        //render(req, resp);
        resp.sendRedirect(req.getRequestURL().toString());
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("GET received!");
        render(req, resp);
    }


    private void render(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        System.out.println("SESSIONID: " + session.getId());

        String username = (String) session.getAttribute("username");
        String actionURI = req.getRequestURL().toString();

        resp.setHeader("Content-Type", "text/html");
        String responseString = renderForm(actionURI, username);
        resp.getWriter().println(responseString);
        resp.getWriter().flush();
    }


    private String renderForm(String actionURI, String username) {
        StringBuilder builder = new StringBuilder();
        builder.append("<HTML><HEAD><TITLE>Hello</TITLE></HEAD><BODY>");
        builder.append("Current username: " +username + "<br><hr>");
        builder.append("<FORM action='" + actionURI + "' method='POST'>");
        builder.append("Username: <INPUT name='username' value='" + username + "' />");
        builder.append("</FORM>");

        builder.append("</BODY></HTML>");
        return builder.toString();
    }

}

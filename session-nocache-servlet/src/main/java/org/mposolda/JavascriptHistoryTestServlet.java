package org.mposolda;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JavascriptHistoryTestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("JS: POST received!");

        String username = req.getParameter("username");
        System.out.println("username from parameter: " + username);

        HttpSession session = req.getSession();
        session.setAttribute("username", username);

        render(req, resp, true);
        //resp.sendRedirect(req.getRequestURL().toString());
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("JS: GET received!");
        System.out.println("User-Agent: " + req.getHeader("User-Agent"));
        render(req, resp, false);
    }


    private void render(HttpServletRequest req, HttpServletResponse resp, boolean pushState) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String username = (String) session.getAttribute("username");
        System.out.println("SESSIONID: " + session.getId() + ", username: " + username);
        String actionURI = req.getScheme() + "://" + req.getLocalAddr() + ":" + req.getLocalPort() + "/session-nocache-servlet/j";

//        Integer newCode = computeNewCode(session);
//        actionURI = actionURI + "?code=" + newCode;

        resp.setHeader("Content-Type", "text/html");

        // no back-button
        resp.setHeader("Cache-Control", "no-store, must-revalidate, max-age=0");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Expires", "0");

        String responseString = renderForm(actionURI, username, pushState);
        resp.getWriter().println(responseString);
        resp.getWriter().flush();
    }

    private String renderForm(String actionURI, String username, boolean pushState) {
        StringBuilder builder = new StringBuilder();
        builder.append("<HTML><HEAD><TITLE>Hello</TITLE>");

        if (pushState) {
            builder.append("<SCRIPT>" + getJavascriptText() + "</SCRIPT>");
        }

        builder.append("</HEAD><BODY><SCRIPT>document.write(typeof history.replaceState);  document.write(\" NEXT: \"); document.write(typeof history.brekeke);</SCRIPT>");
        builder.append("Current username: " + username + "<br><hr>");
        builder.append("<FORM action='" + actionURI + "' method='POST'>");
        builder.append("Username: <INPUT id='username' name='username' value='" + username + "' />");
        builder.append("Username: <INPUT id='submitme' type='submit' value='submit' />");
        builder.append("</FORM>");

        builder.append("</BODY></HTML>");
        return builder.toString();
    }

    private String getJavascriptText() {
        int number = new Random().nextInt();
        return new StringBuilder()
                .append("if (typeof history.replaceState === 'function') {")
                        //.append("history.replaceState({}, \"page " + number + "\", \"j?foo=" + number + "\");")
                .append("history.replaceState({}, \"execution 123\", \"j?execution=123\");")
                        .append(" }")
                .toString();
    }
}

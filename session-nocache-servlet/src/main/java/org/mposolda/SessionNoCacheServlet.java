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

    private enum CodeCheck {
        VALID, INVALID, EXPIRED
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("POST received!");

        String queryString = req.getQueryString();
        CodeCheck codeCheck = checkCode(queryString, req.getSession());

        // Restart flow from scratch
        if (codeCheck == CodeCheck.INVALID) {
            System.out.println("Code invalid. Restarting from Scratch!");
            req.getSession().invalidate();
            String appUri = req.getScheme() + "://localhost:" + req.getLocalPort() + "/session-nocache-servlet/app";
            resp.sendRedirect(appUri);
            return;
        } else if (codeCheck == CodeCheck.EXPIRED) {
            System.out.println("Code expired. Just redirecting to get");
            String getUri = req.getScheme() + "://localhost:" + req.getLocalPort() + "/session-nocache-servlet/s";
            resp.sendRedirect(getUri);
            return;
        }

        String username = req.getParameter("username");
        System.out.println("username from parameter: " + username);

        HttpSession session = req.getSession();
        session.setAttribute("username", username);

        render(req, resp);
        //resp.sendRedirect(req.getRequestURL().toString());
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("GET received!");
        computeNewCode(req.getSession());
        render(req, resp);
    }


    private CodeCheck checkCode(String queryString, HttpSession session) {
        Integer sessionCode = getCurrentCode(session);

        if (queryString == null) {
            return CodeCheck.INVALID;
        }

        int index =  (queryString.indexOf('='));
        if (index == -1) {
            return CodeCheck.INVALID;
        }

        Integer paramCode = Integer.parseInt(queryString.substring(index + 1));
        System.out.println("sessionCode=" + sessionCode + ", paramCode=" + paramCode);
        if (sessionCode == paramCode) {
            return CodeCheck.VALID;
        } else if (sessionCode > paramCode) {
            return CodeCheck.EXPIRED;
        } else {
            return CodeCheck.INVALID;
        }
    }


    private void render(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        System.out.println("SESSIONID: " + session.getId());

        String username = (String) session.getAttribute("username");
        String actionURI = req.getRequestURL().toString();

        Integer newCode = computeNewCode(session);
        actionURI = actionURI + "?code=" + newCode;

        resp.setHeader("Content-Type", "text/html");

        // no back-button
        //resp.setHeader("Cache-Control", "no-store, must-revalidate, max-age=0");

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


    private Integer computeNewCode(HttpSession session) {
        Integer code = (Integer) session.getAttribute("code");
        code = code==null ? 1 : code+1;
        session.setAttribute("code", code);
        return code;
    }

    private Integer getCurrentCode(HttpSession session) {
        return (Integer) session.getAttribute("code");
    }

}

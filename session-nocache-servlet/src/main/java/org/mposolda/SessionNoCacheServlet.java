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
            String appUri = req.getScheme() + "://" + req.getLocalAddr() + ":" + req.getLocalPort() + "/session-nocache-servlet/app";
            resp.sendRedirect(appUri);
            return;
        } else if (codeCheck == CodeCheck.EXPIRED) {
            System.out.println("Code expired. Just redirecting to get");
            String getUri = req.getScheme() + "://" + req.getLocalAddr() + ":" + req.getLocalPort() + "/session-nocache-servlet/s?expired=true";
            resp.sendRedirect(getUri);
            return;
        }

        String username = req.getParameter("username");
        System.out.println("username from parameter: " + username);

        HttpSession session = req.getSession();
        session.setAttribute("username", username);

        render(req, resp, false);
        //resp.sendRedirect(req.getRequestURL().toString());
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean expired = req.getParameter("expired") != null;
        System.out.println("GET received! expired=" + expired);
        computeNewCode(req.getSession());
        render(req, resp, expired);
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


    private void render(HttpServletRequest req, HttpServletResponse resp, boolean expired) throws ServletException, IOException {
        HttpSession session = req.getSession();
        System.out.println("SESSIONID: " + session.getId());

        String username = (String) session.getAttribute("username");
        String actionURI = req.getScheme() + "://" + req.getLocalAddr() + ":" + req.getLocalPort() + "/session-nocache-servlet/s";

        Integer newCode = computeNewCode(session);
        actionURI = actionURI + "?code=" + newCode;

        resp.setHeader("Content-Type", "text/html");

        // no back-button
        resp.setHeader("Cache-Control", "no-store, must-revalidate, max-age=0");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Expires", "0");

        String responseString = renderForm(actionURI, username, expired);
        resp.getWriter().println(responseString);
        resp.getWriter().flush();
    }


    private String renderForm(String actionURI, String username, boolean expired) {
        StringBuilder builder = new StringBuilder();
        builder.append("<HTML><HEAD><TITLE>Hello</TITLE></HEAD><BODY>");
        if (expired) {
            builder.append("<FONT color='red'>Expired page. Submitted form was ignored</FONT><BR>");
        }
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

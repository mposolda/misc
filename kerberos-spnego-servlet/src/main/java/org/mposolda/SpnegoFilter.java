package org.mposolda;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SpnegoFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();

        if (session.getAttribute("principal") == null) {

            // Try if there is username/password login
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            boolean loginFormFailed = false;
            if (username != null && password != null) {
                boolean formLoginSuccess = formLogin(username, password, session);
                if (formLoginSuccess) {
                    resp.sendRedirect("");
                    return;
                } else {
                    loginFormFailed = true;
                }
            }

            String authHeader = req.getHeader("Authorization");
            if (authHeader == null) {
                sendResponse(req, resp, "Negotiate", loginFormFailed);
                return;
            } else {
                String[] tokens = authHeader.split(" ");
                if (tokens.length != 2) {
                    System.err.println("Invalid length of tokens: " + tokens.length);
                    sendResponse(req, resp, "Negotiate", false);
                } else if (!"Negotiate".equalsIgnoreCase(tokens[0])) {
                    System.err.println("Invalid scheme " + tokens[0]);
                    sendResponse(req, resp, "Negotiate", false);
                } else {
                    boolean spnegoLoginSuccess = spnegoLogin(tokens[1], session, req, resp);
                    if (spnegoLoginSuccess) {
                        resp.sendRedirect("");
                    }
                }
            }
        } else {
            // I am already authenticated
            chain.doFilter(request, response);
        }
    }

    private boolean formLogin(String username, String password, HttpSession session) throws IOException, ServletException {
        if ("admin".equals(username) && "password".equals(password)) {
            session.setAttribute("principal", "admin");
            session.setAttribute("auth", "form");
            return true;
        }  else {
            return false;
        }
    }

    private void sendResponse(HttpServletRequest req, HttpServletResponse resp, String negotiateHeader, boolean loginFormFailed) throws IOException {
        // In case of previous failure of FORM login, we won't send SPNEGO headers anymore!!!
        if (!loginFormFailed) {
            resp.setHeader("WWW-Authenticate", negotiateHeader);
            resp.setStatus(401);
        }

        renderLoginForm(req, resp, loginFormFailed);
    }

    private void renderLoginForm(HttpServletRequest req, HttpServletResponse resp, boolean loginFormFailed) throws IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        if (loginFormFailed) {
            writer.println("<font color='red'>Invalid username or password!</font><br>");
        }

        writer.println("<form method='post'>Username: <input name='username'><br>Password: <input name='password' type='password'><br><input type='submit' value='Login'></form>");
        writer.flush();
        writer.close();
    }

    private boolean spnegoLogin(String token, HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SPNEGOAuthenticator spnegoAuthenticator = new SPNEGOAuthenticator(token);
        spnegoAuthenticator.authenticate(token);

        if (spnegoAuthenticator.isAuthenticated()) {
            session.setAttribute("principal", spnegoAuthenticator.getPrincipal());
            session.setAttribute("auth", "spnego");
            return true;
        }  else {
            String spnegoHeader = spnegoAuthenticator.getResponseToken() != null ? "Negotiate " + spnegoAuthenticator.getResponseToken() : "Negotiate";
            sendResponse(req, resp, spnegoHeader, false);
            return false;
        }
    }

    @Override
    public void destroy() {

    }
}

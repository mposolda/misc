package org.mposolda;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class PostServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("POST Invoked!");

        resp.getWriter().println("Hello from POST!");
    }
}

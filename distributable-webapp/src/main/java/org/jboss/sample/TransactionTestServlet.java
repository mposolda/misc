package org.jboss.sample;

import java.io.IOException;
import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class TransactionTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserTransaction userTransaction = null;
        try {
            Context context = new InitialContext();
            userTransaction = (UserTransaction) context.lookup("java:comp/UserTransaction");
            userTransaction.begin();

            DataSource idmDs = (DataSource)context.lookup("java:/jdbcidm_portal");
            DataSource jcrDs = (DataSource)context.lookup("java:/jdbcjcr_portal");

            Connection idmConnection = idmDs.getConnection();
            Connection jcrConnection = jcrDs.getConnection();
            idmConnection.close();
            jcrConnection.close();

            resp.getWriter().println("userTransaction: " + userTransaction + ", idmDs: " + idmDs + ", jcrDs: " + jcrDs);

            userTransaction.commit();
        } catch (Exception e) {
            int status = -1;
            try {
                status = userTransaction.getStatus();

                if (status == Status.STATUS_ACTIVE || status == Status.STATUS_MARKED_ROLLBACK) {
                    userTransaction.rollback();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}

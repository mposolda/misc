package org.jboss.resteasy.sample;

import java.io.IOException;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * RequestHandler object is eating exceptions. Hence this handler will resend them
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SendErrorRequestDispatcher extends RequestDispatcher {

    public SendErrorRequestDispatcher(SynchronousDispatcher dispatcher, ResteasyProviderFactory providerFactory, SecurityDomain domain) {
        super(dispatcher, providerFactory, domain);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response, boolean handleNotFound) throws IOException {
        try {
            super.service(request, response, handleNotFound);
        } catch (Exception e) {
            e.printStackTrace();
            response.reset();
            response.setStatus(500);
            response.sendError(500, "Unexpected error during resteasy processing. Details in server.log. Message: " + e.getMessage());
        }
    }
}

package org.jboss.resteasy.sample;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Path("/a")
public class EndpointA {

    @Context
    private ResourceContext resourceContext;

    @Path("{param}/some")
    public ServiceA getServiceA(final @PathParam("param") String param) {
        ServiceA servA = new ServiceA();
        System.out.println("Param is: " + param);
        resourceContext.initResource(servA);
        return servA;
    }
}

package org.jboss.resteasy.sample;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.cache.NoCache;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ServiceA {

    @Context
    protected UriInfo uriInfo;

    @Context
    private ResourceContext resourceContext;

    @Context
    private HelloService helloService;

    public ServiceA() {
    }

    @GET
    @NoCache
    @Produces("text/html")
    public Response entryPoint() {
        String response = new StringBuilder("<html><head><title>Cool page</title></head><body>")
                .append("JSON: <a href='" + uriInfo.getBaseUriBuilder().path(EndpointA.class).path(EndpointA.class, "getServiceA")
                        .path(ServiceA.class, "jsonEndpoint").build("kokos").toString() + "'>here</a><br>")
                .append("</body></html").toString();

        return Response.ok(response).build();
    }

    @GET
    @NoCache
    @Path("jsonEndpoint")
    @Produces("application/json")
    public Response jsonEndpoint(final @PathParam("param") String param) {
        JsonObject obj = helloService.hello(param);
        return Response.ok(obj).build();
    }
}

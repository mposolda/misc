package org.mposolda;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Path("/base")
public class SimpleResource {

    // curl --request GET http://localhost:8080/simple-rest-angular/rest/base?sleep=5000 --header "Accept: application/json"
    @GET
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleAppUser getUser(@QueryParam("sleep") String sleep) {

        if (sleep != null && sleep.length() > 0) {
            int sleepInt = Integer.parseInt(sleep);
            System.out.println("Sleeping for " + sleepInt + " ms");

            try {
                Thread.sleep(sleepInt);
            } catch (InterruptedException ie) {}
        }

        return new SimpleAppUser("John", "Doe", "john@email.org");
    }
}

package org.mposolda.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/robots.txt")
public class RobotsResource {

    private static final String robots = "User-agent: *\n" + "Disallow: /";

    @GET
    @Produces(MediaType.TEXT_PLAIN_UTF_8)
    public String getRobots() {
        return robots;
    }

}
package org.jboss.resteasy.sample;

import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Provider
public class HelloService {

    private int counter;

    public JsonObject hello(String name) {
        return new JsonObject("hello " + name);
    }
}

package org.jboss.resteasy.sample;

import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JsonObject {

    private static final AtomicInteger globalCounter = new AtomicInteger(0);

    public JsonObject(String greeting) {
        this.greeting = greeting;
        this.counter = globalCounter.incrementAndGet();
    }

    @JsonProperty("greeting")
    private String greeting;
    @JsonProperty("counter")
    private int counter;

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}

package org.gatein.example.cdi;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

/**
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@SessionScoped
public class DataBean implements Serializable {

    private String message = "Hello from CDI!";
    private Integer counter = 0;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }
}

package org.mposolda.drools.uripolicytest.proxytest;

import java.beans.Introspector;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class InvHandler implements InvocationHandler {

    private final Object delegate;

    public InvHandler(Object delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getName().startsWith("get")) {
            String propertyName = Introspector.decapitalize(method.getName().substring(3));
            if (propertyName.equals("foo")) {
                return "FOO found";
            } else if (propertyName.equals("bar")) {
                return "BAR found";
            } else {
                return null;
            }
        } else {
            return method.invoke(delegate, args);
        }
    }
}

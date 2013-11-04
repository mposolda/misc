package org.mposolda.drools.uripolicytest.proxytest;

import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ProxyTest {

    public static void main(String[] args) {
        Object proxy = Proxy.newProxyInstance(ProxyTest.class.getClassLoader(), new Class[] { MyInterface.class, Kokos.class }, new InvHandler(new Object()));
        MyInterface myProxy = (MyInterface)proxy;
        System.out.println(myProxy.getBar());
        System.out.println(myProxy.getFoo());
        System.out.println(myProxy.getBaz());
//        System.out.println(myProxy.something());
        System.out.println(myProxy.hashCode());
    }
}

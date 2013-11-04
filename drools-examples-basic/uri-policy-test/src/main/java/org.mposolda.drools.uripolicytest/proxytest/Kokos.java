package org.mposolda.drools.uripolicytest.proxytest;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface Kokos {

    default String getUri() {
        return "haha";
    }
}

package org.mposolda;

import javax.net.ssl.SSLSession;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SessionDump {

    public void dumpSSLSession(SSLSession session) {
        StringBuilder builder = new StringBuilder("SSLSession: ");
        builder.append("cipherSuite=" + session.getCipherSuite());
        builder.append(", protocol=" + session.getProtocol());

        String peerPrincipal = null;
        try {
            peerPrincipal = session.getPeerPrincipal().toString();
        } catch (Exception ignore) {};

        String localPrincipal = null;
        try {
            localPrincipal = session.getLocalPrincipal().toString();
        } catch (Exception ignore) {};

        builder.append(", peerPrincipal: " + peerPrincipal);
        builder.append(", localPrincipal: " + localPrincipal);
        builder.append(" ]");

        System.out.println(builder.toString());
    }
}

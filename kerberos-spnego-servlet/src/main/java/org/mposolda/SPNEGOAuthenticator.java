package org.mposolda;

import java.security.PrivilegedExceptionAction;
import java.util.Date;

import javax.security.auth.Subject;

import net.iharder.Base64;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SPNEGOAuthenticator {

    private static final GSSManager GSS_MANAGER = GSSManager.getInstance();

    private final String spnegoToken;

    private boolean authenticated = false;
    private String principal = null;
    private String responseToken = null;

    public SPNEGOAuthenticator(String spnegoToken) {
        this.spnegoToken = spnegoToken;
    }

    public void authenticate(String token) {
        System.out.println(new Date() + " SPNEGO Login with token: " + token);

        KerberosServerSubjectAuthenticator serverAuthenticator = new KerberosServerSubjectAuthenticator();
        try {
            Subject serverSubject = serverAuthenticator.authenticateServerSubject();
            authenticated = Subject.doAs(serverSubject, new AcceptSecContext());
        } catch (Exception e) {
            System.err.println(new Date() + " SPNEGO login failed");
            e.printStackTrace();
        } finally {
            serverAuthenticator.logoutServerSubject();
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getResponseToken() {
        return responseToken;
    }

    private class AcceptSecContext implements PrivilegedExceptionAction<Boolean> {

        @Override
        public Boolean run() throws Exception {
            GSSContext gssContext = null;
            try {
                System.out.println(new Date() + " GOING TO ESTABLISH SECURITY CONTEXT NOW");
                gssContext = getEstablishedContext();

                // Some logging
                System.out.println("AcceptSecContext: acceptSecContext finished successfuly with token  "
                        + responseToken + " from acceptSecContext.");
                System.out.println("AcceptSecContext: gssContext.isEstablished()=" + gssContext.isEstablished());
                System.out.println("AcceptSecContext: context.getCredDelegState() = " + gssContext.getCredDelegState());

                if (gssContext.getCredDelegState()) {
                    System.out.println("AcceptSecContext: context.getDelegCred() = " + gssContext.getDelegCred());
                }

                System.out.println("AcceptSecContext: context.getMutualAuthState() = " + gssContext.getMutualAuthState());
                System.out.println("AcceptSecContext: context.getLifetime() = " + gssContext.getLifetime());
                System.out.println("AcceptSecContext: context.getConfState() = " + gssContext.getConfState());
                System.out.println("AcceptSecContext: context.getIntegState() = " + gssContext.getIntegState());
                System.out.println("AcceptSecContext: context.getSrcName() = " + gssContext.getSrcName());
                System.out.println("AcceptSecContext: context.getTargName() = " + gssContext.getTargName());

                if (gssContext.isEstablished()) {
                    principal = gssContext.getSrcName().toString();
                    return true;
                } else {
                    return false;
                }
            } finally {
                if (gssContext != null) {
                    gssContext.dispose();
                }
            }
        }

        private GSSContext getEstablishedContext() throws Exception {
            Oid spnegoOid = new Oid("1.3.6.1.5.5.2");
            GSSCredential credential = GSS_MANAGER.createCredential(null,
                    GSSCredential.DEFAULT_LIFETIME,
                    spnegoOid,
                    GSSCredential.ACCEPT_ONLY);
            GSSContext gssContext = GSS_MANAGER.createContext(credential);

            byte[] inputToken = Base64.decode(spnegoToken);
            byte[] respToken = gssContext.acceptSecContext(inputToken, 0, inputToken.length);
            responseToken = Base64.encodeBytes(respToken);

            return gssContext;
        }
    }
}

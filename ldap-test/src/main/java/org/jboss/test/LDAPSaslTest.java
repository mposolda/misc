package org.jboss.test;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.sasl.Sasl;

import org.ietf.jgss.GSSCredential;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class LDAPSaslTest {

    public static void main(String[] args) throws Exception {
        new LDAPSaslTest().mainn(args);
    }

    public void mainn(String[] args) throws Exception {
        getAvailableSaslMechanisms();
        digestMd5SaslAuthentication();
    }

    private void getAvailableSaslMechanisms() throws NamingException {
        Hashtable env = new Hashtable(11);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://localhost:10389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
        env.put(Context.SECURITY_CREDENTIALS, "secret");


        DirContext ctx = new InitialDirContext(env);
        Attributes attrs = ctx.getAttributes("ldap://localhost:10389", new String[] { "supportedSASLMechanisms" });

        System.out.println("Available authentication mechanism of SASL server: " + attrs);
    }

    private void digestMd5SaslAuthentication() throws NamingException {
        Hashtable env = new Hashtable(11);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://localhost:10389");
        env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");
        env.put(Context.SECURITY_PRINCIPAL, "hnelson");
        env.put(Context.SECURITY_CREDENTIALS, "secret");
        env.put("java.naming.security.sasl.realm", "localhost");
        DirContext ctx = new InitialDirContext(env);
        try {
            Attributes attrs = ctx.getAttributes("uid=hnelson,ou=People,dc=keycloak,dc=org");

            // This is binary data
            attrs.remove("krb5key");

            System.out.println("DIGEST-MD5 Authentication success! Attributes of hnelson: " + attrs);
        } finally {
            ctx.close();
        }
    }

    private String invokeLdap() throws NamingException {
        Hashtable env = new Hashtable(11);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://localhost:10389");

        DirContext ctx = new InitialDirContext(env);
        try {
            Attributes attrs = ctx.getAttributes("uid=hnelson,ou=People,dc=keycloak,dc=org");
            String cn = (String) attrs.get("cn").get();
            String sn = (String) attrs.get("sn").get();
            return cn + " " + sn;
        } finally {
            ctx.close();
        }
    }
}

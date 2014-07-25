package org.jboss.test;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ActiveDirectoryTesst {

    public static void main(String[] args) throws NamingException, IOException {
        System.out.println("Start test");

        // System.setProperty("com.sun.jndi.ldap.connect.pool.debug", "all");
        System.setProperty("com.sun.jndi.ldap.connect.pool.maxsize", "5");
        System.setProperty("com.sun.jndi.ldap.connect.pool.protocol", "plain ssl");


        // Activate paged results
        int pageSize = 5; // 5 entries per page
        byte[] cookie = new byte[0];
        byte[] crookie = null;
        int total = 0;

        while (cookie != null)
        {
            LdapContext ldapContext = getLdapContext();
            System.err.println("Context obtained");

            //Reset request controls
            ldapContext.setRequestControls(null);

            SearchControls searchControls = new SearchControls();
            searchControls.setReturningObjFlag(false);
            searchControls.setTimeLimit(10000);
            searchControls.setReturningAttributes(new String[] { "objectGUID", "createTimeStamp", "objectclass", "givenName", "sn", "cn", "mail"});

            Name jndiName = new CompositeName().add("ou=People,o=keycloak,dc=jboss,dc=test1");
            //Name jndiName = new CompositeName().add("ou=People,o=portal,o=gatein,dc=example,dc=com");

            String filter = "(&((whenChanged>=20130723153344.0Z)(objectClass=person)(objectClass=organizationalPerson)(objectClass=user)))";
            //String filter = "(&((objectClass=person)(objectClass=organizationalPerson)))";

            ldapContext.setRequestControls(new Control[] {
                    new PagedResultsControl(pageSize, cookie, Control.CRITICAL) });
            NamingEnumeration resultsEnumeration = ldapContext.search(jndiName, filter, searchControls);

            // Can null cookie now
            cookie = null;

            //results.addAll(Tools.toList(resultsEnumeration));
            while (resultsEnumeration.hasMoreElements())
            {
                SearchResult sr = (SearchResult)resultsEnumeration.nextElement();
                System.out.println(sr.getAttributes());

                if (sr instanceof HasControls) {
                    // ((HasControls)entry).getControls();
                    System.out.println("HAS CONTROLS: " + ((HasControls)sr).getControls());
                }
            }

            Control[] controls = ldapContext.getResponseControls();
            if (controls != null) {
                for (int i = 0; i < controls.length; i++) {
                    if (controls[i] instanceof PagedResultsResponseControl) {
                        PagedResultsResponseControl prrc =
                                (PagedResultsResponseControl)controls[i];
                        total = prrc.getResultSize();
                        cookie = prrc.getCookie();

                        System.out.println("TOTAL RESULTS: " + total + ", cookie: " + cookie);
                    } else {
                        // Handle other response controls (if any)
                        System.out.println("OTher response control: " + controls[i].getClass());
                    }
                }
            } else {
                System.out.println("Controls are null");
            }

            // Can close enumeration now
            resultsEnumeration.close();
            ldapContext.close();
        }

        System.err.println("Context was closed");
    }

    private static LdapContext getLdapContext() throws NamingException {
        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.PROVIDER_URL, "ldaps://dev101.mw.lab.eng.bos.redhat.com/");
        env.put(Context.SECURITY_PRINCIPAL, "JBOSS1\\jbossqa");
        env.put(Context.SECURITY_CREDENTIALS, "jboss42");

//        env.put(Context.PROVIDER_URL, "ldap://localhost:1389");
//        env.put(Context.SECURITY_PRINCIPAL, "cn=Directory Manager");
//        env.put(Context.SECURITY_CREDENTIALS, "password");

        env.put("com.sun.jndi.ldap.connect.pool", "true");
        return new InitialLdapContext(env, null);
    }
}

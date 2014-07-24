package org.jboss.test;

import java.util.Hashtable;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ActiveDirectoryTesst {

    public static void main(String[] args) throws NamingException {
        System.out.println("Start test");

        // System.setProperty("com.sun.jndi.ldap.connect.pool.debug", "all");
        System.setProperty("com.sun.jndi.ldap.connect.pool.maxsize", "5");
        System.setProperty("com.sun.jndi.ldap.connect.pool.protocol", "plain ssl");

        LdapContext ldapContext = getLdapContext();
        System.err.println("Context obtained");

        //Reset request controls
        ldapContext.setRequestControls(null);

        SearchControls searchControls = new SearchControls();
        searchControls.setReturningObjFlag(false);
        searchControls.setTimeLimit(10000);
        searchControls.setReturningAttributes(new String[] { "objectGUID", "createTimeStamp", "objectclass", "givenName", "sn", "cn", "mail"});

        Name jndiName = new CompositeName().add("ou=People,o=keycloak,dc=jboss,dc=test1");

        String filter = "(&((whenChanged>=20140723153344.0Z)(objectClass=person)(objectClass=organizationalPerson)(objectClass=user)))";

        NamingEnumeration resultsEnumeration = ldapContext.search(jndiName, filter, searchControls);

        if (resultsEnumeration != null)
        {
            //results.addAll(Tools.toList(resultsEnumeration));

            while (resultsEnumeration.hasMoreElements())
            {
                SearchResult sr = (SearchResult)resultsEnumeration.nextElement();
                System.out.println(sr.getAttributes());
            }

            resultsEnumeration.close();
        }

        ldapContext.close();

        System.err.println("Context was closed");
    }

    private static LdapContext getLdapContext() throws NamingException {
        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
//        env.put(Context.PROVIDER_URL, "ldap://localhost:1389");
//        env.put(Context.SECURITY_PRINCIPAL, "cn=Directory Manager");
//        env.put(Context.SECURITY_CREDENTIALS, "password");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.PROVIDER_URL, "ldaps://dev101.mw.lab.eng.bos.redhat.com/");
        env.put(Context.SECURITY_PRINCIPAL, "JBOSS1\\jbossqa");
        env.put(Context.SECURITY_CREDENTIALS, "jboss42");

        env.put("com.sun.jndi.ldap.connect.pool", "true");
        return new InitialLdapContext(env, null);
    }
}

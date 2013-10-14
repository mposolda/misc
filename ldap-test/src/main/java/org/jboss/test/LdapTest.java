package org.jboss.test;

import java.util.Hashtable;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class LdapTest {

    public static void main(String[] args) throws NamingException {
        System.out.println("Start test");

        System.setProperty("com.sun.jndi.ldap.connect.pool.debug", "all");
        System.setProperty("com.sun.jndi.ldap.connect.pool.maxsize", "5");
        System.setProperty("com.sun.jndi.ldap.connect.pool.protocol", "plain ssl");

        LdapContext ldapContext = getLdapContext();
        System.err.println("Context obtained");

        //Reset request controls
        ldapContext.setRequestControls(null);

        SearchControls searchControls = new SearchControls();
        searchControls.setReturningObjFlag(false);
        searchControls.setTimeLimit(10000);
        searchControls.setReturningAttributes(new String[] {"cn"});

        //searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        //searchControls.setSearchScope(SearchControls.OBJECT_SCOPE);

        Name jndiName = new CompositeName().add("ou=Platform,o=portal,o=gatein,dc=example,dc=com");

        String filter = "(cn=users)";

        NamingEnumeration resultsEnumeration = ldapContext.search(jndiName, filter, searchControls);

        if (resultsEnumeration != null)
        {
            //results.addAll(Tools.toList(resultsEnumeration));

            while (resultsEnumeration.hasMoreElements())
            {
                SearchResult sr = (SearchResult)resultsEnumeration.nextElement();
                Object o = sr.getObject();
                System.err.println("Object: " + o);

                // THIS IS NEEDED IF setReturningObjFlag IS TRUE
                if (o instanceof Context) {
                    ((Context)o).close();
                }
                // results.add(new SerializableSearchResult(sr));
            }

            resultsEnumeration.close();
        }

        ldapContext.close();

        System.err.println("Context was closed");
    }

    private static LdapContext getLdapContext() throws NamingException {
        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://localhost:1389");
        env.put(Context.SECURITY_PRINCIPAL, "cn=Directory Manager");
        env.put(Context.SECURITY_CREDENTIALS, "password");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put("com.sun.jndi.ldap.connect.pool", "true");
        return new InitialLdapContext(env, null);
    }

}

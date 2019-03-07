package org.jboss.test;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

public class LDAPPaginationTest {

    public static void main(String[] args) throws Exception {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("com.sun.jndi.ldap.connect.pool", "true");
        System.setProperty("com.sun.jndi.ldap.connect.pool.debug", "fine");
        System.setProperty("com.sun.jndi.ldap.connect.pool.initsize", "1");
        System.setProperty("com.sun.jndi.ldap.connect.pool.prefsize", "5");
        System.setProperty("com.sun.jndi.ldap.connect.pool.maxsize", "100");
        System.setProperty("com.sun.jndi.ldap.connect.pool.protocol", "plain ssl");

        String ldapVendor = System.getProperty("ldap.vendor", "apacheds");
        boolean singleCtx = System.getProperty("ldap.ctx", "single").equals("single");

        System.out.println("LDAP Vendor: " + ldapVendor + ", Single ctx: " + singleCtx);

        String usersDn = null;

        if (ldapVendor.equals("apacheds")) {
            usersDn = "ou=People,dc=keycloak,dc=org";
            env.put(Context.PROVIDER_URL, "ldap://localhost:10389");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
            env.put(Context.SECURITY_CREDENTIALS, "secret");
        } else if (ldapVendor.equals("freeipa")) {
            usersDn = "cn=users,cn=accounts,dc=demo1,dc=freeipa,dc=org";
            env.put(Context.PROVIDER_URL, "ldap://ipa.demo1.freeipa.org:389");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "uid=admin,cn=users,cn=accounts,dc=demo1,dc=freeipa,dc=org");
            env.put(Context.SECURITY_CREDENTIALS, "Secret123");
        } else if (ldapVendor.equals("msad")) {
            usersDn = "OU=People,O=keycloak,DC=JBOSS3,DC=test";

            env.put(Context.PROVIDER_URL, "ldaps://dev156-w2012-x86-64.JBOSS3.test:636");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "JBOSS3\\jbossqa");
            env.put(Context.SECURITY_CREDENTIALS, "jboss42");
        } else {
            throw new IllegalArgumentException("Unknown LDAP Vendor: " + ldapVendor);
        }


        LdapContext ctx = null;
        if (singleCtx) {
            ctx = new InitialLdapContext(env, null);
        }

        // Activate paged results
        int pageSize = 1;
        byte[] cookie = null;
        //ctx.setRequestControls(new Control[]{new PagedResultsControl(pageSize, Control.NONCRITICAL)});
        int total;

        do {
            if (!singleCtx) {
                ctx = new InitialLdapContext(env, null);
            }

            // Very 1st page
            if (cookie == null) {
                ctx.setRequestControls(new Control[]{new PagedResultsControl(pageSize, Control.NONCRITICAL)});
            } else {
                ctx.setRequestControls(new Control[]{new PagedResultsControl(pageSize, cookie, Control.CRITICAL)});
            }

    /* perform the search */
            NamingEnumeration results = ctx.search(usersDn, "(objectclass=*)",
                    new SearchControls());

    /* for each entry print out name + all attrs and values */
            while (results != null && results.hasMore()) {
                SearchResult entry = (SearchResult) results.next();
                System.out.println(entry.getName());
            }

            // Examine the paged results control response
            Control[] controls = ctx.getResponseControls();
            if (controls != null) {
                for (int i = 0; i < controls.length; i++) {
                    if (controls[i] instanceof PagedResultsResponseControl) {
                        PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
                        total = prrc.getResultSize();
                        if (total != 0) {
                            System.out.println("***************** END-OF-PAGE "
                                    + "(total : " + total + ") *****************\n");
                        } else {
                            System.out.println("***************** END-OF-PAGE "
                                    + "(total: unknown) ***************\n");
                        }
                        cookie = prrc.getCookie();
                    }
                }
            } else {
                System.out.println("No controls were sent from the server");
            }

            if (!singleCtx) {
                ctx.close();
            }

            // Re-activate paged results
//            ctx.setRequestControls(new Control[]{new PagedResultsControl(
//                    pageSize, cookie, Control.CRITICAL)});

        } while (cookie != null);

        ctx.close();
    }
}


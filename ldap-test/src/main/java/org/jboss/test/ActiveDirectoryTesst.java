package org.jboss.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

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

    private static final Config ACTIVE_DIRECTORY = new Config()
            .setProviderURL("ldaps://dev101.mw.lab.eng.bos.redhat.com/")
            .setUserDN("ou=People,o=keycloak,dc=jboss,dc=test1")
            .setSecurityPrincipal("JBOSS1\\jbossqa")
            .setSecurityCredentials("jboss42");

    private static final Config OPENDS = new Config()
            .setProviderURL("ldap://localhost:1389")
            .setUserDN("ou=People,o=portal,o=gatein,dc=example,dc=com")
            .setSecurityPrincipal("cn=Directory Manager")
            .setSecurityCredentials("password");

    private static final Config OPENLDAP = new Config()
            .setProviderURL("ldap://localhost:389")
            .setUserDN("ou=People,dc=example,dc=com")
            .setSecurityPrincipal("cn=admin,dc=example,dc=com")
            .setSecurityCredentials("password");

    public static void main(String[] args) throws NamingException, IOException {
        Config cfg = ACTIVE_DIRECTORY;
        // Config cfg = OPENDS;
        // Config cfg = OPENLDAP;

        // System.setProperty("com.sun.jndi.ldap.connect.pool.debug", "all");
        System.setProperty("com.sun.jndi.ldap.connect.pool.maxsize", "5");
        System.setProperty("com.sun.jndi.ldap.connect.pool.protocol", "plain ssl");

        paginationTest(cfg);

        addUpdateRemoveUser(cfg);
    }

    private static void paginationTest(Config cfg) throws NamingException, IOException {
        // Activate paged results
        int pageSize = 5; // 5 entries per page
        byte[] cookie = new byte[0];
        int total = 0;

        while (cookie != null)
        {
            LdapContext ldapContext = getLdapContext(cfg);

            //Reset request controls
            ldapContext.setRequestControls(null);

            SearchControls searchControls = new SearchControls();
            searchControls.setReturningObjFlag(false);
            searchControls.setTimeLimit(10000);
            searchControls.setReturningAttributes(new String[] { "objectGUID", "uid", "objectclass", "givenName", "sn", "cn", "mail", "createTimeStamp", "modifyTimeStamp", "whenCreated", "whenChanged"});

            Name jndiName = new CompositeName().add(cfg.userDN);

            String filter = "(&((whenChanged>=20140622134652.0Z)(objectClass=person)(objectClass=organizationalPerson)(objectClass=user)))";
            // String filter = "(&((modifyTimestamp>=20130529132901.0Z)(objectClass=person)(objectClass=organizationalPerson)))";

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
    }

    private static void addUpdateRemoveUser(Config cfg) throws NamingException, IOException {

    }

    private static LdapContext getLdapContext(Config cfg) throws NamingException {
        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        env.put(Context.PROVIDER_URL, cfg.providerURL);
        env.put(Context.SECURITY_PRINCIPAL, cfg.securityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, cfg.securityCredentials);

        env.put("com.sun.jndi.ldap.connect.pool", "true");
        return new InitialLdapContext(env, null);
    }

    private static class Config {
        private String providerURL;
        private String securityPrincipal;
        private String securityCredentials;
        private String userDN;

        public Config setProviderURL(String providerURL) {
            this.providerURL = providerURL;
            return this;
        }

        public Config setSecurityPrincipal(String securityPrincipal) {
            this.securityPrincipal = securityPrincipal;
            return this;
        }

        public Config setSecurityCredentials(String securityCredentials) {
            this.securityCredentials = securityCredentials;
            return this;
        }

        public Config setUserDN(String userDN) {
            this.userDN = userDN;
            return this;
        }
    }
}

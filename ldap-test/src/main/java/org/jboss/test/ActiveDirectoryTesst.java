package org.jboss.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
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
        //Config cfg = OPENDS;
        // Config cfg = OPENLDAP;

        // System.setProperty("com.sun.jndi.ldap.connect.pool.debug", "all");
        System.setProperty("com.sun.jndi.ldap.connect.pool.maxsize", "5");
        System.setProperty("com.sun.jndi.ldap.connect.pool.protocol", "plain ssl");

        // paginationTest(cfg);

        // addUser(cfg);
        updateUser(cfg);
        // removeUser(cfg);
    }

    private static void paginationTest(Config cfg) throws NamingException, IOException {
        // Activate paged results
        int pageSize = 5; // 5 entries per page
        byte[] cookie = new byte[0];
        int total = 0;

        while (cookie != null) {
            LdapContext ldapContext = getLdapContext(cfg);

            //Reset request controls
            ldapContext.setRequestControls(null);

            SearchControls searchControls = new SearchControls();
            searchControls.setReturningObjFlag(false);
            searchControls.setTimeLimit(10000);
            searchControls.setReturningAttributes(new String[]{"objectGUID", "uid", "objectclass", "givenName", "sn", "cn", "mail", "createTimeStamp", "modifyTimeStamp", "whenCreated", "whenChanged"});

            Name jndiName = new CompositeName().add(cfg.userDN);

            String filter = "(&((whenChanged>=20140622134652.0Z)(objectClass=person)(objectClass=organizationalPerson)(objectClass=user)))";
            // String filter = "(&((modifyTimestamp>=20130529132901.0Z)(objectClass=person)(objectClass=organizationalPerson)))";

            ldapContext.setRequestControls(new Control[]{
                    new PagedResultsControl(pageSize, cookie, Control.CRITICAL)});
            NamingEnumeration resultsEnumeration = ldapContext.search(jndiName, filter, searchControls);

            // Can null cookie now
            cookie = null;

            //results.addAll(Tools.toList(resultsEnumeration));
            while (resultsEnumeration.hasMoreElements()) {
                SearchResult sr = (SearchResult) resultsEnumeration.nextElement();
                System.out.println(sr.getAttributes());

                if (sr instanceof HasControls) {
                    // ((HasControls)entry).getControls();
                    System.out.println("HAS CONTROLS: " + ((HasControls) sr).getControls());
                }
            }

            Control[] controls = ldapContext.getResponseControls();
            if (controls != null) {
                for (int i = 0; i < controls.length; i++) {
                    if (controls[i] instanceof PagedResultsResponseControl) {
                        PagedResultsResponseControl prrc =
                                (PagedResultsResponseControl) controls[i];
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

    private static void addUser(Config cfg) throws NamingException, IOException {
        LdapContext ldapContext = getLdapContext(cfg);
        try {

            Attributes entryAttributes = getLDAPAttributes();
            String dn = getExampleUserDN(cfg);

            DirContext subcontext = ldapContext.createSubcontext(dn, entryAttributes);
            subcontext.close();
        } finally {
            ldapContext.close();
        }
    }

    private static void updateUser(Config cfg) throws NamingException, IOException {
        LdapContext ldapContext = getLdapContext(cfg);

        try {
            Attributes attributes = getLDAPAttributesForUpdate();
            NamingEnumeration<? extends Attribute> attrs = attributes.getAll();
            String dn = getExampleUserDN(cfg);

            List<ModificationItem> modItems = new ArrayList<ModificationItem>();
            while (attrs.hasMore()) {
                ModificationItem modItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attrs.next());
                modItems.add(modItem);
            }

            ldapContext.modifyAttributes(dn, modItems.toArray(new ModificationItem[] {}));
        } finally {
            ldapContext.close();
        }
    }

    private static void removeUser(Config cfg) throws NamingException, IOException {
        LdapContext ldapContext = getLdapContext(cfg);

        try {

        } finally {
            ldapContext.close();
        }
    }
   /*
    private static String getFilterById(String id) {
        return "(&(objectClass=*)(entryUUID=" + id + "))";
    }

    private static String getFilterByIdActiveDirectory(Config cfg, String id) {
        final String strObjectGUID = "<GUID=" + id + ">";
        String filter;
        try {
            LdapContext ldapContext = getLdapContext(cfg);
            Attributes attributes = ldapContext.getAttributes(strObjectGUID);
            ldapContext.close();

            byte[] objectGUID = (byte[]) attributes.get("objectGUID").get();

            return "(&(objectClass=*)(objectGUID=" + convertObjectGUIToByteString(objectGUID) + "))";
        } catch (NamingException ne) {
            return null;
        }
    }

    public static String convertObjectGUIToByteString(byte[] objectGUID) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < objectGUID.length; i++) {
            String transformed = prefixZeros((int) objectGUID[i] & 0xFF);
            result.append("\\");
            result.append(transformed);
        }

        return result.toString();
    }

    private static String prefixZeros(int value) {
        if (value <= 0xF) {
            StringBuilder sb = new StringBuilder("0");
            sb.append(Integer.toHexString(value));
            return sb.toString();
        } else {
            return Integer.toHexString(value);
        }
    }    */

    private static Attributes getLDAPAttributes() {
        BasicAttributes entryAttributes = new BasicAttributes();
        entryAttributes.put("sn", "Kokos");
        entryAttributes.put("mail", "Kokos@emailekkk.cz");
        BasicAttribute objectClassAttribute = new BasicAttribute("objectClass");

        // AD
//        entryAttributes.put("givenName", "Johny");
//        entryAttributes.put("cn", "John Kokos");
//        entryAttributes.put("sAMAccountName", "john2");
//        entryAttributes.put("objectClass", "john2");
//        objectClassAttribute.add("organizationalPerson");
//        objectClassAttribute.add("person");
//        objectClassAttribute.add("user");

        // Others
        entryAttributes.put("cn", "John");
        entryAttributes.put("uid", "john2");
        objectClassAttribute.add("inetOrgPerson");
        objectClassAttribute.add("person");

        entryAttributes.put(objectClassAttribute);
        return entryAttributes;
    }

    private static Attributes getLDAPAttributesForUpdate() {
        BasicAttributes entryAttributes = new BasicAttributes();
        entryAttributes.put("sn", "Kokosak");
        entryAttributes.put("mail", "Kokosak@emailekkk.cz");

        // AD
        entryAttributes.put("givenName", "Johny");
        //entryAttributes.put("cn", "John Kokos");
        entryAttributes.put("sAMAccountName", "johnnn2");

        // Other servers
//        entryAttributes.put("uid", "john2");
//        entryAttributes.put("cn", "Johnn");

        return entryAttributes;
    }

    private static String getExampleUserDN(Config cfg) {
        // AD
        return "cn=John Kokos," + cfg.userDN;

        // Others
        // return "uid=john2," + cfg.userDN;
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

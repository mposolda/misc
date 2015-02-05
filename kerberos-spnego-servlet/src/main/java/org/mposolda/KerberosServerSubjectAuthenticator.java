package org.mposolda;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class KerberosServerSubjectAuthenticator {

    private LoginContext loginContext;

    private KerberosConfig kerberosConfig = new KerberosConfig();

    public Subject authenticateServerSubject() throws LoginException {
        Configuration config = createJaasConfiguration();
        loginContext = new LoginContext("does-not-matter", null, null, config);
        loginContext.login();
        return loginContext.getSubject();
    }

    public void logoutServerSubject() {
        if (loginContext != null) {
            try {
                loginContext.logout();
            } catch (LoginException le) {
                le.printStackTrace();
            }
        }
    }

    protected Configuration createJaasConfiguration() {
        return new Configuration() {

            @Override
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                Map<String, Object> options = new HashMap<String, Object>();
                options.put("storeKey", "true");
                options.put("doNotPrompt", "true");
                options.put("isInitiator", "false");
                options.put("useKeyTab", "true");

//                options.put("keyTab", "/etc/krb5.keytab");
//                options.put("principal", "HTTP/server.local.network@LOCAL.NETWORK");
//                options.put("keyTab", "/tmp/http.keytab");
//                options.put("principal", "HTTP/server.jboss.org@JBOSS.ORG");

                options.put("keyTab", kerberosConfig.keyTab);
                options.put("principal", kerberosConfig.serverHttpPrincipal);
                options.put("debug", kerberosConfig.debug);
                AppConfigurationEntry kerberosLMConfiguration = new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
                return new AppConfigurationEntry[] { kerberosLMConfiguration };
            }
        };
    }


    private class KerberosConfig {
        private String keyTab;
        private String serverHttpPrincipal;
        private String debug;

        private KerberosConfig() {
            this.keyTab = System.getProperty("kerberos.keyTab");
            this.serverHttpPrincipal = System.getProperty("kerberos.serverHttpPrincipal");
            this.debug = System.getProperty("kerberos.debug");
        }
    }
}

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

                // TODO: Those should be configurable
                options.put("keyTab", "/etc/krb5.keytab");
                options.put("principal", "HTTP/server.local.network@LOCAL.NETWORK");
                options.put("debug", "true");
                AppConfigurationEntry kerberosLMConfiguration = new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
                return new AppConfigurationEntry[] { kerberosLMConfiguration };
            }
        };
    }
}

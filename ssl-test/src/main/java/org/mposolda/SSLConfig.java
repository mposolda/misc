package org.mposolda;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public enum SSLConfig {

    JKS("secret", SSLSocketServer.KEYSTORE_PATH),
    PKCS11(null, null);

    private final String keystorePassword;

    private final String keystoreFileLocation;

    SSLConfig(String keystorePassword, String keystoreFileLocation) {
        this.keystorePassword = keystorePassword;
        this.keystoreFileLocation = keystoreFileLocation;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getKeystoreFileLocation() {
        return keystoreFileLocation;
    }
}

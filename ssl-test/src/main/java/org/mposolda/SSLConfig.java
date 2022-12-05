package org.mposolda;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public enum SSLConfig {

    JKS("secret", SSLSocketServer.KEYSTORE_PATH, "localhost"),
    PKCS11(null, null, "keycloakk");

    private final String keystorePassword;

    private final String keystoreFileLocation;

    private final String keyAliasInKs;

    SSLConfig(String keystorePassword, String keystoreFileLocation, String keyAliasInKs) {
        this.keystorePassword = keystorePassword;
        this.keystoreFileLocation = keystoreFileLocation;
        this.keyAliasInKs = keyAliasInKs;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getKeystoreFileLocation() {
        return keystoreFileLocation;
    }

    public String getKeyAliasInKs() {
        return keyAliasInKs;
    }
}

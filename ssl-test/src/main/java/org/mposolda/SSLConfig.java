package org.mposolda;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public enum SSLConfig {

    JKS("secret", SSLSocketServer.KEYSTORE_PATH, SSLSocketClient.TRUSTSTORE_PATH, "localhost"),
    PKCS11(null, null, null, "keycloakk");

    private final String keystorePassword;

    // Keystore and truststore used by SSLSocketServer
    private final String keystoreFileLocation;

    // Keystore and truststore used by SSLSocketClient
    private final String clientKeystoreFileLocation;

    private final String keyAliasInKs;

    SSLConfig(String keystorePassword, String keystoreFileLocation, String clientKeystoreFileLocation, String keyAliasInKs) {
        this.keystorePassword = keystorePassword;
        this.keystoreFileLocation = keystoreFileLocation;
        this.clientKeystoreFileLocation = clientKeystoreFileLocation;
        this.keyAliasInKs = keyAliasInKs;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getKeystoreFileLocation() {
        return keystoreFileLocation;
    }

    public String getClientKeystoreFileLocation() {
        return clientKeystoreFileLocation;
    }

    public String getKeyAliasInKs() {
        return keyAliasInKs;
    }
}

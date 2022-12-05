Steps
=====
1) Run the script from "init-script.sh (Some commands require password, so use `secret`.
Use `localhost` as alias for private key of server (first one))

3) Run the SSLSocketServer from IDE

4) Run the SSLSocketClient from IDE (either with jbrown or bwilson certificate)



# NOTE

Look at directory "ssltest-openssl" for some example how to generate certificate authority, certificates and CRL

# HOW TO HAVE THIS RUNNING ON RHEL 8.6 WITH FIPS ENABLED

It requires NSS DB installed. It is installed by default, but may require some changes in the DB. 

NOTE: This is based on https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.4/html-single/how_to_configure_server_security/index#configure_ssl_fips_rhel7

Steps:

1) Assuming that `JAVA_HOME/conf/security/java.security` has security provider like this:

```
fips.provider.1=SunPKCS11 ${java.home}/conf/security/nss.fips.cfg`
```

Then make sure that the `nss.fips.cfg` has changed the property `nssSecmodDirectory` for example like this:

```
#nssSecmodDirectory = sql:/etc/pki/nssdb
nssSecmodDirectory = sql:/home/mposolda/nss-db
```

2) Then run commands like this (Some commands require some input, so don't run all at the same time):
```
export DB_DIR=/home/mposolda/nss-db
rm -rf $DB_DIR
mkdir -p $DB_DIR
modutil -create -dbdir $DB_DIR

chmod a+r $DB_DIR/*.db
modutil -fips true -dbdir $DB_DIR

# CHANGING PASSWORD DOES NOT WORK. IT IS NOT POSSIBLE TO USE THE PASSWORD FROM JAVA APPLICATION. SO USING EMPTY PW FOR NOW
# modutil -changepw "NSS FIPS 140-2 Certificate DB" -dbdir $DB_DIR

certutil -S -k rsa -n keycloakk  -t "u,u,u" -x -s "CN=localhost, OU=MYOU, O=MYORG, L=MYCITY, ST=MYSTATE, C=MY" -d $DB_DIR

# To doublecheck keystore. Should contain our entry (Ignore specifying password)
keytool -list -storetype pkcs11
```

3) Run the server application. On the laptop run something like this:

```
mvn clean install
cp target/ssl-test-0.1-SNAPSHOT.jar /home/mposolda/IdeaProjects/keycloak/common/target/
```

Then on RHEL VM (Assumption is that directory `/home/mposolda/IdeaProjects/keycloak` is shared with VM):
```
java -cp /home/mposolda/IdeaProjects/keycloak/common/target/ssl-test-0.1-SNAPSHOT.jar \
     -DkeystoreType=PKCS11 org.mposolda.SSLSocketServer
```


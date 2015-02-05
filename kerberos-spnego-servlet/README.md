Have example working on Tomcat
==============================

1) Add this to $TOMCAT_HOME/bin/catalina.sh

```
export CATALINA_OPTS="$CATALINA_OPTS -Dsun.security.krb5.debug=true -Dsun.security.spnego.debug=true -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n"
```

And additionally this to configure kerberos:

```
export CATALINA_OPTS="$CATALINA_OPTS -Dkerberos.keyTab=/etc/krb5.keytab -Dkerberos.serverHttpPrincipal=HTTP/server.local.network@LOCAL.NETWORK -Dkerberos.debug=true"
```


2) Run "mvn clean install" and deploy servlet to tomcat
 



3) Chrome needs to be executed with command like:

```
/usr/bin/google-chrome-stable --auth-server-whitelist="server.local.network,localhost,*redhat.com"
```

or 

```
/usr/bin/google-chrome-stable --auth-server-whitelist="server.local.network" --auth-negotiate-delegate-whitelist="server.local.network"
```

In case 2 is delegated authentication used, which is less safe, but allow to share credentials from client to other services 
(gssContext.getCredDelegState() will be true and gssContext.getDelegCred() will be non-null with credential of the user)

4) Run "kinit root" and go to "http://server.local.network:8080/kerberos-spnego-servlet" . If you have kerberos ticket, you are logged automatically 
 
 

Have example working with the server from kwart
===============================================

1) Download the project https://github.com/kwart/kerberos-using-apacheds and build with "mvn clean install"

```
java -agentlib:jdwp=transport=dt_socket,address=5006,server=y,suspend=n -jar target/kerberos-using-apacheds.jar test.ldif
```

2) Generate keytab from another window:

```
rm /tmp/http.keytab
java -classpath /home/mposolda/IdeaProjects/kerberos-using-apacheds/target/kerberos-using-apacheds.jar org.jboss.test.kerberos.CreateKeytab HTTP/localhost@JBOSS.ORG httppwd /tmp/http.keytab
```


3) There is possibility to configure your own krb5.conf file and point KRB5_CONFIG env variable to it (good krb5.conf is available in root directory of kerberos-using-apacheds project). But it's sufficient to add those
 encryptions to /etc/krb5.conf (works with my default KDC, with Kerberos from kwart and also with redhat.com - February 2015):

``` 
default_tgs_enctypes = aes256-cts-hmac-sha1-96 des3-cbc-sha1-kd rc4-hmac
default_tkt_enctypes = aes256-cts-hmac-sha1-96 des3-cbc-sha1-kd rc4-hmac
permitted_enctypes = aes256-cts-hmac-sha1-96 des3-cbc-sha1-kd rc4-hmac
```

4) Add this line to TOMCAT_HOME/bin/catalina.sh:
 
```
export CATALINA_OPTS="$CATALINA_OPTS -Dkerberos.keyTab=/tmp/http.keytab -Dkerberos.serverHttpPrincipal=HTTP/localhost@JBOSS.ORG -Dkerberos.debug=true"
```

5) Run "kinit hnelson@JBOSS.ORG" (password: secret) and go to "http://localhost:8080/kerberos-spnego-servlet" . If you have kerberos ticket, you are logged automatically
 

Working with Redhat.com
=======================
kinit mposolda@REDHAT.COM

and then go to "http://mojo.redhat.com"


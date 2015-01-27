Have example working on Tomcat
==============================

1) Add file $TOMCAT_HOME/conf/jaas.conf with the content like this:

kerberos-server {
        com.sun.security.auth.module.Krb5LoginModule required
        storeKey=true
        doNotPrompt=true
        useKeyTab=true
        keyTab="/etc/krb5.keytab"
        principal="HTTP/server.local.network@LOCAL.NETWORK"
        useFirstPass=true
        debug=true
        isInitiator=false;
};

2) Add this to $TOMCAT_HOME/bin/catalina.sh

# TODO
export CATALINA_OPTS="$CATALINA_OPTS -Djava.security.auth.login.config=/home/mposolda/work/keycloak/spnego/apache-tomcat-7.0.41/conf/jaas.conf"
export CATALINA_OPTS="$CATALINA_OPTS -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n"

3) Run "mvn clean install" and deploy servlet to tomcat
 
4) Chrome needs to be executed with command like:

```
/usr/bin/google-chrome-stable --auth-server-whitelist="server.local.network"
```

or 

```
/usr/bin/google-chrome-stable --auth-server-whitelist="server.local.network" --auth-negotiate-delegate-whitelist="server.local.network"
```

In case 2 is delegated authentication used, which is less safe, but allow to share credentials from client to other services 
(gssContext.getCredDelegState() will be true and gssContext.getDelegCred() will be non-null with credential of the user) 
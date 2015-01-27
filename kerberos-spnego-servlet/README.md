Have example working on Tomcat
==============================

1) Add this to $TOMCAT_HOME/bin/catalina.sh

# TODO
export CATALINA_OPTS="$CATALINA_OPTS -Dsun.security.krb5.debug=true -Dsun.security.spnego.debug=true -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n"

2) Run "mvn clean install" and deploy servlet to tomcat
 
3) Chrome needs to be executed with command like:

```
/usr/bin/google-chrome-stable --auth-server-whitelist="server.local.network"
```

or 

```
/usr/bin/google-chrome-stable --auth-server-whitelist="server.local.network" --auth-negotiate-delegate-whitelist="server.local.network"
```

In case 2 is delegated authentication used, which is less safe, but allow to share credentials from client to other services 
(gssContext.getCredDelegState() will be true and gssContext.getDelegCred() will be non-null with credential of the user) 
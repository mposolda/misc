Rolling Upgrade
---------------

1) Run IspnTestV1 with system properties:

-Dcom.sun.management.jmxremote.port=1255 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

2) Run CLI:
 
java -jar /home/mposolda/.m2/repository/org/infinispan/infinispan-cli/8.2.8.Final/infinispan-cli-8.2.8.Final.jar

3) connect jmx://localhost:1255

More in ISPN docs: http://infinispan.org/docs/8.2.x/user_guide/user_guide.html#_CLI_chapter
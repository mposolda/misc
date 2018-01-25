Rolling Upgrade
---------------

1) Run IspnTestV1 with system properties:

-Dcom.sun.management.jmxremote.port=1255 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

2) Run CLI:
 
java -jar /home/mposolda/.m2/repository/org/infinispan/infinispan-cli/8.2.8.Final/infinispan-cli-8.2.8.Final.jar

3) connect jmx://localhost:1255

More in ISPN docs: http://infinispan.org/docs/8.2.x/user_guide/user_guide.html#_CLI_chapter


Rolling upgrade servers with HotRod - alternative 1
---------------------------------------------------

1) Run infinispan-server 8.x


    cd ~/tmp/test-rolling-upgrades/infinispan-server-8.2.6.Final-1/bin
    ./standalone.sh -c clustered.xml -Djava.net.preferIPv4Stack=true \
    -Djboss.socket.binding.port-offset=1010 -Djboss.default.multicast.address=234.56.78.99 \
    -Djboss.node.name=cache-server-1
    
2) Configure infinispan 9.1.X server with remoteStore like this 
in `~/tmp/test-rolling-upgrades/infinispan-server-9.1.0.Final-1/standalone/configuration/clustered.xml`


    <distributed-cache name="default">
        <remote-store cache="default" socket-timeout="60000" tcp-no-delay="true" shared="true" 
            raw-values="true" hotrod-wrapping="true" purge="false" passivation="false" 
            protocol-version="2.4">
                <remote-server outbound-socket-binding="remote-store-hotrod-server"/>
        </remote-store>
    </distributed-cache>    
                
and:

        <outbound-socket-binding name="remote-store-hotrod-server">
            <remote-destination host="localhost" port="12232"/>
        </outbound-socket-binding>
 
3) Run infinispan-server-9.1.4 on different port and with different multicast address 

    cd ~/tmp/test-rolling-upgrades/infinispan-server-9.1.4.Final-1/bin
    ./standalone.sh -c clustered.xml -Djava.net.preferIPv4Stack=true \
    -Djboss.socket.binding.port-offset=3010 -Djboss.default.multicast.address=234.56.78.100 \
    -Djboss.node.name=cache-server-3 
        
4) Run `RemoteIspnTestV1` class with `-Djdg.port=12232` . Then create some items through:


    create 123
    create 456
    create 789
    
5) Connect through `jconsole` to the infinispan-server 8.2.6 and check 
MBean `jboss.datagrid-infinispan:type=Cache,name="default(dist_sync)",manager="clustered",component=Statistics`
that it has 3 items (numberOfEntries)

6) Go to RollingUpgrade in `jconsole` and run operation `recordKnownGlobalKeyset` .

7) Check statistics again. It should have 4 entries now.

8) Disconnect `jconsole` from infinispan 8.2.6 and connect rather to infinispan-server 9.1.4 and 
check statistics, that there are 0 entries in `default` cache.

9) Go to `RollingUpgradesManager` and run operation `synchronizeData` with parameter `hotrod` .

10) Check statistics, that there are 4 entries now.

11) Go back to RollingUpgradeManager and run `disconnectSource` with parameter `hotrod` .

12) Run `RemoteIspnTestV2` class with `-Djdg.port=14232` . Then run `list` in it for list items.
The 4 items should be there including `___MigrationManager_HotRod_KnownKeys___` .

NOTE: The `recordKnownGlobalKeyset` was tried as workaround to https://issues.jboss.org/browse/ISPN-8719 .
But looks that it may not be needed at all. So it's probably better to try alternativer 2.

Rolling upgrade servers with HotRod - alternative 2
---------------------------------------------------

1) Run points 1,2,3,4 from alternative 1. But in point 2, make sure that 
protocol is configured with compatibility version 2.5 


    <distributed-cache name="default">
        <remote-store cache="default" socket-timeout="60000" tcp-no-delay="true" shared="true" 
            raw-values="true" hotrod-wrapping="true" purge="false" passivation="false" 
            protocol-version="2.5">
                <remote-server outbound-socket-binding="remote-store-hotrod-server"/>
        </remote-store>
    </distributed-cache>  


The 2.5 should be version for infinispan-server 8 (or JDG 7.1). 
See https://access.redhat.com/documentation/en-us/red_hat_jboss_data_grid/7.1/html/administration_and_configuration_guide/rolling_upgrades
From this version, there is no need for use `recordKnownGlobalKeyset` as it's possible to iterate
through remoteCache. See source code of infinispan 9.1.4 and methods `HotRodTargetMigrator#synchronizeData` and `HotRodMigratorHelper#supportsIteration` .

2) Skip points 5,6,7 from above (no need to connect to infinispan-server 8.2.6 console)

3) Just use the points 8-12 from above. There is difference, that there will be just 3 entries comparing to alternative 1. 



  
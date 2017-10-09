package org.jboss;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class KeycloakJMXHelper {

    /* CONSUMER CODE EXAMPLE - requires 2 infinispan servers running (one with port offset 1010 and second with port offset 2010)

        JmxHelper helper1 = new JmxHelper("service:jmx:remote+http://localhost:11000");
        JmxHelper helper2 = new JmxHelper("service:jmx:remote+http://localhost:12000");

        for (int i=0 ; i<20 ; i++) {
            RemoteCache remoteCache = InfinispanUtil.getRemoteCache(actionKeyCache);
            ActionTokenReducedKey tokeKey = new ActionTokenReducedKey(key.getUserId(), key.getActionId(), UUID.randomUUID());
            remoteCache.put(tokeKey, tokenValue);
            LOG.infof("Iteration: %d, stats1: %s, stats2: %s", i, helper1.dumpStats(), helper2.dumpStats());

        }

        LOG.infof("FINAL");
     */
    private final MBeanServerConnection mbsc;
    private final ObjectName objectName;

    public KeycloakJMXHelper(String urll) {
        try {
            JMXServiceURL url = new JMXServiceURL(urll);
            JMXConnector conn = JMXConnectorFactory.newJMXConnector(url, null);
            conn.connect();

            mbsc = conn.getMBeanServerConnection();
            objectName = new ObjectName("jboss.datagrid-infinispan:type=channel,cluster=\"clustered\"");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String dumpStats() {
        Map<String, Object> stats = getStatistics();
        String sent = stats.get("sent_messages").toString();
        String received = stats.get("received_messages").toString();
        return "sent_messages: " + sent + ", received_messages: " + received;
    }



    public Map<String, Object> getStatistics() {
        try {
            MBeanInfo mBeanInfo = mbsc.getMBeanInfo(objectName);
            String[] statAttrs = Arrays.asList(mBeanInfo.getAttributes()).stream()
                    .filter(MBeanAttributeInfo::isReadable)
                    .map(MBeanAttributeInfo::getName)
                    .collect(Collectors.toList())
                    .toArray(new String[] {});
            return mbsc.getAttributes(objectName, statAttrs)
                    .asList()
                    .stream()
                    .collect(Collectors.toMap(Attribute::getName, Attribute::getValue));
        } catch (IOException | InstanceNotFoundException | ReflectionException | IntrospectionException ex) {
            throw new RuntimeException(ex);
        }
    }
}

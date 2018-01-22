package org.mposolda.ispn.v1;

import java.util.Date;

import org.infinispan.Cache;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CreateSessionCommand extends AbstractCommand<String, UserSessionEntity> {

    @Override
    public String getName() {
        return "create";
    }

    @Override
    protected void doRunCommand(Cache<String, UserSessionEntity> cache) {
        UserSessionEntity userSession = new UserSessionEntity();
        String id = getArg(0);

        userSession.setId(id);
        userSession.setRealmId("foo");

        userSession.setLastSessionRefresh((int) (new Date().getTime() / 1000));
        cache.put(id, userSession);
        log.infof("Created: %s=%s", id, userSession);
    }

    @Override
    public String printUsage() {
        return super.printUsage() + " <id>";
    }
}

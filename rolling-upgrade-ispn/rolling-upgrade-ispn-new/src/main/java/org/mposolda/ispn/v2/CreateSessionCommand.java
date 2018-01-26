package org.mposolda.ispn.v2;

import java.util.Date;

import org.infinispan.Cache;
import org.mposolda.ispn.entity.UserSessionEntity;

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

        int started = (int) (new Date().getTime() / 1000);
        userSession.setStarted(started);
        userSession.setLastSessionRefresh(String.valueOf(started + 100));

        cache.put(id, userSession);
        log.infof("Created: %s=%s", id, userSession);
    }

    @Override
    public String printUsage() {
        return super.printUsage() + " <id>";
    }
}

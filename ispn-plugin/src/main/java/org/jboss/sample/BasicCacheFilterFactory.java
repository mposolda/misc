package org.jboss.sample;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.List;

import org.infinispan.Cache;
import org.infinispan.cache.impl.DecoratedCache;
import org.infinispan.commands.VisitableCommand;
import org.infinispan.configuration.cache.BackupConfiguration;
import org.infinispan.context.Flag;
import org.infinispan.filter.NamedFactory;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.metadata.Metadata;
import org.infinispan.notifications.cachelistener.filter.CacheEventFilter;
import org.infinispan.notifications.cachelistener.filter.CacheEventFilterFactory;
import org.infinispan.notifications.cachelistener.filter.EventType;
import org.infinispan.xsite.BackupReceiver;
import org.infinispan.xsite.BackupReceiverRepository;
import org.infinispan.xsite.BackupReceiverRepositoryImpl;
import org.infinispan.xsite.BaseBackupReceiver;
import org.infinispan.xsite.statetransfer.XSiteStatePushCommand;
import org.infinispan.xsite.statetransfer.XSiteStateTransferControlCommand;

@NamedFactory(name = "basic-filter-factory")
public class BasicCacheFilterFactory implements CacheEventFilterFactory {

    public BasicCacheFilterFactory() {
        try {
            // TODO:mposolda JNDI
            EmbeddedCacheManager cacheMgr = (EmbeddedCacheManager) new javax.naming.InitialContext().lookup("java:jboss/infinispan/clustered");

            Cache sessions = cacheMgr.getCache("sessions");

            InterceptorInject.checkInterceptors(sessions);

            BackupReceiverRepository backupReceiverRepo = sessions.getAdvancedCache().getComponentRegistry().getComponent(BackupReceiverRepository.class);

            List<BackupConfiguration> backupSites = sessions.getAdvancedCache().getCacheConfiguration().sites().allBackups();

            for (BackupConfiguration backupCfg : backupSites) {
                String siteName = backupCfg.site();
                BackupReceiver origin = backupReceiverRepo.getBackupReceiver(siteName, "sessions");

                // Not using "instanceof" just because of deploy/undeploy (different classloader and hence instanceof would fail)
                if (origin.getClass().getName().endsWith("DecoratedBackupReceiver")) {
                    System.err.println("Skip decorating as it's decorated already");
                } else {
                    decorateBackupReceiver((BackupReceiverRepositoryImpl) backupReceiverRepo, origin, siteName);
                    System.err.println("Decorated backupReceiver for site " + siteName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void decorateBackupReceiver(BackupReceiverRepositoryImpl repo, BackupReceiver origin, String siteName) {
        BackupReceiver newReceiver = new DecoratedBackupReceiver(origin);
        repo.replace(siteName, "sessions", newReceiver);
    }



    @Override
    public CacheEventFilter<String, Object> getFilter(final Object[] params) {
        return new BasicKeyValueFilter();
    }

    static class BasicKeyValueFilter implements CacheEventFilter<String, Object>, Serializable {

        @Override
        public boolean accept(String key, Object oldValue, Metadata oldMetadata, Object newValue, Metadata newMetadata, EventType eventType) {
            return true;
        }
    }


    static class DecoratedBackupReceiver implements BackupReceiver {

        private final BackupReceiver decorated;
        private volatile BaseBackupReceiver.BackupCacheUpdater decoratedSiteUpdater;

        public DecoratedBackupReceiver(BackupReceiver decorated) {
            this.decorated = decorated;
        }

        @Override
        public Cache getCache() {
            return decorated.getCache();
        }

        @Override
        public Object handleRemoteCommand(VisitableCommand command) throws Throwable {
            if (decoratedSiteUpdater == null) {
                synchronized (this) {
                    if (decoratedSiteUpdater == null) {
                        Cache decoratedCache = new DecoratedCache(getCache().getAdvancedCache(), Flag.ZERO_LOCK_ACQUISITION_TIMEOUT);

                        Constructor<BaseBackupReceiver.BackupCacheUpdater> ctor = BaseBackupReceiver.BackupCacheUpdater.class.getDeclaredConstructor(Cache.class);
                        ctor.setAccessible(true);
                        decoratedSiteUpdater = ctor.newInstance(decoratedCache);
                    }
                }
            }

            return command.acceptVisitor(null, decoratedSiteUpdater);
        }

        @Override
        public void handleStateTransferControl(XSiteStateTransferControlCommand command) throws Exception {
            decorated.handleStateTransferControl(command);
        }

        @Override
        public void handleStateTransferState(XSiteStatePushCommand cmd) throws Exception {
            decorated.handleStateTransferState(cmd);
        }

    }
}
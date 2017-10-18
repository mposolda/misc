package org.jboss.sample;

import java.util.List;

import org.infinispan.Cache;
import org.infinispan.commands.LocalFlagAffectedCommand;
import org.infinispan.commands.VisitableCommand;
import org.infinispan.commons.CacheException;
import org.infinispan.context.Flag;
import org.infinispan.context.InvocationContext;
import org.infinispan.interceptors.InvocationContextInterceptor;
import org.infinispan.interceptors.base.CommandInterceptor;
import org.infinispan.util.concurrent.TimeoutException;
import org.infinispan.xsite.BackupFailureException;

/**
 * Workaround for infinispan always logging the exception in InvocationContextInterceptor
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class InterceptorInject {

    public static void checkInterceptors(Cache cache) {
        List<CommandInterceptor> interceptors = cache.getAdvancedCache().getInterceptorChain();

        boolean found = false;
        for (CommandInterceptor interceptor : interceptors) {
            if (interceptor.getClass().getName().endsWith("BeforeInvocationContextInterceptor")) {
                found = true;
                break;
            }
        }

        if (!found) {
            System.err.println("Injecting custom interceptors to cache: " + cache.getName());
            injectInterceptors(cache);
        } else {
            System.err.println("Interceptors already injected. Skip injecting");
        }

    }


    private static void injectInterceptors(Cache cache) {
        ThreadLocal exceptionHolder = new ThreadLocal<CacheException>();
        cache.getAdvancedCache().addInterceptorBefore(new BeforeInvocationContextInterceptor(exceptionHolder), InvocationContextInterceptor.class);
        cache.getAdvancedCache().addInterceptorAfter(new AfterInvocationContextInterceptor(exceptionHolder), InvocationContextInterceptor.class);
    }


    public static class AfterInvocationContextInterceptor extends CommandInterceptor {

        private final ThreadLocal<CacheException> exceptionHolder;

        AfterInvocationContextInterceptor(ThreadLocal<CacheException> exceptionHolder) {
            this.exceptionHolder = exceptionHolder;
        }


        @Override
        protected Object handleDefault(InvocationContext ctx, VisitableCommand command) throws Throwable {
            try {
                return invokeNextInterceptor(ctx, command);
            } catch (BackupFailureException backupFailure) {
                // Failure in BackupSender
                System.err.println("Exception handled: " + backupFailure.toString());
                exceptionHolder.set(backupFailure);
                return null;
            } catch (TimeoutException te) {
                // Failure in BackupReceiver due the lock already acquired by someone else
                if (command instanceof LocalFlagAffectedCommand) {
                    boolean hasZeroLockAcquisitionTimeout = ((LocalFlagAffectedCommand) command).hasFlag(Flag.ZERO_LOCK_ACQUISITION_TIMEOUT);
                    if (hasZeroLockAcquisitionTimeout) {
                        //System.err.println("Exception handled: " + te.getMessage());
                        exceptionHolder.set(te);
                        return null;
                    }
                }

                throw te;
            }
        }
    }


    public static class BeforeInvocationContextInterceptor extends CommandInterceptor {

        private final ThreadLocal<CacheException> exceptionHolder;

        BeforeInvocationContextInterceptor(ThreadLocal<CacheException> exceptionHolder) {
            this.exceptionHolder = exceptionHolder;
        }


        @Override
        protected Object handleDefault(InvocationContext ctx, VisitableCommand command) throws Throwable {
            try {
                Object toReturn = invokeNextInterceptor(ctx, command);

                // Re-throw the exception set by AfterInvocationContextInterceptor
                CacheException ex = exceptionHolder.get();
                if (ex != null) {
                    throw ex;
                }

                return toReturn;
            } finally {
                exceptionHolder.set(null);
            }
        }
    }
}

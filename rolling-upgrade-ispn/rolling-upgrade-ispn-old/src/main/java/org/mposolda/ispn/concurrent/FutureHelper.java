package org.mposolda.ispn.concurrent;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.infinispan.commons.util.concurrent.FutureListener;
import org.infinispan.commons.util.concurrent.NotifyingFuture;
import org.infinispan.util.concurrent.ConcurrentHashSet;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
class FutureHelper {

    private static final int LIMIT = 100;

    private Set<Future> futures = new ConcurrentHashSet<>();

    // Performance optimization - just to avoid calling futures.size() continuously
    private AtomicInteger counter = new AtomicInteger(0);


    // Decide if task should be run synchronously or asynchronously based on the queue size
    public void registerTask(Callable<NotifyingFuture> asyncTask, Runnable syncTask) {
        if (counter.get() > LIMIT) {
            // Just run synchronously
            //System.out.println("sync run");
            syncTask.run();
        } else {
            // Register and run asynchronously
            //System.out.println("async run. Counter: " + counter.get());
            try {
                NotifyingFuture future = asyncTask.call();
                registerFuture(future);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void registerFuture(NotifyingFuture f) {
        counter.incrementAndGet();
        futures.add(f);

        f.attachListener(new FutureListener() {

            @Override
            public void futureDone(Future future) {
                futures.remove(f);
                counter.decrementAndGet();
            }

        });
    }


    public void waitForAllToFinish() {
        while (futures.iterator().hasNext()) {
            try {
                System.out.println("waitForAllToFinish: Counter: " + counter.get());

                Future f = futures.iterator().next();
                f.get();
                // Just very short sleep to avoid checking same future many times.
                // 2 ms should ensure that listener will be invoked in the meantime and remove it from collection)
                Thread.sleep(2);

            } catch (NoSuchElementException nsee) {
                // Could happen if "hasNext" returns true, but "next()" will throw NSEE due the element removed by the listener in the other thread in the meantime
            } catch (InterruptedException | ExecutionException ee) {
                throw new RuntimeException(ee);
            }
        }
    }

}

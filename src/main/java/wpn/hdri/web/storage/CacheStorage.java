/*
 * The main contributor to this project is Institute of Materials Research,
 * Helmholtz-Zentrum Geesthacht,
 * Germany.
 *
 * This project is a contribution of the Helmholtz Association Centres and
 * Technische Universitaet Muenchen to the ESS Design Update Phase.
 *
 * The project's funding reference is FKZ05E11CG1.
 *
 * Copyright (c) 2012. Institute of Materials Research,
 * Helmholtz-Zentrum Geesthacht,
 * Germany.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package wpn.hdri.web.storage;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.collections.keyvalue.UnmodifiableMapEntry;
import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.data.DataSets;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static wpn.hdri.web.data.Users.User;

/**
 * Wraps {@link Storage}. Actual type of <code>T</code> should implement .equals & .hashCode properly.
 * <p/>
 * Guarantees that {@link this#load(wpn.hdri.web.data.Users.User, String, wpn.hdri.web.ApplicationContext)}
 * will be called only once on the underlying Storage for the same key.
 * <p/>
 * Guarantees that {@link this#save(Object, wpn.hdri.web.data.Users.User, String, wpn.hdri.web.ApplicationContext)}
 * will always store new value. This is a workaround for {@link DataSets#update(org.apache.commons.beanutils.DynaBean, wpn.hdri.web.data.DataSet)}
 * method.
 * <p/>
 * Implementation is ThreadSafe in terms that the newest value will be always returned. Since {@link ConcurrentMap} is being wrapped
 * by this class it provides the same contract for ThreadSafety as {@link ConcurrentHashMap}.
 * <p/>
 * By default this Cache will clean itself once per day.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 27.03.12
 */
@ThreadSafe
public class CacheStorage<T> implements Storage<T> {
    private final ConcurrentMap<UnmodifiableMapEntry, Future<T>> cache = new ConcurrentHashMap<UnmodifiableMapEntry, Future<T>>();
    private final Set<T> cachedValues = new HashSet<T>();

    private final Storage<T> wrapped;

    //use low priority daemon thread to clean up Cache once per day
    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        private final ThreadFactory wrapped = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(Runnable r) {
            Thread t = wrapped.newThread(r);
            //do not allow this thread to prevent JVM from shutdown
            t.setDaemon(true);
            t.setName(CacheStorage.this + " cleaner.");
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    });

    public CacheStorage(Storage<T> wrapped) {
        this(wrapped, 1, 1, TimeUnit.DAYS);
    }

    /**
     * For internal use only
     *
     * @param wrapped
     * @param cleanupInitialDelay
     * @param cleanupPeriodicDelay
     * @param timeUnit
     */
    CacheStorage(Storage<T> wrapped, long cleanupInitialDelay, long cleanupPeriodicDelay, TimeUnit timeUnit) {
        this.wrapped = wrapped;

        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (cachedValues) {
                    cachedValues.clear();
                }
                cache.clear();
            }
        }, cleanupInitialDelay, cleanupPeriodicDelay, timeUnit);
    }

    /**
     * Always saves data.
     *
     * @param data
     * @param user
     * @param dataSetName
     * @param ctx
     * @throws StorageException
     */
    public void save(final T data, final User user, final String dataSetName, final ApplicationContext ctx) throws StorageException {
        if (data == null) {
            throw new NullPointerException("data can not be null.");
        }
        //if data is already cached no need to save it
        synchronized (cachedValues) {
            if (!cachedValues.add(data)) return;
        }

        FutureTask<T> futureTask = new FutureTask<T>(new Callable<T>() {
            @Override
            public T call() throws Exception {
                //TODO this blocks get method, but we are able to handle exception properly,
                //TODO using Runnable here won't block get method, but run does not throw any Exception
                wrapped.save(data, user, dataSetName, ctx);
                return data;
            }
        });

        cache.put(new UnmodifiableMapEntry(user, dataSetName), futureTask);
        futureTask.run();
    }

    public T load(final User user, final String dataSetName, final ApplicationContext ctx) throws StorageException {
        UnmodifiableMapEntry key = new UnmodifiableMapEntry(user, dataSetName);
        while (true) {
            Future<T> f = cache.get(key);
            if (f == null) {
                FutureTask<T> ft = new FutureTask<T>(new Callable<T>() {
                    @Override
                    public T call() throws Exception {
                        try {
                            return wrapped.load(user, dataSetName, ctx);
                        } catch (StorageException e) {
                            throw new Exception(e);
                        }
                    }
                });

                f = cache.putIfAbsent(key, ft);
                if (f == null) {
                    f = ft;
                    ft.run();
                }
            }
            try {
                T data = f.get();
                return data;
            } catch (CancellationException e) {
                cache.remove(key, f);
            } catch (ExecutionException e) {
                throw launderThrowable(e.getCause());
            } catch (InterruptedException e) {
                throw new StorageException("Load attempt interrupted.", e);
            }
        }
    }

    /**
     * If the Throwable is an Error, throw it; if it is a
     * RuntimeException return it, otherwise throw IllegalStateException
     *
     * @param t cause
     * @return RuntimeException if t is a RuntimeException, Error - if Error otherwise IllegalStateException
     */
    private static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException)
            return (RuntimeException) t;
        else if (t instanceof Error)
            throw (Error) t;
        else
            throw new IllegalStateException("Not unchecked", t);
    }

    /**
     * A weak hope to shutdown {@link this#exec} during finalization of this instance.
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        exec.shutdownNow();

        super.finalize();
    }

    @Override
    public void close() {
        exec.shutdown();
    }
}

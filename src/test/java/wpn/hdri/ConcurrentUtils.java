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

package wpn.hdri;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Concurrent tests helper class.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 28.03.12
 */
public class ConcurrentUtils {
    public static final int CPUS = Runtime.getRuntime().availableProcessors() < 2 ? 2 : Runtime.getRuntime().availableProcessors();

    private ConcurrentUtils() {
    }


    public static void testConcurrently(final Method method, final Object instance, final Object... args) throws Exception {
        ExecutorService exec = Executors.newFixedThreadPool(CPUS);

        final CountDownLatch finish = new CountDownLatch(CPUS);
        for (final CountDownLatch start = new CountDownLatch(CPUS);
             start.getCount() > 0;
             start.countDown()) {
            exec.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        start.await();

                        method.invoke(instance, args);

                        finish.countDown();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        finish.await();


        exec.shutdownNow();
    }

    public static void testConcurrently(final Runnable task) throws Exception {
        ExecutorService exec = Executors.newFixedThreadPool(CPUS);

        final CountDownLatch finish = new CountDownLatch(CPUS);
        for (final CountDownLatch start = new CountDownLatch(CPUS);
             start.getCount() > 0;
             start.countDown()) {
            exec.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        start.await();

                        task.run();

                        finish.countDown();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        finish.await();


        exec.shutdownNow();
    }
}

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

package wpn.hdri.web.frontend;

import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.ApplicationProperties;
import wpn.hdri.web.backend.ApplicationServlet;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * Tango frontend servlet. Initializes JsonDS Tango server.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 21.02.12
 */
public class TangoServlet extends GenericServlet {
    private final ExecutorService exec;
    public static final String APPLICATION_CONTEXT = "TangoServer.context";
    public static final String STORAGE = "TangoServer.storage";

    TangoServlet(ExecutorService exec) {
        this.exec = exec;
    }

    /**
     * This is used by servlet container
     */
    public TangoServlet() {
        this(Executors.newSingleThreadExecutor(new ThreadFactory() {
            private final ThreadFactory factory = Executors.defaultThreadFactory();

            public Thread newThread(Runnable r) {
                Thread t = factory.newThread(r);
                //prevent this thread from locking JVM
                t.setDaemon(true);
                t.setName("PreExperimentDataCollector Tango frontend");
                return t;
            }
        }));
    }

    private volatile Future<?> backgroundTask;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        ApplicationContext applicationContext = (ApplicationContext) config.getServletContext().getAttribute(ApplicationServlet.APPLICATION_CONTEXT);
        //TODO we should consider how to avoid this dirty hack with System.property
        System.getProperties().put(APPLICATION_CONTEXT, applicationContext);

        ApplicationProperties properties = applicationContext.getApplicationProperties();

        String tangoServerName = properties.tangoServerClassName;
        String tangoInstanceName = properties.tangoServerInstanceName;

        String[] tangoServerArguments = properties.tangoServerArguments.isEmpty() ? new String[0] : properties.tangoServerArguments.split(",");

        backgroundTask = exec.submit(new TangoDevice(tangoServerName, tangoInstanceName, tangoServerArguments));
    }


    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        PrintWriter writer = res.getWriter();
        writer.write("Tango server is alive:");
        writer.write(String.valueOf(backgroundTask != null && !backgroundTask.isDone() && !backgroundTask.isCancelled()));
    }

    public String getServletInfo() {
        return "TangoServlet runs Tango server.";
    }

    public void destroy() {
        backgroundTask.cancel(true);
        exec.shutdownNow();
    }
}

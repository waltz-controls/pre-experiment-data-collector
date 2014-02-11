package hzg.wpn.hdri.predator;

import hzg.wpn.hdri.predator.data.DataSetsManager;
import hzg.wpn.hdri.predator.frontend.TangoDevice;
import hzg.wpn.hdri.predator.meta.Meta;
import hzg.wpn.hdri.predator.storage.SimpleSerializationStorage;
import hzg.wpn.hdri.predator.storage.Storage;
import hzg.wpn.util.properties.PropertiesFactory;
import hzg.wpn.util.properties.PropertiesHelper;
import org.apache.commons.beanutils.DynaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tango.server.ServerManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.01.14
 */
public class ApplicationLoader implements ServletContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationLoader.class);

    public static final String WEB_INF = "WEB-INF/";
    public static final String LOGIN_PROPERTIES = WEB_INF + "login.properties";
    public static final String APPLICATION_PROPERTIES = WEB_INF + "application.properties";
    public static final String META_YAML = WEB_INF + "meta.yaml";
    public static final String HOME = "home";

    private final ExecutorService exec = Executors.newSingleThreadExecutor(new ThreadFactory() {
        private final ThreadFactory factory = Executors.defaultThreadFactory();

        public Thread newThread(Runnable r) {
            Thread t = factory.newThread(r);
            //prevent this thread from locking JVM
            t.setDaemon(true);
            t.setName("PreExperimentDataCollector Tango frontend");
            return t;
        }
    });

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String realPath = sce.getServletContext().getRealPath("/");

        initializeLoginProperties(realPath);

        ApplicationProperties appProperties = initializeApplicationProperties(realPath);

        ApplicationContext context = initializeApplicationContext(appProperties, sce.getServletContext());
        sce.getServletContext().setAttribute(ApplicationContext.APPLICATION_CONTEXT, context);

        //TODO avoid this hack
        TangoDevice.setStaticContext(context);

        initializeTangoFrontend(appProperties);
    }

    private void initializeTangoFrontend(ApplicationProperties appProperties) {
        final String tangoServerName = appProperties.tangoServerClassName;
        final String tangoInstanceName = appProperties.tangoServerInstanceName;
        final String[] tangoServerArguments = appProperties.tangoServerArguments.isEmpty() ? new String[0] : appProperties.tangoServerArguments.split(",");
        final String[] args = new String[tangoServerArguments.length + 1];
        args[0] = tangoInstanceName;
        System.arraycopy(tangoServerArguments,0,args,1,tangoServerArguments.length);

        exec.submit(new Runnable() {
            @Override
            public void run() {
                ServerManager.getInstance().addClass(tangoServerName,TangoDevice.class);
                ServerManager.getInstance().start(args,tangoServerName);
            }
        });
    }

    private ApplicationProperties initializeApplicationProperties(String realPath) {
        try {
            Properties properties = PropertiesHelper.loadProperties(Paths.get(realPath, APPLICATION_PROPERTIES));

            PropertiesFactory<ApplicationProperties> factory = new PropertiesFactory<>(properties, ApplicationProperties.class);
            ApplicationProperties appProperties = factory.createType();
            return appProperties;
        } catch (Exception e) {
            LOG.error("Cannot initialize application properties",e);
            throw new RuntimeException(e);
        }
    }

    private void initializeLoginProperties(String realPath) {
        try {
            Properties loginProperties = PropertiesHelper.loadProperties(Paths.get(realPath, LOGIN_PROPERTIES));

            for (Map.Entry<Object, Object> entry : loginProperties.entrySet()) {
                System.getProperties().put(entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            LOG.error("Cannot initialize login.properties", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes {@link ApplicationContext} and puts it into ServletContext
     *
     * @param appProperties
     * @param servletContext
     */
    private ApplicationContext initializeApplicationContext(ApplicationProperties appProperties, ServletContext servletContext) {
        String realPath = servletContext.getRealPath("/");
        String contextPath = servletContext.getContextPath();
        try {

            String beamtimeId = appProperties.beamtimeId;

            Storage storage = new SimpleSerializationStorage();

            Meta meta = new Meta(Paths.get(realPath, META_YAML));
            DynaClass dataClass = meta.extractDynaClass();


            DataSetsManager manager = new DataSetsManager(beamtimeId, Paths.get(realPath, HOME), dataClass, storage);

            ApplicationContext context = new ApplicationContext(realPath, contextPath, beamtimeId, storage, appProperties, meta, dataClass, manager);

            return context;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("Context destroyed");
        exec.shutdownNow();
    }
}

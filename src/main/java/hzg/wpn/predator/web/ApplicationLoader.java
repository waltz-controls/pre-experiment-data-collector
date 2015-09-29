package hzg.wpn.predator.web;

import hzg.wpn.predator.ApplicationContext;
import hzg.wpn.predator.meta.Meta;
import hzg.wpn.tango.PreExperimentDataCollector;
import hzg.wpn.predator.storage.SimpleSerializationStorage;
import hzg.wpn.predator.storage.Storage;
import hzg.wpn.properties.PropertiesParser;
import hzg.wpn.xenv.ResourceManager;
import org.apache.commons.beanutils.DynaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tango.server.ServerManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
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
    public static final String APPLICATION_CONTEXT = "predator.context";
    public static final String JMVC_ROOT = "jmvc_root/";
    public static final String LOGIN_PROPERTIES = "login.properties";
    public static final String APPLICATION_PROPERTIES = "application.properties";
    public static final String META_YAML = "meta.yaml";
    public static final String ETC_PRE_EXPERIMENT_DATA_COLLECTOR = "etc/PreExperimentDataCollector";
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationLoader.class);
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
        initializeLoginProperties();

        ApplicationProperties appProperties = initializeApplicationProperties();

        ApplicationContext context = initializeApplicationContext(appProperties, sce.getServletContext());
        sce.getServletContext().setAttribute(APPLICATION_CONTEXT, context);

        //TODO avoid this hack
        PreExperimentDataCollector.setStaticContext(context);

        initializeTangoFrontend(appProperties);
    }

    private void initializeTangoFrontend(ApplicationProperties appProperties) {
        final String tangoServerName = appProperties.tangoServerClassName;
        final String tangoInstanceName = appProperties.tangoServerInstanceName;
        final String[] tangoServerArguments = appProperties.tangoServerArguments.isEmpty() ? new String[0] : appProperties.tangoServerArguments.split(",");
        final String[] args = new String[tangoServerArguments.length + 1];
        args[0] = tangoInstanceName;
        System.arraycopy(tangoServerArguments, 0, args, 1, tangoServerArguments.length);

        exec.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Context initCtx = new InitialContext();
                    Context envCtx = (Context) initCtx.lookup("java:comp/env");

                    //TODO get rid off this mess
                    String tmp = System.getProperty("TANGO_HOST");
                    String tango_host = (String) envCtx.lookup("TANGO_HOST");
                    System.setProperty("TANGO_HOST", tango_host);

                    ServerManager.getInstance().addClass(tangoServerName, PreExperimentDataCollector.class);
                    ServerManager.getInstance().start(args, tangoServerName);
                    System.setProperty("TANGO_HOST", tmp);
                } catch (NamingException e) {
                    LOG.error(e.getLocalizedMessage());
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private ApplicationProperties initializeApplicationProperties() {
        try {
            Properties properties = ResourceManager.loadProperties(ETC_PRE_EXPERIMENT_DATA_COLLECTOR, APPLICATION_PROPERTIES);

            PropertiesParser<ApplicationProperties> factory = PropertiesParser.createInstance(ApplicationProperties.class);
            ApplicationProperties appProperties = factory.parseProperties(properties);
            return appProperties;
        } catch (Exception e) {
            LOG.error("Cannot initialize application properties", e);
            throw new RuntimeException(e);
        }
    }

    private void initializeLoginProperties() {
        try {
            Properties loginProperties = ResourceManager.loadProperties(ETC_PRE_EXPERIMENT_DATA_COLLECTOR, LOGIN_PROPERTIES);

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

            Meta meta = new Meta(ResourceManager.loadResource(ETC_PRE_EXPERIMENT_DATA_COLLECTOR, META_YAML));
            DynaClass dataClass = meta.extractDynaClass();

            ApplicationContext context = new ApplicationContext(realPath, contextPath, beamtimeId, storage, appProperties, meta, dataClass);

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

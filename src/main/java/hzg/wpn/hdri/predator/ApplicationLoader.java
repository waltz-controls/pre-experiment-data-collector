package hzg.wpn.hdri.predator;

import hzg.wpn.hdri.predator.meta.Meta;
import hzg.wpn.util.properties.PropertiesFactory;
import hzg.wpn.util.properties.PropertiesHelper;
import org.apache.commons.beanutils.DynaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wpn.hdri.web.ApplicationProperties;
import wpn.hdri.web.storage.SimpleSerializationStorage;
import wpn.hdri.web.storage.Storage;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.01.14
 */
public class ApplicationLoader implements ServletContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationLoader.class);

    public static final String APPLICATION_CONTEXT = "predator.context";
    public static final String WEB_INF = "WEB-INF/";
    public static final String LOGIN_PROPERTIES = WEB_INF + "login.properties";
    public static final String APPLICATION_PROPERTIES = WEB_INF + "application.properties";
    public static final String META_YAML = WEB_INF + "meta.yaml";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initializeLoginProperties();

        initializeApplicationContext(sce.getServletContext());
    }

    private void initializeLoginProperties() {
        try {
            Properties loginProperties = PropertiesHelper.loadProperties(Paths.get(LOGIN_PROPERTIES));

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
     * @param servletContext
     */
    private void initializeApplicationContext(ServletContext servletContext) {
        try {
            Properties properties = PropertiesHelper.loadProperties(Paths.get(APPLICATION_PROPERTIES));

            PropertiesFactory<ApplicationProperties> factory = new PropertiesFactory<ApplicationProperties>(properties, ApplicationProperties.class);
            ApplicationProperties appProperties = factory.createType();

            String beamtimeId = appProperties.beamtimeId;

            Storage storage = new SimpleSerializationStorage();

            Meta meta = new Meta(Paths.get(META_YAML));
            DynaClass dataClass = meta.extractDynaClass();
            String realPath = servletContext.getRealPath("/");
            String contextPath = servletContext.getContextPath();

            ApplicationContext context = new ApplicationContext(realPath, contextPath, beamtimeId, storage, appProperties, meta, dataClass);
            servletContext.setAttribute(APPLICATION_CONTEXT, context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("Context destroyed");
    }
}

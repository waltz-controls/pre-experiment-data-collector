package hzg.wpn.predator.web;

import de.hzg.wpi.utils.authorization.Kerberos;
import hzg.wpn.predator.ApplicationContext;
import hzg.wpn.predator.meta.Meta;
import hzg.wpn.predator.storage.SimpleSerializationStorage;
import hzg.wpn.predator.storage.Storage;
import hzg.wpn.properties.PropertiesParser;
import hzg.wpn.tango.PreExperimentDataCollector;
import hzg.wpn.xenv.ResourceManager;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.01.14
 */
public class ApplicationLoader implements ServletContextListener {
    public static final String APPLICATION_CONTEXT = "predator.context";
    public static final String LOGIN_PROPERTIES = "login.properties";
    public static final String APPLICATION_PROPERTIES = "application.properties";
    public static final String META_YAML = "meta.yaml";
    public static final String ETC_PRE_EXPERIMENT_DATA_COLLECTOR = "etc/PreExperimentDataCollector";
    public static final String XENV_ROOT;
    public static final String VAR_PREDATOR_ROOT_WAR = "var/predator/ROOT.war";
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationLoader.class);

    static {
        Converter integerConverter =
                new IntegerConverter();
        ConvertUtils.register(integerConverter, Integer.TYPE);    // Native type
        ConvertUtils.register(integerConverter, Integer.class);   // Wrapper class
    }

    static {
        String xenv_rootProperty = System.getProperty("XENV_ROOT", System.getenv("XENV_ROOT"));
        XENV_ROOT = xenv_rootProperty == null ? "." : xenv_rootProperty;
        LOG.info("XENV_ROOT={}", XENV_ROOT);
    }

    public static void initializeLoginProperties(Tomcat tomcat) {
        try {
            Properties loginProperties = ResourceManager.loadProperties(ETC_PRE_EXPERIMENT_DATA_COLLECTOR, LOGIN_PROPERTIES);

            boolean useKerberos = Boolean.parseBoolean(loginProperties.getProperty("predator.tomcat.use.kerberos"));
            if (useKerberos) {
                Kerberos kerberos = new Kerberos();
                kerberos.configure();
            } else {
                String userName = loginProperties.getProperty("predator.tomcat.user.name");
                String userPass = loginProperties.getProperty("predator.tomcat.user.pass");

                tomcat.addUser(userName, userPass);
                tomcat.addRole(userName, "user");
            }
        } catch (IOException e) {
            LOG.error("Cannot initialize login.properties", e);
            throw new RuntimeException(e);
        }
    }

    public static void initializeWebapp(Tomcat tomcat) {
        try {
            //create tomcat's basedir
            Path tomcatBasedir = Paths.get(XENV_ROOT, "var/predator/tomcat");
            if(Files.notExists(tomcatBasedir)) Files.createDirectories(Paths.get(XENV_ROOT,"var/predator/tomcat/webapps"));
            tomcat.setBaseDir(tomcatBasedir.toAbsolutePath().toString());
        } catch (IOException e) {
            LOG.error("Unable to create tomcat's basedir.", e);
            throw new RuntimeException("Unable to create tomcat's basedir.", e);
        }

        try {
            extractWebapp();
        } catch (IOException e) {
            LOG.error("Unable to extract webapp.", e);
            throw new RuntimeException("Unable to extract webapp.", e);
        }

        String webapp = Paths.get(XENV_ROOT).resolve(VAR_PREDATOR_ROOT_WAR).toAbsolutePath().toString();

        try {
            LOG.info("Adding webapp {}", webapp);
            org.apache.catalina.Context context = tomcat.addWebapp("/", webapp);
            WebappLoader loader =
                    new WebappLoader(Thread.currentThread().getContextClassLoader());
            context.setLoader(loader);
        } catch (ServletException e) {
            LOG.error("Unable to add webapp to tomcat.", e);
            throw new RuntimeException("Unable to add webapp to tomcat.", e);
        }

    }

    private static void extractWebapp() throws IOException {
        InputStream webapp = PreExperimentDataCollector.class.getResourceAsStream("/ROOT.war");

        Path cwd = Paths.get(XENV_ROOT);

        Files.copy(webapp, Files.createDirectories(cwd.resolve("var/predator")).resolve("ROOT.war"), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {


        ApplicationProperties appProperties = initializeApplicationProperties();

        ApplicationContext context = initializeApplicationContext(appProperties, sce.getServletContext());
        sce.getServletContext().setAttribute(APPLICATION_CONTEXT, context);

        //TODO avoid this hack
        PreExperimentDataCollector.setStaticContext(context);
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
    }
}

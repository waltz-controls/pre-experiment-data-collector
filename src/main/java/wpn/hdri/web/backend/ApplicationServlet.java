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

package wpn.hdri.web.backend;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wpn.hdri.properties.PropertiesFactory;
import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.ApplicationProperties;
import wpn.hdri.web.data.BeamtimeId;
import wpn.hdri.web.meta.MetaDataHelpers;
import wpn.hdri.web.storage.CacheStorage;
import wpn.hdri.web.storage.Storage;
import wpn.hdri.web.storage.StorageFactory;

import javax.servlet.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Main servlet of the web application.
 * <p/>
 * Provides access to the {@link ApplicationContext}, {@link Storage} and application.properties
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 08.03.12
 */
public final class ApplicationServlet extends GenericServlet {
    private static final Logger log = LoggerFactory.getLogger(ApplicationServlet.class);

    public static final String APPLICATION_CONTEXT = ApplicationContext.class.getSimpleName() + ".context";


    private Storage<DynaBean> storage;

    /**
     * Initializes essential for application workflow items and places them into {@link javax.servlet.ServletContext}
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        initializeLoginProperties();

        ApplicationContext context = initializeApplicationContext(config);

        config.getServletContext().setAttribute(APPLICATION_CONTEXT, context);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        //TODO print useful information
    }

    private ApplicationContext initializeApplicationContext(ServletConfig config) throws ServletException {
        ApplicationPropertiesInitializer initializer = new ApplicationPropertiesInitializer("/application.properties");
        ApplicationProperties properties = initializer.initializeApplicationProperties();

        BeamtimeId beamtimeId = new BeamtimeId(properties.beamtimeId);

        StorageInitializer<DynaBean> storageInitializer = new StorageInitializer<DynaBean>(properties.storageClassName,
                new StorageFactory(properties.storageClassName) {
                    @Override
                    public <DataSet> Storage<DataSet> createInstance() throws Exception {
                        return new CacheStorage<DataSet>(super.<DataSet>createInstance());
                    }
                });
        storage = storageInitializer.initializeStorage();

        //TODO customize helper through init params or application properties
        MetaDataHelpers helper = MetaDataHelpers.JSON_DEFAULT;
        String realPath = config.getServletContext().getRealPath("/");
        String contextPath = config.getServletContext().getContextPath();

        return new ApplicationContext(realPath, contextPath, beamtimeId, storage, properties, helper);
    }

    @Override
    public void destroy() {
        IOUtils.closeQuietly(storage);
        super.destroy();
    }

    private void initializeLoginProperties() throws ServletException {
        LoginPropertiesInitializer loginPropertiesInitializer = new LoginPropertiesInitializer("/login.properties");
        Properties loginProperties = loginPropertiesInitializer.initializeLoginProperties();

        for (Map.Entry<Object, Object> entry : loginProperties.entrySet()) {
            System.getProperties().put(entry.getKey(), entry.getValue());
        }
    }


    public static class ApplicationPropertiesInitializer {
        private final String propertiesFile;

        public ApplicationPropertiesInitializer(String propertiesFile) {
            this.propertiesFile = propertiesFile;
        }

        public ApplicationProperties initializeApplicationProperties() throws ServletException {
            InputStream inputStream = ApplicationServlet.class.getResourceAsStream(propertiesFile);
            try {
                Properties loginProperties = new Properties();
                loginProperties.load(inputStream);

                PropertiesFactory<ApplicationProperties> factory = new PropertiesFactory<ApplicationProperties>(loginProperties, ApplicationProperties.class);
                return factory.createType();
            } catch (Exception e) {
                log.error("An attempt to initialize properties from file[" + propertiesFile + "] failed.", e);
                throw new ServletException(e);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    public static class StorageInitializer<T> {
        private final String storageClassName;
        private final StorageFactory factory;

        public StorageInitializer(String storageClassName, StorageFactory factory) {
            this.storageClassName = storageClassName;
            this.factory = factory;
        }

        public Storage<T> initializeStorage() throws ServletException {
            try {
                return factory.createInstance();
            } catch (Exception e) {
                log.error("An attempt to create " + storageClassName + " failed.", e);
                throw new ServletException(e);
            }
        }
    }

    public static class LoginPropertiesInitializer {
        private final String loginPropertiesFile;

        public LoginPropertiesInitializer(String loginPropertiesFile) {
            this.loginPropertiesFile = loginPropertiesFile;
        }

        public Properties initializeLoginProperties() throws ServletException {
            try {
                Properties properties = new Properties();

                properties.load(this.getClass().getResourceAsStream(loginPropertiesFile));

                return properties;
            } catch (IOException e) {
                log.error("Can not read file " + loginPropertiesFile, e);
                throw new ServletException(e);
            }
        }
    }
}

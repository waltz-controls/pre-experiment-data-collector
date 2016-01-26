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

package hzg.wpn.tango;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import fr.esrf.Tango.AttrWriteType;
import fr.esrf.Tango.DevFailed;
import hzg.wpn.predator.ApplicationContext;
import hzg.wpn.predator.meta.Meta;
import hzg.wpn.util.beanutils.BeanUtilsHelper;
import org.apache.catalina.Context;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tango.DeviceState;
import org.tango.server.ServerManager;
import org.tango.server.StateMachineBehavior;
import org.tango.server.annotation.*;
import org.tango.server.attribute.AttributeConfiguration;
import org.tango.server.attribute.AttributeValue;
import org.tango.server.attribute.IAttributeBehavior;
import org.tango.server.dynamic.DynamicManager;
import org.tango.utils.DevFailedUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.jar.JarFile;

/**
 * Designed to be Thread condemned
 */
@Device
public class PreExperimentDataCollector {
    private static final Logger logger = LoggerFactory.getLogger(PreExperimentDataCollector.class);

    public static final String XENV_ROOT;

    static {
        String xenv_rootProperty = System.getProperty("XENV_ROOT", System.getenv("XENV_ROOT"));
        XENV_ROOT = xenv_rootProperty == null ? "." : xenv_rootProperty;
        logger.info("XENV_ROOT={}", XENV_ROOT);
    }

    public static final String VAR_PREDATOR_ROOT_WAR = "var/predator/ROOT.war";

    private static ApplicationContext APPLICATION_CONTEXT;
    private ApplicationContext appCtx;
    private volatile DynaBean data;
    @DynamicManagement
    private DynamicManager dynamic;

    public synchronized static void setStaticContext(ApplicationContext applicationContext) {
        APPLICATION_CONTEXT = applicationContext;
    }

    private static void extractWebapp() {
        try {
            InputStream webapp = PreExperimentDataCollector.class.getResourceAsStream("/ROOT.war");

            Path cwd = Paths.get(XENV_ROOT);

            Files.copy(webapp, Files.createDirectories(cwd.resolve("var/predator")).resolve("ROOT.war"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new Error("Unable to extract native library.", e);
        }
    }

    /**
     * Iterates over all users and loads their data sets
     *
     * @return
     * @throws Exception
     */
    @Command
    public String[] datasets() throws Exception {
        List<String> result = new ArrayList<>();

        //get all users
        Iterable<String> users = appCtx.getUsers();
        //add all data sets of each user
        for (String user : users) {
            Iterables.addAll(result, appCtx.getManager().getUserDataSetNames(user));
        }

        return result.toArray(new String[result.size()]);
    }

    @Command(inTypeDesc = "dataset_name")
    @StateMachine(endState = DeviceState.ON)
    public void delete_data_set(final String name) throws Exception {
        Iterable<String> users = appCtx.getUsers();

        DynaBean data = getDataSet(name, users);

        appCtx.getManager().delete(data);

        this.data = null;
    }

    private DynaBean getDataSet(final String name, Iterable<String> users) {
        DynaBean data = null;
        for (String user : users) {
            Optional<DynaBean> result = Iterables.tryFind(appCtx.getManager().getUserDataSets(user), new Predicate<DynaBean>() {
                @Override
                public boolean apply(@Nullable DynaBean input) {
                    return BeanUtilsHelper.getProperty(input, Meta.NAME, String.class).equals(name);
                }
            });
            if (result.isPresent()) {
                data = result.get();
                break;
            }
        }
        if (data == null)
            throw new NoSuchElementException("Dataset[" + name + "] can not be found!");

        return data;
    }

    @Command(inTypeDesc = "user_name;dataset_name")
    @StateMachine(endState = DeviceState.STANDBY)
    public void create_data_set(String[] args) throws Exception {
        if (args.length != 2)
            DevFailedUtils.throwDevFailed("Exactly 2 arguments are expected here: user name and data set name.");
        data = appCtx.getManager().newDataSet(args[0], args[1]);
        appCtx.getManager().save(data);
    }

    @Command(inTypeDesc = "dataset_name")
    @StateMachine(endState = DeviceState.STANDBY)
    public void load_data_set(final String name) throws Exception {
        //get all users
        Iterable<String> users = appCtx.getUsers();
        //add all data sets of each user
        this.data = getDataSet(name, users);
    }

    public void setDynamic(DynamicManager dynamic) {
        this.dynamic = dynamic;
    }

    @Init
    @StateMachine(endState = DeviceState.ON)
    public void init() throws Exception {
        this.appCtx = APPLICATION_CONTEXT;

        //populate attributes
        for (final DynaProperty dynaProperty : appCtx.getDataClass().getDynaProperties()) {
            dynamic.addAttribute(createNewAttribute(dynaProperty, appCtx));
        }
    }

    private IAttributeBehavior createNewAttribute(final DynaProperty dynaProperty, final ApplicationContext appCtx) {
        final StateMachineBehavior stateMachine = new StateMachineBehavior();
        stateMachine.setDeniedStates(DeviceState.ON);
        return new IAttributeBehavior() {
            @Override
            public AttributeConfiguration getConfiguration() throws DevFailed {
                AttributeConfiguration configuration = new AttributeConfiguration();
                configuration.setName(dynaProperty.getName());
                configuration.setType(dynaProperty.getType());
                configuration.setWritable(AttrWriteType.READ_WRITE);
                return configuration;
            }

            @Override
            public AttributeValue getValue() throws DevFailed {
                if (data == null) DevFailedUtils.throwDevFailed("data_set is null. load_data_set first.");
                return new AttributeValue(BeanUtilsHelper.getProperty(data, getConfiguration().getName(), getConfiguration().getType()));
            }

            @Override
            public void setValue(AttributeValue value) throws DevFailed {
                data.set(getConfiguration().getName(), value.getValue());
                try {
                    appCtx.getManager().save(data);
                } catch (IOException e) {
                    DevFailedUtils.throwDevFailed(e.getClass().getSimpleName(), e.getLocalizedMessage());
                }
            }

            @Override
            public StateMachineBehavior getStateMachine() throws DevFailed {
                return stateMachine;
            }
        };
    }

    public static void main(String... args) throws Exception {
        extractWebapp();

        ServerManager.getInstance().start(args, PreExperimentDataCollector.class);

        //TODO start tomcat
        Tomcat tomcat = new Tomcat();

        Path tomcatBasedir = Paths.get(XENV_ROOT, "var/predator/tomcat");
        if(Files.notExists(tomcatBasedir)) Files.createDirectories(Paths.get(XENV_ROOT,"var/predator/tomcat/webapps"));
        tomcat.setBaseDir(tomcatBasedir.toAbsolutePath().toString());

        tomcat.setPort(8333);//TODO move to server properties?

        String webapp = Paths.get(XENV_ROOT).resolve(VAR_PREDATOR_ROOT_WAR).toAbsolutePath().toString();
        logger.info("Adding webapp {}", webapp);

        Context context = tomcat.addWebapp("/", webapp);
        WebappLoader loader =
                new WebappLoader(Thread.currentThread().getContextClassLoader());
        context.setLoader(loader);

        tomcat.addUser("ingvord", "test");
        tomcat.addRole("ingvord", "user");

        tomcat.start();
    }
}
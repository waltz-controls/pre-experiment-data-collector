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
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fr.esrf.Tango.AttrWriteType;
import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.DevState;
import fr.esrf.TangoApi.PipeBlob;
import fr.esrf.TangoApi.PipeBlobBuilder;
import hzg.wpn.predator.ApplicationContext;
import hzg.wpn.predator.meta.Meta;
import hzg.wpn.predator.web.ApplicationLoader;
import hzg.wpn.predator.web.LoginProperties;
import hzg.wpn.util.beanutils.BeanUtilsHelper;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tango.DeviceState;
import org.tango.client.ez.util.TangoUtils;
import org.tango.server.ServerManager;
import org.tango.server.ServerManagerUtils;
import org.tango.server.StateMachineBehavior;
import org.tango.server.annotation.*;
import org.tango.server.attribute.AttributeConfiguration;
import org.tango.server.attribute.AttributeValue;
import org.tango.server.attribute.IAttributeBehavior;
import org.tango.server.device.DeviceManager;
import org.tango.server.dynamic.DynamicManager;
import org.tango.server.pipe.PipeValue;
import org.tango.utils.DevFailedUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Designed to be Thread condemned
 */
@Device
public class PreExperimentDataCollector {
    public static final String ERROR_MESSAGE = "data_set is null. load_data_set first.";
    public static final int TOMCAT_PORT = 10002;
    private static final Logger logger = LoggerFactory.getLogger(PreExperimentDataCollector.class);
    private static final Tomcat TOMCAT = new Tomcat();
    private static final ExecutorService TOMCAT_STARTER = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("PreExperimentDataCollector embedded tomcat starter").setDaemon(true).build());
    private static ApplicationContext APPLICATION_CONTEXT;
    @Pipe(name = "status")
    private final PipeValue statusPipe = new PipeValue();
    private ApplicationContext appCtx;
    //@Monitored
    private LoginProperties loginProperties;
    //@MonitoredSpecial(DataHandler.class)
    private volatile DynaBean data;
    @DeviceManagement
    private DeviceManager deviceManager;
    @State
    //@Monitored
    private volatile DevState state;
    @Status
    private String status;
    @DynamicManagement
    private DynamicManager dynamic;
    @Pipe
    private PipeValue pipe;

    public synchronized static void setStaticContext(ApplicationContext applicationContext) {
        APPLICATION_CONTEXT = applicationContext;
    }

    public static void main(String... args) throws Exception {
        ServerManager.getInstance().start(args, PreExperimentDataCollector.class);
        ServerManagerUtils.writePidFile(null);
    }

    public void setDeviceManager(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    public DevState getState() {
        return state;
    }

    public void setState(DevState state) {
        this.state = state;
    }

    public PipeValue getPipe() {
        Preconditions.checkNotNull(data, ERROR_MESSAGE);

        PipeBlobBuilder pbb = new PipeBlobBuilder("any");//see DFS

        pbb.add("append", false);

        PipeBlobBuilder dataBlob = new PipeBlobBuilder("data");
        for (final DynaProperty dynaProperty : appCtx.getDataClass().getDynaProperties()) {
            Class<?> type = dynaProperty.getType();
            Object property = BeanUtilsHelper.getProperty(data, dynaProperty.getName(), type);
            if(property == null) continue;
            Object value;
            if(type.isArray()) {
                value = property;
            } else {
                value = Array.newInstance(type, 1);
                Array.set(value, 0, property);
            }

            dataBlob.add(dynaProperty.getName(),
                    new PipeBlobBuilder(dynaProperty.getName())
                            .add("value", value)
                            .build());
        }

        pbb.add("data", dataBlob.build());

        PipeValue result = new PipeValue();
        result.setValue(pbb.build(), System.currentTimeMillis());
        return result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //aspect
    private PipeBlob updateStatus(){

        PipeBlobBuilder pbb = new PipeBlobBuilder("status");

        //TODO walk through @Monitored and put them into pipe

        pbb.add("state", state);
        pbb.add("status", "NA");//TODO status
        pbb.add("dataset", data != null ? BeanUtilsHelper.getProperty(data,Meta.NAME, String.class) : "NONE");

//        pbb.add("auth", loginProperties.isKerberos ? "kerberos" : "basic");

        try {
            pbb.add("datasets", getDatasets());
        } catch (Exception e) {
            logger.warn("Failed to load datasets: {}", e.getMessage());
        }

        return pbb.build();
    }

    public PipeValue getStatusPipe() {
        //pipe value is set in aspect
        statusPipe.setValue(updateStatus(), System.currentTimeMillis());
        return statusPipe;
    }

    /**
     * Iterates over all users and loads their data sets
     *
     * @return
     * @throws Exception
     */
    @Attribute
    public String[] getDatasets() throws Exception {
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
        setStatus(String.format("Dataset[%s] has been deleted", name));
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
    public void create_data_set(String[] args) throws Exception {
        if (args.length != 2)
            throw DevFailedUtils.newDevFailed("Exactly 2 arguments are expected here: user name and data set name.");
        String user = args[0];
        String name = args[1];
        data = appCtx.getManager().newDataSet(user, name);
        appCtx.getManager().save(data);
        setStatus(String.format("Dataset[%s] for user[%s] has been created", name, user));
    }

    @Command(inTypeDesc = "dataset_name")
    //@UpdatesMonitor -- basically calls pushStatus inside aspect
    public void load_data_set(final String name) throws Exception {
        //get all users
        Iterable<String> users = appCtx.getUsers();
        //add all data sets of each user
        this.data = getDataSet(name, users);
        setStatus(String.format("Dataset[%s] has been loaded", name));
        pushStatus();
    }


    //aspect
    private void pushStatus() {
        try {
            deviceManager.pushPipeEvent("status", getStatusPipe());
        } catch (DevFailed devFailed) {
            if(getState() == DevState.FAULT){
                logger.error("Failed to push statusPipe event: {}", TangoUtils.convertDevFailedToException(devFailed).getMessage());
                return;//give up
            }

            setState(DevState.FAULT);
            //TODO status

            pushStatus();
        }
    }

    public void setDynamic(DynamicManager dynamic) {
        this.dynamic = dynamic;
    }

    @Init
    @StateMachine(endState = DeviceState.ON)
    public void init() throws Exception {
        TOMCAT_STARTER.execute(new TomcatStarterTask());

    }

    @Delete
    public void delete() throws Exception {
        TOMCAT.stop();
        TOMCAT_STARTER.shutdownNow();
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
                configuration.getAttributeProperties().setLabel(dynaProperty.getName());
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

    public class TomcatStarterTask implements Runnable {
        @Override
        public void run() {
            TOMCAT.setPort(TOMCAT_PORT);

            TOMCAT.setConnector(TOMCAT.getConnector());


            ApplicationLoader.initializeWebapp(TOMCAT);

            loginProperties = ApplicationLoader.initializeLoginProperties(TOMCAT);

            try {
                TOMCAT.start();

                appCtx = APPLICATION_CONTEXT;
                //TODO set status
                //populate attributes
                for (final DynaProperty dynaProperty : appCtx.getDataClass().getDynaProperties()) {
                    dynamic.addAttribute(createNewAttribute(dynaProperty, appCtx));
                }
            } catch (LifecycleException e) {
                logger.error("Failed to start Tomcat: {}", e.getMessage());
                setState(DevState.FAULT);
                //TODO status
            } catch (DevFailed devFailed) {
                DevFailedUtils.logDevFailed(devFailed, logger);
                setState(DevState.FAULT);
            }
        }
    }
}
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

package hzg.wpn.hdri.predator.frontend;

import fr.esrf.Tango.AttrWriteType;
import fr.esrf.Tango.DevFailed;
import hzg.wpn.hdri.predator.ApplicationContext;
import hzg.wpn.hdri.predator.data.DataSet;
import hzg.wpn.hdri.predator.data.DataSets;
import hzg.wpn.hdri.predator.data.User;
import hzg.wpn.hdri.predator.data.Users;
import hzg.wpn.hdri.predator.meta.MetaData;
import hzg.wpn.hdri.predator.meta.MetaDataHelpers;
import hzg.wpn.hdri.predator.meta.MetaField;
import org.apache.commons.beanutils.DynaBean;
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

import static hzg.wpn.hdri.predator.data.DataSets.*;

@Device
public class TangoDevice implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(TangoDevice.class);

    private static volatile ApplicationContext CTX;

    public static void setContext(ApplicationContext ctx) {
        CTX = ctx;
    }

    private final String tangoInstanceName;
    private final String[] tangoServerArguments;

    public TangoDevice(String tangoInstanceName, String[] tangoServerArguments) {
        this.tangoInstanceName = tangoInstanceName;
        this.tangoServerArguments = tangoServerArguments;
    }

    public TangoDevice() {
        tangoInstanceName = null;
        tangoServerArguments = null;
    }

    //tango time fields

    @DynamicManagement
    private DynamicManager manager;

    public void setManager(DynamicManager manager) {
        this.manager = manager;
    }

    private volatile User user;
    private volatile DataSet dataSet;

    @Status
    private volatile String status;

    public String getStatus() {
        return "USER:" + ((user == null) ? "NULL" : user.getName()) +
                ";DATA_SET:" + ((dataSet == null) ? "NULL" : dataSet.getId());
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Command
    @StateMachine(endState = DeviceState.ON)
    public void reset() {
        user = null;
        dataSet = null;
    }

    @Command
    public void newUser(String userName) {
        user = Users.getUser(userName, true, CTX);
    }

    @Command
    public void loadUser(String userName) {
        user = Users.getUser(userName, false, CTX);
        if (user == null)
            throw new IllegalStateException("User[" + userName + "] does not exist!");
    }

    @Command
    @StateMachine(endState = DeviceState.RUNNING)
    public void newDataSet(String name) throws Exception {
        if (user == null)
            throw new IllegalStateException("User is null! Create or load user first.");

        this.dataSet = DataSets.createDataSet(user, CTX.getMetaDataHelper().getMetaData(), CTX.getBeamtimeId(), name);

        CTX.getStorage().save(dataSet.getData(), CTX);
    }

    @Command
    @StateMachine(endState = DeviceState.RUNNING)
    public void loadDataSet(String name) throws Exception {
        if (user == null)
            throw new IllegalStateException("User is null! Create or load user first.");
        DynaBean data = CTX.getStorage().load(name, CTX);
        this.dataSet = createDataSet(
                user, CTX.getMetaDataHelper().getMetaData(), CTX.getBeamtimeId(), name, data);
    }

    @Command(name = "showAllDataSetNames")
    public String[] getAllDataSets(String userName) throws Exception {
        User user = Users.getUser(userName, false, CTX);
        if (user == null)
            throw new IllegalStateException("Can not load data set names for non-existing user[" + userName + "].");
        return getUserDataSetNames(user, CTX).toArray(new String[0]);
    }

    @Command(name = "showAllUserNames")
    public String[] getAllUsers() throws Exception {
        return Users.loadUserNames(CTX).toArray(new String[0]);
    }

    @Attribute
    @AttributeProperties(description = "This attribute specifies current BeamtimeId. BeamtimeId is specified in application.properties")
    public String getBeamtimeId() {
        return CTX.getBeamtimeId().getValue();
    }

    @Init
    @StateMachine(endState = DeviceState.ON)
    public void init() throws Exception {
        if (CTX == null) throw new IllegalStateException("ApplicationContext is not yet initialized!");

        MetaData meta = CTX.getMetaDataHelper().getMetaData();
        for (final MetaField fld : meta.getAllFields()) {
            manager.addAttribute(new IAttributeBehavior() {
                @Override
                public AttributeConfiguration getConfiguration() throws DevFailed {
                    AttributeConfiguration configuration = new AttributeConfiguration();
                    configuration.setName(fld.getId());
                    configuration.setWritable(AttrWriteType.READ_WRITE);
                    configuration.setType(MetaDataHelpers.getFieldTypeAdaptor(fld).getTargetClass());
                    return configuration;
                }

                @Override
                public AttributeValue getValue() throws DevFailed {
                    if (TangoDevice.this.dataSet == null)
                        throw new IllegalStateException("Can not set value to null data set. Set dataSet first.");

                    return new AttributeValue(dataSet.get(fld.getId()));
                }

                @Override
                public void setValue(AttributeValue value) throws DevFailed {
                    if (TangoDevice.this.dataSet == null)
                        throw new IllegalStateException("Can not set value to null data set. Set dataSet first.");

                    DataSet dataSet = TangoDevice.this.dataSet;
                    update(fld.getId(), value.getValue(), dataSet);

                    try {
                        CTX.getStorage().save(dataSet.getData(), CTX);
                    } catch (Exception e) {
                        DevFailedUtils.throwDevFailed(e);
                    }
                }

                @Override
                public StateMachineBehavior getStateMachine() throws DevFailed {
                    return null;
                }
            });
        }
    }

    @Delete
    @StateMachine(endState = DeviceState.OFF)
    public void delete() {

    }

    /**
     * Main entry point for Tango Server
     */
    public void run() {
        String[] args = new String[tangoServerArguments.length + 1];
        args[0] = tangoInstanceName;
        System.arraycopy(tangoServerArguments, 0, args, 1, tangoServerArguments.length);

        ServerManager.getInstance().addClass("PreExperimentDataCollector", TangoDevice.class);
        ServerManager.getInstance().start(args, "PreExperimentDataCollector");
    }
}
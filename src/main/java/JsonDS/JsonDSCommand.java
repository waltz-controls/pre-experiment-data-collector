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

package JsonDS;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoDs.Command;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import wpn.hdri.tango.command.AbsCommand;
import wpn.hdri.tango.data.type.ScalarTangoDataTypes;
import wpn.hdri.tango.data.type.SpectrumTangoDataTypes;
import wpn.hdri.tango.proxy.TangoProxyException;
import wpn.hdri.tango.proxy.TangoProxyWrapper;
import wpn.hdri.tango.util.TangoUtils;
import wpn.hdri.web.data.DataSet;
import wpn.hdri.web.data.DataSets;
import wpn.hdri.web.data.Users;
import wpn.hdri.web.storage.StorageException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 28.03.12
 */
public enum JsonDSCommand {
    SEND_DATA_TO(new AbsCommand<JsonDS, String, Void>("sendDataTo",
            ScalarTangoDataTypes.STRING, ScalarTangoDataTypes.VOID,
            "full name of the destination, i.e. tango://<host>:<port>/<device_name>", ""
    ) {
        @Override
        protected Void executeInternal(JsonDS instance, String receiver, Logger log) throws DevFailed {
            try {
                TangoProxyWrapper proxy = new TangoProxyWrapper(receiver);


                DataSet dataSet = instance.getDataSet();

                Collection<String> data = Sets.newHashSet(
                        Iterables.<Map.Entry<Object, Object>, String>transform(
                                PropertyUtils.describe(dataSet.getData()).entrySet(),
                                new Function<Map.Entry<Object, Object>, String>() {
                                    @Override
                                    public String apply(Map.Entry<Object, Object> input) {
                                        return String.valueOf(input.getKey()) + "=" + String.valueOf(input.getValue());
                                    }
                                }));

                data.add(JsonDSAttr.CRT_BEAMTIME_ID.name() + "=" + instance.getContext().getBeamtimeId().getValue());
                data.add(JsonDSAttr.CRT_USER_NAME.name() + "=" + instance.getUser().getName());
                data.add(JsonDSAttr.CRT_USER_SCAN.name() + "=" + instance.getDataSetName());

                String[] dataToSend = data.toArray(new String[data.size()]);
                proxy.executeCommand("receiveData", dataToSend);
                return null;
            } catch (TangoProxyException e) {
                throw TangoUtils.createDevFailed(e);
            } catch (InvocationTargetException e) {
                throw TangoUtils.createDevFailed(e);
            } catch (NoSuchMethodException e) {
                throw TangoUtils.createDevFailed(e);
            } catch (IllegalAccessException e) {
                throw TangoUtils.createDevFailed(e);
            }
        }
    }),
    AVLB_USER_NAMES(new AbsCommand<JsonDS, Void, String[]>("availableUserNames",
            ScalarTangoDataTypes.VOID, SpectrumTangoDataTypes.STRING_ARR,
            "", "") {

        @Override
        protected String[] executeInternal(JsonDS instance, Void data, Logger log) throws DevFailed {
            try {
                Collection<String> result = Users.loadUserNames(instance.getContext());

                return result.toArray(new String[result.size()]);
            } catch (IOException e) {
                throw TangoUtils.createDevFailed(e);
            }
        }
    }),
    AVLB_USER_SCAN_NAMES(new AbsCommand<JsonDS, String, String[]>("availableScanNamesForUser",
            ScalarTangoDataTypes.STRING, SpectrumTangoDataTypes.STRING_ARR,
            "USER_NAME", "") {

        @Override
        protected String[] executeInternal(JsonDS instance, String data, Logger log) throws DevFailed {
            try {
                Users.User user = Users.getUser(data, false, instance.getContext());
                Collection<String> result = DataSets.getUserDataSetNames(user, instance.getContext());
                return result.toArray(new String[result.size()]);
            } catch (IOException e) {
                throw TangoUtils.createDevFailed(e);
            }
        }
    }),
    LOAD_DATA_SET(new AbsCommand<JsonDS, String[], Void>("loadDataSet",
            SpectrumTangoDataTypes.STRING_ARR, ScalarTangoDataTypes.VOID,
            "USER_NAME,SCAN_NAME", "") {

        @Override
        protected Void executeInternal(JsonDS instance, String[] args, Logger log) throws DevFailed {
            String userName = args[0];
            String scanName = args[1];

            Users.User user = Users.getUser(userName, false, instance.getContext());

            instance.setUser(user);
            instance.setDataSetName(scanName);

            try {
                DynaBean data = instance.getContext().getStorage().load(user, scanName, instance.getContext());
                DataSet dataSet = DataSets.createDataSet(
                        user, instance.getContext().getMetaDataHelper().getMetaData(), instance.getContext().getBeamtimeId(), scanName, data);
                instance.setDataSet(dataSet);
                return null;
            } catch (StorageException e) {
                throw TangoUtils.createDevFailed(e);
            } catch (Exception e) {
                throw TangoUtils.createDevFailed(e);
            }
        }
    }),
    MAKE_ALL_WEB_FIELDS_READONLY(new AbsCommand<JsonDS, String[], Void>("setWebScanPropertiesReadonly",
            SpectrumTangoDataTypes.STRING_ARR, ScalarTangoDataTypes.VOID,
            "USER_NAME,SCAN_NAME", "") {
        @Override
        protected Void executeInternal(JsonDS instance, String[] args, Logger logger) throws DevFailed {
            String userName = args[0];
            String scanName = args[1];
            Users.User user = Users.getUser(userName, false, instance.getContext());
            try {
                DynaBean data = instance.getContext().getStorage().load(user, scanName, instance.getContext());
                DataSet dataSet = DataSets.createDataSet(
                        user, instance.getContext().getMetaDataHelper().getMetaData(), instance.getContext().getBeamtimeId(), scanName, data);
                DataSets.setReadonly(dataSet);
                return null;
            } catch (StorageException e) {
                throw TangoUtils.createDevFailed(e);
            } catch (Exception e) {
                throw TangoUtils.createDevFailed(e);
            }
        }
    });

    private final Command cmd;

    private JsonDSCommand(Command cmd) {
        this.cmd = cmd;
    }

    public Command toCommand() {
        return cmd;
    }
}

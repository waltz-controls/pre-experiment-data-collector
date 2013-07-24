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

package wpn.hdri.web.backend.submit;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.data.DataSet;
import wpn.hdri.web.data.DataSets;
import wpn.hdri.web.data.User;
import wpn.hdri.web.storage.StorageException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.05.12
 */
public class OnlineSubmitHelper extends SubmitHelper {
    @Override
    protected DataSet processSubmitInternal(User user, SubmitDataHandler.Parameters requestParameters, String dataSetName, ApplicationContext applicationContext, Logger log) throws BackendException {
        DataSet oldDataSet = load(user, dataSetName, applicationContext);

        if (!oldDataSet.isReadonly()) {
            DynaBean values = getDataSetValues(requestParameters.data, oldDataSet.getMeta());
            log.info("Writing new values to data set:");
            try {
                for (Map.Entry entry : (Set<Map.Entry>) PropertyUtils.describe(values).entrySet()) {
                    log.info(String.valueOf(entry.getKey()) + "=" + String.valueOf(entry.getValue()));
                }
            } catch (IllegalAccessException e) {
                log.error("Ag-rh", e);
            } catch (InvocationTargetException e) {
                log.error("Ouch", e);
            } catch (NoSuchMethodException e) {
                log.error("Oops", e);
            }
            DataSets.update(values, oldDataSet);
        }

        return oldDataSet;
    }

    /**
     * Loads a data set or creates a new one. A new one can be needed when user uploads data from offline client.
     *
     * @param user
     * @param dataSetName
     * @param applicationContext @return
     * @throws wpn.hdri.web.backend.BackendException
     *
     */
    private DataSet load(User user, String dataSetName, ApplicationContext applicationContext) throws BackendException {
        try {
            DynaBean data = applicationContext.getStorage().load(user, dataSetName, applicationContext);
            if (data == null) {
                //TODO handle situation
                throw new IllegalStateException("DataSet can not be null at this point.");
            }
            DataSet result = DataSets.createDataSet(user, applicationContext.getMetaDataHelper().getMetaData(), applicationContext.getBeamtimeId(), dataSetName, data);
            return result;
        } catch (StorageException e) {
            throw new BackendException("An attempt to load dataset failed.", e);
        } catch (Exception e) {
            throw new BackendException("An attempt to create dataset failed.", e);
        }
    }
}

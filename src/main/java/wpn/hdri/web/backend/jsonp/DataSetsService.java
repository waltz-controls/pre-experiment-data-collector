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

package wpn.hdri.web.backend.jsonp;

import org.apache.commons.beanutils.DynaBean;
import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.backend.CommonRequestParameters;
import wpn.hdri.web.backend.RequestParameter;
import wpn.hdri.web.data.DataSets;
import wpn.hdri.web.data.Users;
import wpn.hdri.web.meta.MetaData;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Map;

import static wpn.hdri.web.backend.CommonRequestParameters.DATA_SET_NAME;
import wpn.hdri.web.data.DataSet;

/**
 * Provides JSONP interface for creation and finding {@link DataSet} associated with the user.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 09.03.12
 */
public final class DataSetsService extends JsonpBaseServlet<DataSet> {
    /**
     * Creates, stores and returns new {@link DataSet}
     *
     * @param user              current User
     * @param requestParameters
     * @param req
     * @return a new DataSet
     * @throws BackendException
     */
    public DataSet doCreate(Users.User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException {
        String id = requestParameters.get(DATA_SET_NAME);

        try {
            MetaData meta = getApplicationContext().getMetaDataHelper().createMetaData();
            DataSet dataSet = DataSets.createDataSet(user, meta, getApplicationContext().getBeamtimeId(), id);

            getStorage().save(dataSet.getData(), user, id, getApplicationContext());

            return dataSet;
        } catch (Exception e) {
            throw new BackendException("An attempt to create DataSet failed.", e);
        }
    }

    /**
     * Finds and returns all {@link DataSet} associated with the User.
     *
     * @param user              current User
     * @param requestParameters
     * @param req
     * @return a collection of the found DataSets
     * @throws BackendException
     */
    public DataSet[] doFindAll(Users.User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException {
        try {
            Collection<DataSet> result = new LinkedHashSet<DataSet>();

            for (String dataSetName : DataSets.getUserDataSetNames(user, getApplicationContext())) {
                DynaBean data = getStorage().load(user, dataSetName, getApplicationContext());
                DataSet dataSet = DataSets.createDataSet(user,getApplicationContext().getMetaDataHelper().getMetaData(),
                        getApplicationContext().getBeamtimeId(),dataSetName,data);
                result.add(dataSet);
            }

            return result.toArray(new DataSet[result.size()]);
        } catch (Exception e) {
            throw new BackendException("Unable to load user's datasets [user:" + user.getName() + "]", e);
        }
    }

    @Override
    protected Collection<CommonRequestParameters> getUsedRequestParameters() {
        EnumSet<CommonRequestParameters> result = EnumSet.of(DATA_SET_NAME);
        result.addAll(super.getUsedRequestParameters());
        return result;
    }
}

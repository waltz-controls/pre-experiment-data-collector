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
import su.clan.tla.web.backend.RequestParameter;
import su.clan.tla.web.backend.json.JsonRequest;
import su.clan.tla.web.backend.json.JsonpBaseServlet;
import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.backend.ApplicationServlet;
import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.data.DataSet;
import wpn.hdri.web.data.DataSets;
import wpn.hdri.web.data.User;
import wpn.hdri.web.data.Users;
import wpn.hdri.web.meta.MetaData;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Provides JSONP interface for creation and finding {@link DataSet} associated with the user.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 09.03.12
 */
public final class DataSetsService extends JsonpBaseServlet<DataSet, DataSetsService.Parameters> {
    private volatile ApplicationContext appCtx;

    @Override
    protected void doInitInternal(ServletConfig config) throws ServletException {
        super.doInitInternal(config);

        appCtx = (ApplicationContext) config.getServletContext().getAttribute(ApplicationServlet.APPLICATION_CONTEXT);
    }

    /**
     * Creates, stores and returns new {@link DataSet}
     *
     * @param req
     * @return a new DataSet
     * @throws BackendException
     */
    public DataSet create(JsonRequest<Parameters> req) throws BackendException {
        try {
            MetaData meta = appCtx.getMetaDataHelper().createMetaData();
            User user = Users.getUser(req.getRemoteUser(), false, appCtx);
            String id = req.getParameters().dataSetName;
            DataSet dataSet = DataSets.createDataSet(user, meta, appCtx.getBeamtimeId(), id);

            appCtx.getStorage().save(dataSet.getData(), user, id, appCtx);

            return dataSet;
        } catch (Exception e) {
            throw new BackendException("An attempt to create DataSet has failed:" + e.getMessage(), e);
        }
    }

    @Override
    public DataSet delete(JsonRequest<Parameters> req) throws Exception {
        //TODO
        throw new UnsupportedOperationException("This method is not supported in " + this.getClass());
    }

    /**
     * Finds and returns all {@link DataSet} associated with the User.
     *
     * @param req
     * @return a collection of the found DataSets
     * @throws BackendException
     */
    public Collection<DataSet> findAll(JsonRequest<Parameters> req) throws BackendException {
        User user = Users.getUser(req.getRemoteUser(), false, appCtx);
        try {
            Collection<DataSet> result = new LinkedHashSet<DataSet>();


            for (String dataSetName : DataSets.getUserDataSetNames(user, appCtx)) {
                DynaBean data = appCtx.getStorage().load(user, dataSetName, appCtx);
                DataSet dataSet = DataSets.createDataSet(user, appCtx.getMetaDataHelper().getMetaData(),
                        appCtx.getBeamtimeId(), dataSetName, data);
                result.add(dataSet);
            }

            return result;
        } catch (Exception e) {
            throw new BackendException("Unable to load user's datasets [user:" + user.getName() + "]", e);
        }
    }

    public static class Parameters {
        @RequestParameter("data-set-name")
        public String dataSetName;
    }
}

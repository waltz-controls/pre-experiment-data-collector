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

import hzg.wpn.hdri.predator.ApplicationContext;
import org.apache.commons.collections.Transformer;
import su.clan.tla.web.backend.json.JsonRequest;
import su.clan.tla.web.backend.json.JsonpBaseServlet;
import wpn.hdri.web.backend.ApplicationServlet;
import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.data.DataSets;
import wpn.hdri.web.data.User;
import wpn.hdri.web.data.Users;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * This servlet provide clients with data set names associated with the user.
 * <p/>
 * Supports only findAll action.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 26.03.12
 */
public class DataSetNamesService extends JsonpBaseServlet<DataSetNamesService.DataSetName, DataSetNamesService.Parameters> {
    private volatile ApplicationContext appCtx;

    @Override
    protected void doInitInternal(ServletConfig config) throws ServletException {
        super.doInitInternal(config);

        appCtx = (ApplicationContext) config.getServletContext().getAttribute(ApplicationServlet.APPLICATION_CONTEXT);
    }

    /**
     * DataSet name holder
     */
    public static class DataSetName {
        private final String value;

        private DataSetName(String value) {
            this.value = value;
        }
    }

    public DataSetName create(JsonRequest<Parameters> req) {
        throw new UnsupportedOperationException("DataSetNameService does not support create action.");
    }

    /**
     * Returns a collection of the {@link DataSetName} names associated with the current {@link wpn.hdri.web.data.User}
     *
     * @param req
     * @return collection of the DataSet names
     * @throws BackendException
     */
    public Collection<DataSetName> findAll(JsonRequest<Parameters> req) {
        User user = Users.getUser(req.getRemoteUser(), true, appCtx);
        try {
            Collection<DataSetName> result =
                    //TODO optimize anonymous
                    new Transformer() {
                        public Collection<DataSetName> transform(Object o) {
                            Collection<String> col = (Collection<String>) o;

                            Collection<DataSetName> result = new LinkedHashSet<DataSetName>();

                            for (String dataSetName : col) {
                                result.add(new DataSetName(dataSetName));
                            }

                            return result;
                        }
                    }.transform(DataSets.getUserDataSetNames(user, appCtx));


            return result;
        } catch (Exception e) {
            throw new RuntimeException("Unable to load user's datasets [user:" + user.getName() + "]", e);
        }
    }

    @Override
    public DataSetName delete(JsonRequest<Parameters> req) {
        throw new UnsupportedOperationException("This method is not supported in " + this.getClass());
    }

    public static class Parameters {
    }
}

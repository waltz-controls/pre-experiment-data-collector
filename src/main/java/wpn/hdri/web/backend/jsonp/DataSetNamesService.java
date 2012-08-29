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

import org.apache.commons.collections.Transformer;
import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.backend.RequestParameter;
import wpn.hdri.web.data.DataSets;
import wpn.hdri.web.data.Users;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Supports only {@link JsonService#doFindAll(wpn.hdri.web.data.Users.User, java.util.Map, javax.servlet.http.HttpServletRequest)}
 * action.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 26.03.12
 */
public class DataSetNamesService extends JsonpBaseServlet<DataSetNamesService.DataSetName> {
    /**
     * DataSet name holder
     */
    public static class DataSetName {
        private final String value;

        private DataSetName(String value) {
            this.value = value;
        }
    }

    public DataSetName doCreate(Users.User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException {
        throw new BackendException("DataSetNameService does not support create action.", new UnsupportedOperationException());
    }

    /**
     * Returns a collection of the {@link DataSetName} names associated with the current {@link Users.User}
     *
     * @param user              current User
     * @param requestParameters
     * @param req
     * @return collection of the DataSet names
     * @throws BackendException
     */
    public DataSetName[] doFindAll(Users.User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException {
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
                    }.transform(DataSets.getUserDataSetNames(user, getApplicationContext()));


            return result.toArray(new DataSetName[result.size()]);
        } catch (Exception e) {
            throw new BackendException("Unable to load user's datasets [user:" + user.getName() + "]", e);
        }
    }
}

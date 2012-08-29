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

import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.backend.CommonRequestParameters;
import wpn.hdri.web.backend.RequestParameter;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

import static wpn.hdri.web.backend.CommonRequestParameters.CALLBACK;
import static wpn.hdri.web.data.Users.User;

/**
 * This servlet is designed for extending. It wraps the response according to JSONP pattern.
 * It uses {@link com.google.gson.Gson Gson} to de/serialize data packets.
 * <p/>
 * Provides JSONP response to client.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 22.12.11
 */
public abstract class JsonpBaseServlet<T> extends JsonBaseServlet<T> {
    /**
     * Calls {@link super#doGetInternal(wpn.hdri.web.data.Users.User, java.util.Map, javax.servlet.http.HttpServletRequest)} to handle request.
     * Wraps json output with callback function according to JSONP pattern.
     */
    @Override
    protected String doGetInternal(User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException {
        String callback = requestParameters.get(CALLBACK);
        if (callback == null) {
            throw new BackendException("Callback is null.", new IllegalArgumentException("callback parameter can not be null"));
        }

        String jsonData = super.doGetInternal(user, requestParameters, req);
        StringBuilder output = new StringBuilder();

        output.append(callback).append("(").append(jsonData).append(");");

        return output.toString();
    }

    protected String getResponseType() {
        return "application/javascript";
    }

    @Override
    protected Collection<CommonRequestParameters> getUsedRequestParameters() {
        EnumSet<CommonRequestParameters> result = EnumSet.of(CALLBACK);

        result.addAll(super.getUsedRequestParameters());

        return result;
    }

    /**
     * JSONP does not support POST method.
     *
     * @return
     * @throws UnsupportedOperationException wrapped by BackendException
     */
    @Override
    protected String doPostInternal(User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException {
        throw new BackendException("JSONP servlet does not support POST method.", new UnsupportedOperationException());
    }
}

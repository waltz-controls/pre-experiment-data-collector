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

import com.google.gson.*;
import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.backend.BaseServlet;
import wpn.hdri.web.backend.CommonRequestParameters;
import wpn.hdri.web.backend.RequestParameter;
import wpn.hdri.web.data.JsonSerializable;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;

import static wpn.hdri.web.backend.CommonRequestParameters.ACTION;
import static wpn.hdri.web.data.Users.User;

/**
 * Provides JSON response to client.
 * <p/>
 * Implementation uses {@link Gson} to convert whatever is returned by subclass into JSON. Expect String. if subclass returns
 * {@link String} from {@link this#doGetInternal(wpn.hdri.web.data.Users.User, java.util.Map, javax.servlet.http.HttpServletRequest)}
 * then it is assumed that this String is already in JSON format.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 16.03.12
 */
public abstract class JsonBaseServlet<T> extends BaseServlet implements JsonService<T> {
    public static final String CREATE = "create";
    public static final String FIND_ALL = "find_all";

    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeHierarchyAdapter(JsonSerializable.class,new JsonSerializer<JsonSerializable>() {
                @Override
                public JsonElement serialize(JsonSerializable src, Type typeOfSrc, JsonSerializationContext context) {
                    return src.toJson();
                }
            })
            .create();

    @Override
    //TODO handle exception, i.e. client and server should provide meaningful information to the user in case exception
    //TODO this should be done through an agreement between client and server
    //TODO implement error console framework
    protected String doGetInternal(User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException {
        String action = requestParameters.get(ACTION);
        if (action == null) {
            //return everything by default
            action = FIND_ALL;
        }

        Object o = null;
        if (CREATE.equalsIgnoreCase(action)) {
            o = doCreate(user, requestParameters, req);
        } else {
            o = doFindAll(user, requestParameters, req);
        }

        return getJsonData(o);
    }

    protected String getResponseType() {
        //prevent IE to popup save dialog when response is application/json
        return "text/plain";
    }

    @Override
    protected Collection<CommonRequestParameters> getUsedRequestParameters() {
        return EnumSet.of(CommonRequestParameters.ACTION);
    }

    private String getJsonData(Object o) throws BackendException {
        if (String.class.isAssignableFrom(o.getClass()))
            return (String) o;
        else
            try {
                return gson.toJson(o);
            } catch (JsonParseException e) {
                throw new BackendException("An attempt to serialize data into json failed.", e);
            }
    }
}

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

package wpn.hdri.web.backend;

import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Logger;
import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.storage.Storage;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static wpn.hdri.web.data.Users.User;
import static wpn.hdri.web.data.Users.getUser;

/**
 * Base class for all application's servlets. Designed for inheritance.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.03.12
 */
public abstract class BaseServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(BaseServlet.class);

    private RequestParametersInitializer requestParametersInitializer;

    private ApplicationContext applicationContext;
    private Storage<DynaBean> storage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        requestParametersInitializer = new RequestParametersInitializer(getUsedRequestParameters());

        applicationContext = (ApplicationContext) config.getServletContext().getAttribute(ApplicationServlet.APPLICATION_CONTEXT);
        storage = applicationContext.getStorage();
    }

    /**
     * Forces response charset to be set to UTF-8.
     *
     * @param req
     * @param resp
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected final void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType(getResponseType());
        super.service(req, resp);
    }

    /**
     * @return response type, i.e. text/plain, text/html, application/json etc
     */
    protected String getResponseType() {
        return "text/html";
    }

    /**
     * Returns a set of {@link RequestParameter}s used by Servlet to process request.
     *
     * @return an EnumSet
     */
    protected abstract Collection<? extends RequestParameter> getUsedRequestParameters();

    /**
     * Implement this for GET-request processing logic.
     *
     * @param user              remote user.
     * @param requestParameters initialized request parameters.
     * @param req
     * @throws BackendException
     */
    protected abstract String doGetInternal(User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException;

    /**
     * Implement this for POST-request processing logic.
     *
     * @param user              remote user.
     * @param requestParameters initialized RequestParameters
     * @param req
     * @throws BackendException
     */
    protected abstract String doPostInternal(User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException;

    /**
     * Calls {@link this#doGetInternal(wpn.hdri.web.data.Users.User, java.util.Map, javax.servlet.http.HttpServletRequest)} to process request.
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User user = getUser(req.getRemoteUser(), true, applicationContext);
            Map<RequestParameter, String> requestParameters = requestParametersInitializer.initializeRequestParameters(req);

            String output = doGetInternal(user, requestParameters, req);

            PrintWriter out = resp.getWriter();
            out.write(output);
        } catch (BackendException e) {
            log.error("GET request processing has failed.", e);
            throw new ServletException(e);
        }
    }

    //TODO
//    @Override
//    protected long getLastModified(HttpServletRequest req) {
//        return super.getLastModified(req);
//    }

    /**
     * Finalises default implementation of the {@link super#doHead(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected final void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doHead(req, resp);
    }

    /**
     * Calls {@link this#doPostInternal(wpn.hdri.web.data.Users.User, java.util.Map, javax.servlet.http.HttpServletRequest)} to handle request.
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User user = getUser(req.getRemoteUser(), true, applicationContext);
            Map<RequestParameter, String> requestParameters = requestParametersInitializer.initializeRequestParameters(req);

            String output = doPostInternal(user, requestParameters, req);

            PrintWriter out = resp.getWriter();
            out.write(output);
        } catch (BackendException e) {
            log.error("POST request processing has failed.", e);
            throw new ServletException(e);
        }
    }

    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //TODO
        throw new ServletException(new UnsupportedOperationException());
    }

    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //TODO
        throw new ServletException(new UnsupportedOperationException());
    }

    @Override
    protected final void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doOptions(req, resp);
    }

    @Override
    protected final void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doTrace(req, resp);
    }

    private static class RequestParametersInitializer {
        private final Collection<? extends RequestParameter> parameters;

        private RequestParametersInitializer(Collection<? extends RequestParameter> parameters) {
            this.parameters = parameters;
        }

        //TODO replace String with more convenient values
        private Map<RequestParameter, String> initializeRequestParameters(HttpServletRequest req) {
            Map<RequestParameter, String> result = new HashMap<RequestParameter, String>();
            for (RequestParameter parameter : parameters) {
                //TODO check nulls
                result.put(parameter, req.getParameter(parameter.getAlias()));
            }
            return result;
        }
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected Storage<DynaBean> getStorage() {
        return storage;
    }
}

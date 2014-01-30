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

package hzg.wpn.util.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 01.02.12
 */
public class ServletUtils {
    private ServletUtils() {
    }

    /**
     * Returns a value associated with the name or null if no value was found and tolerateNull is true. Otherwise IllegalArgumentException
     *
     * @param name
     * @param req
     * @param tolerateNull
     * @return
     * @throws IllegalArgumentException no value was found and tolerateNull is false
     */
    public static String getParameter(String name, HttpServletRequest req, boolean tolerateNull) {
        String value = req.getParameter(name);

        if (value != null) {
            return value;
        } else if (tolerateNull) {
            return null;
        } else {
            throw new IllegalArgumentException("no parameter was found for name[" + name + "]");
        }
    }

    /**
     * Returns a value associated with the name or null if no value was found and tolerateNull is true. Otherwise IllegalArgumentException
     *
     * @param name
     * @param session
     * @param tolerateNull
     * @return
     * @throws IllegalArgumentException no vaule was found and tolerateNull is false
     */
    public static <T> T getParameter(String name, HttpSession session, boolean tolerateNull, Class<T> clazz) {
        Object value = session.getAttribute(name);

        if (value != null) {
            return clazz.cast(value);
        } else if (tolerateNull) {
            return null;
        } else {
            throw new IllegalArgumentException("no parameter was found for name[" + name + "]");
        }
    }

    public static <T> T getAttribute(String name, Class<T> typeOf, ServletContext ctx) {
        return typeOf.cast(ctx.getAttribute(name));
    }

    public static String getAttribute(String name, ServletContext ctx) {
        return String.valueOf(ctx.getAttribute(name));
    }

    /**
     * Returns an app URL, i.e. for my-app will return 'http://my-server:8080/my-app'
     *
     * @param req
     * @return {scheme}://{server-name}:{port}/{app-context}
     */
    public static StringBuilder getUrl(HttpServletRequest req) {
        String scheme = req.getScheme();
        String server = req.getServerName();
        int portNumber = req.getServerPort();
        String contextPath = req.getContextPath();

        return getUrl(scheme, server, portNumber, contextPath);
    }

    /**
     * Returns an app URL, i.e. for my-app will return 'http://my-server:8080/my-app'
     *
     * @param scheme
     * @param server
     * @param portNumber
     * @param contextPath
     * @return {scheme}://{server-name}:{port}/{app-context}
     */
    public static StringBuilder getUrl(String scheme, String server, int portNumber, String contextPath) {
        return new StringBuilder().append(scheme).append("://").append(server).append(":").append(portNumber).append(contextPath);
    }

    /**
     * @param req
     * @param resource resource path on the server, i.e. 'web-client/app'
     * @return for 'web-client/app' -> 'http://my-server:8080/my-app/web-client/app'
     */
    public static StringBuilder getResourcePath(HttpServletRequest req, String resource) {
        return getUrl(req).append("/").append(resource);
    }

    /**
     * @param scheme
     * @param server
     * @param portNumber
     * @param contextPath
     * @param resource    resource path on the server, i.e. 'web-client/app'
     * @return for 'web-client/app' -> 'http://my-server:8080/my-app/web-client/app'
     */
    public static StringBuilder getResourcePath(String scheme, String server, int portNumber, String contextPath, String resource) {
        return getUrl(scheme, server, portNumber, contextPath).append("/").append(resource);
    }
}

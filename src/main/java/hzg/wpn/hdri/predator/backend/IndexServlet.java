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

package hzg.wpn.hdri.predator.backend;

import hzg.wpn.util.servlet.ServletUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * Trivial implementation of the greetings page.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 12.01.12
 */
public final class IndexServlet extends HttpServlet {
    private static final String JMVC_ROOT = "web-client";

    @Override
    public void init() throws ServletException {
        Velocity.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer out = resp.getWriter();

        Context vctx = createContext(req);

        Velocity.mergeTemplate("index.vm", "UTF-8", vctx, out);
    }

    private Context createContext(HttpServletRequest req) {
        VelocityContext result = new VelocityContext();
        result.put("user", req.getRemoteUser());
        String scheme = req.getScheme();
        result.put("scheme", scheme);
        String serverName = req.getServerName();
        result.put("serverName", serverName);
        int serverPort = req.getServerPort();
        result.put("portNumber", String.valueOf(serverPort));
        String contextPath = req.getContextPath();

        result.put("url", ServletUtils.getUrl(scheme, serverName, serverPort, contextPath).toString());
        result.put("jmvc_root", ServletUtils.getResourcePath(scheme, serverName, serverPort, contextPath, JMVC_ROOT).toString());

        return result;
    }
}

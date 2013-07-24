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

import su.clan.tla.web.backend.GetOnlyServlet;
import wpn.hdri.util.servlet.ServletUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Trivial implementation of the greetings page.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 12.01.12
 */
public final class IndexServlet extends GetOnlyServlet {
    private static final String JMVC_ROOT = "web-client";

    @Override
    protected void doInitInternal(ServletConfig config) throws ServletException {
    }

    /**
     * Generates index page.
     *
     * @param req
     * @return
     * @throws BackendException
     */
    @Override
    protected String doGetInternal(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String userName = req.getRemoteUser();
        String scheme = req.getScheme();
        String serverName = req.getServerName();
        int portNumber = req.getServerPort();
        String contextPath = req.getContextPath();

        return getIndexHtml(userName, scheme, serverName, portNumber, contextPath).toString();
    }

    private static StringBuilder getIndexHtml(String userName, String scheme, String serverName, int portNumber, String contextPath) {
        StringBuilder result = new StringBuilder();
        result.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        result.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">\n");
        result.append("<head>\n");
        result.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>\n");
        result.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9\"/>\n");
        result.append("<title>Tomography Pre Experiment Data Wizard</title>\n");
        result.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/smoothness/jquery-ui.css\"/>\n");
        result.append("</head>\n");
        result.append("<body>\n");
        result.append("<div id=\"wrapper\">\n");
        result.append("<div id=\"loading-box\">\n");
        result.append("<div title=\"Loading...\" id=\"loading\"></div>\n");
        result.append("</div>\n");
        result.append("<div id=\"example-2\" style=\"display:none;\" class=\"wizard\">\n");
        result.append("<div class=\"header\"></div>\n");
        result.append("<div id=\"Wizard\">\n");
        result.append("</div>\n");
        result.append("</div>\n");
        result.append("</div>\n");
        result.append("<script language=\"javascript\" type=\"text/javascript\">\n");
        result.append("ApplicationContext = {\n");
        result.append("userName:'").append(userName).append("',\n");
        result.append("scheme:'").append(scheme).append("',\n");
        result.append("serverName:'").append(serverName).append("',\n");
        result.append("port:'").append(String.valueOf(portNumber)).append("',\n");
        result.append("domain:'").append(ServletUtils.getUrl(scheme, serverName, portNumber, contextPath)).append("'");
        result.append("}\n</script>\n");
        result.append("<script type=\"text/javascript\" src=\"http://www.google.com/jsapi\"></script>\n");
        result.append("<script type=\"text/javascript\">\n");
        result.append("google.load('jquery', '1.7');\n");
        result.append("google.load('jqueryui', '1.8');\n");
        result.append("</script>\n");
        result.append("<script type=\"text/javascript\" src=\"");
        result.append(ServletUtils.getResourcePath(scheme, serverName, portNumber, contextPath, JMVC_ROOT));
        result.append("/jmvc/include.js?main,production\"></script>\n");
        result.append("</body>\n");
        result.append("</html>\n");

        return result;
    }
}

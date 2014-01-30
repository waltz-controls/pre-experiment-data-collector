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

package wpn.hdri.web.backend.submit;

import hzg.wpn.util.servlet.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import su.clan.tla.web.backend.BackendHelper;
import su.clan.tla.web.backend.PostOnlyServlet;
import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.backend.ApplicationServlet;
import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.data.User;
import wpn.hdri.web.data.Users;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles main submit request from the user.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 25.01.12
 */
//TODO replace with jsp
public final class SubmitDataHandler extends PostOnlyServlet {
    private static final Logger LOG = LoggerFactory.getLogger(SubmitDataHandler.class);

    private volatile ApplicationContext appCtx;

    @Override
    protected void doInitInternal(ServletConfig config) throws ServletException {
        appCtx = (ApplicationContext) config.getServletContext().getAttribute(ApplicationServlet.APPLICATION_CONTEXT);
    }

    private final SubmitHelper offlineHelper = new OfflineSubmitHelper();
    private final SubmitHelper onlineHelper = new OnlineSubmitHelper();

    //TODO refactor: data set should encapsulate meta entirely
    @Override
    protected String doPostInternal(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        //offline user directly goes here, so it is ok if we create a new one
        User user = Users.getUser(req.getRemoteUser(), true, appCtx);
        Parameters params = BackendHelper.parseRequest(Parameters.class, req);
        boolean offlineSubmit = Boolean.parseBoolean(params.isOfflineParam);

        LOG.info("Processing submit request from " + user.getName() + "@" + req.getRemoteHost());
        try {
            if (offlineSubmit) {
                offlineHelper.processSubmit(user, params, appCtx, LOG);
            } else {
                onlineHelper.processSubmit(user, params, appCtx, LOG);
            }
            LOG.info("Finish processing submit request from " + user.getName() + "@" + req.getRemoteHost());
            return getSuccessHtml(req).toString();
        } catch (BackendException e) {
            LOG.error("Finish processing submit request from " + user.getName() + "@" + req.getRemoteHost(), e);
            return getFailureHtml(req, e).toString();
        }
    }

    //TODO ThreadLocal StringBuilder
    private StringBuilder getFailureHtml(HttpServletRequest req, BackendException e) {
        StringBuilder result = new StringBuilder();

        result.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n")
                .append("        \"http://www.w3.org/TR/html4/loose.dtd\">\n")
                .append("<html>\n")
                .append("<head>\n")
                .append("    <title>Failure</title>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("    <h3>Unfortunately your data has not been uploaded.</h3>")
                .append("    <h3>Reason:</h3><p>")
                .append(e.getMessage())
                .append("        <br/>caused by:")
                .append(e.getCause().getMessage())
                .append("</p></body>\n")
                .append("</html>");


        return result;
    }

    private StringBuilder getSuccessHtml(HttpServletRequest req) {
        StringBuilder result = new StringBuilder();

        result.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n")
                .append("        \"http://www.w3.org/TR/html4/loose.dtd\">\n")
                .append("<html>\n")
                .append("<head>\n")
                .append("    <title>Success</title>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("    <h3>Thank you. Your data has been successfully uploaded.</h3>")
                .append("    <h3>You will be redirected to the home page in <strong id='secondsRemaining'>5</strong> sec...</h3>\n")
                .append("    <script type=\"text/javascript\" language=\"javascript\">\n")
                .append("        var secondsElement = document.getElementById('secondsRemaining');\n")
                .append("        var seconds = secondsElement.innerHTML;\n")
                .append("        setInterval(function(){\n")
                .append("            seconds -= 1;\n")
                .append("            secondsElement.innerHTML = seconds;\n")
                .append("        },1000);\n")
                .append("        setTimeout(function(){\n")
                .append("            window.location.replace('" + ServletUtils.getUrl(req) + "');\n")
                .append("        },seconds * 1000);\n")
                .append("    </script>\n")
                .append("</body>\n")
                .append("</html>");

        return result;
    }

    public static class Parameters {
        @su.clan.tla.web.backend.RequestParameter("data")
        public String data;
        @su.clan.tla.web.backend.RequestParameter("data-set-name")
        public String dataSetName;
        @su.clan.tla.web.backend.RequestParameter("meta")
        public String meta;
        @su.clan.tla.web.backend.RequestParameter("is-offline")
        public String isOfflineParam;
    }
}

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

import org.apache.log4j.Logger;
import wpn.hdri.util.servlet.ServletUtils;
import wpn.hdri.web.backend.ApplicationPostServlet;
import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.backend.RequestParameter;
import wpn.hdri.web.data.Users;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;

import static wpn.hdri.web.backend.CommonRequestParameters.DATA;
import static wpn.hdri.web.backend.CommonRequestParameters.DATA_SET_NAME;

/**
 * Handles main submit request from the user.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 25.01.12
 */
//TODO replace with jsp
public final class SubmitDataHandler extends ApplicationPostServlet {
    public static final RequestParameter META = new RequestParameter() {
        @Override
        public String getAlias() {
            return "meta";
        }
    };
    public static final RequestParameter IS_OFFLINE_SUBMIT = new RequestParameter() {
        @Override
        public String getAlias() {
            return "is-offline";
        }
    };

    private static final Logger LOG = Logger.getLogger(SubmitDataHandler.class);

    private final SubmitHelper offlineHelper = new OfflineSubmitHelper();
    private final SubmitHelper onlineHelper = new OnlineSubmitHelper();

    //TODO refactor: data set should encapsulate meta entirely
    @Override
    protected String doPostInternal(Users.User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException {
        LOG.info("Processing submit request from " + user.getName() + "@" + req.getRemoteHost());
        boolean offlineSubmit = Boolean.parseBoolean(requestParameters.get(IS_OFFLINE_SUBMIT));

        if (offlineSubmit) {
            offlineHelper.processSubmit(user, requestParameters, req, getStorage(), getApplicationContext(), LOG);
        } else {
            onlineHelper.processSubmit(user, requestParameters, req, getStorage(), getApplicationContext(), LOG);
        }
        LOG.info("Finish processing submit request from " + user.getName() + "@" + req.getRemoteHost());
        return getSuccessHtml(req).toString();
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

    @Override
    protected Collection<? extends RequestParameter> getUsedRequestParameters() {
        Collection<RequestParameter> requestParameters = new HashSet<RequestParameter>();
        requestParameters.addAll(EnumSet.of(DATA, DATA_SET_NAME));
        requestParameters.add(META);
        requestParameters.add(IS_OFFLINE_SUBMIT);
        return requestParameters;
    }
}

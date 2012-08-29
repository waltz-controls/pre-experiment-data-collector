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

import wpn.hdri.web.data.Users;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Supports only POST method. Attempt to call GET method will fail.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 26.03.12
 */
public abstract class ApplicationPostServlet extends BaseServlet {
    /**
     * Throws UnsupportedOperationException
     *
     * @param req
     * @return
     * @throws UnsupportedOperationException wrapped by BackendException
     */
    @Override
    protected String doGetInternal(Users.User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException {
        throw new BackendException("This servlet [" + getClass().getName() + "] does not support GET method", new UnsupportedOperationException());
    }
}

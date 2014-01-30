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

import hzg.wpn.hdri.predator.meta.MetaDataFactory;
import hzg.wpn.hdri.predator.meta.MetaDataHelpers;
import hzg.wpn.hdri.predator.meta.json.JsonMetaSource;
import hzg.wpn.hdri.predator.meta.json.JsonStringFactory;
import org.apache.commons.beanutils.DynaBean;
import org.apache.xerces.impl.dv.util.Base64;
import org.junit.Before;
import org.junit.Test;
import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.UsefulTestConstants;
import wpn.hdri.web.backend.ApplicationServlet;
import wpn.hdri.web.data.User;
import wpn.hdri.web.data.Users;
import wpn.hdri.web.storage.Storage;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 29.05.12
 */
public class SubmitDataHandlerTest {
    private SubmitDataHandler instance;
    private MetaDataHelpers metaDataHelpers = new MetaDataHelpers(new MetaDataFactory<JsonMetaSource>(new JsonStringFactory(UsefulTestConstants.TEST_META_DATA_JSON)));
    private Storage<DynaBean> mockStorage = mock(Storage.class);

    @Before
    public void before() throws Exception {
        instance = new SubmitDataHandler();

        ApplicationContext appCtx = new ApplicationContext(null, null, UsefulTestConstants.TEST_BEAMTIME_ID, mockStorage, null, metaDataHelpers);

        ServletContext ctx = mock(ServletContext.class);

        doReturn(appCtx).when(ctx).getAttribute(ApplicationServlet.APPLICATION_CONTEXT);

        ServletConfig config = mock(ServletConfig.class);
        doReturn(ctx).when(config).getServletContext();

        instance.doInitInternal(config);
    }

    @Test
    public void testDoPostInternal_FreshDataSet() throws Exception {
        String encodedMeta = Base64.encode(UsefulTestConstants.TEST_META_DATA_JSON.getBytes());

        String encodedData = Base64.encode(UsefulTestConstants.TEST_DATA.getBytes());

        HttpServletRequest req = mock(HttpServletRequest.class);
        doReturn("test-data-set").when(req).getParameter("data-set-name");
        doReturn("true").when(req).getParameter("is-offline");
        doReturn(encodedMeta).when(req).getParameter("meta");
        doReturn(encodedData).when(req).getParameter("data");
        doReturn(this.getClass().getSimpleName()).when(req).getRemoteHost();

        doReturn(Users.TEST_USER.getName()).when(req).getRemoteUser();

        HttpServletResponse res = mock(HttpServletResponse.class);

        instance.doPostInternal(req, res);

        verify(mockStorage, atLeastOnce()).save(any(DynaBean.class), any(User.class), eq("test-data-set"), any(ApplicationContext.class));
    }
}

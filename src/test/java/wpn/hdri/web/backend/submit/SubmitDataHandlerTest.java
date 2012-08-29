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

import org.apache.commons.beanutils.DynaBean;
import org.apache.xerces.impl.dv.util.Base64;
import org.junit.Before;
import org.junit.Test;
import wpn.hdri.util.reflection.ReflectionUtils;
import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.UsefulTestConstants;
import wpn.hdri.web.backend.CommonRequestParameters;
import wpn.hdri.web.backend.RequestParameter;
import wpn.hdri.web.data.Users;
import wpn.hdri.web.meta.MetaDataFactory;
import wpn.hdri.web.meta.MetaDataHelpers;
import wpn.hdri.web.meta.json.JsonMetaSource;
import wpn.hdri.web.meta.json.JsonStringFactory;
import wpn.hdri.web.storage.Storage;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;

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
    public void before() throws Exception{
        instance = new SubmitDataHandler();

        Field storage = ReflectionUtils.getDeclaredField("storage",SubmitDataHandler.class);
        try{
            storage.setAccessible(true);
            storage.set(instance, mockStorage);
        } finally {
            storage.setAccessible(false);
        }

        Field applicationContext = ReflectionUtils.getDeclaredField("applicationContext",SubmitDataHandler.class);
        try{
            applicationContext.setAccessible(true);
            applicationContext.set(instance, new ApplicationContext(null,null,UsefulTestConstants.TEST_BEAMTIME_ID, mockStorage,null, metaDataHelpers));
        } finally {
            applicationContext.setAccessible(false);
        }
    }

    @Test
    public void testDoPostInternal_FreshDataSet() throws Exception{
        HttpServletRequest req = mock(HttpServletRequest.class);

        HashMap<RequestParameter, String> requestParameters = new HashMap<RequestParameter, String>();

        requestParameters.put(CommonRequestParameters.DATA_SET_NAME,"test-data-set");
        requestParameters.put(SubmitDataHandler.IS_OFFLINE_SUBMIT,"true");

        String encodedMeta = Base64.encode(UsefulTestConstants.TEST_META_DATA_JSON.getBytes());
        requestParameters.put(SubmitDataHandler.META,encodedMeta);

        String encodedData = Base64.encode(UsefulTestConstants.TEST_DATA.getBytes());
        requestParameters.put(CommonRequestParameters.DATA,encodedData);

        instance.doPostInternal(Users.TEST_USER, requestParameters, req);

        verify(mockStorage,atLeastOnce()).save(any(DynaBean.class),same(Users.TEST_USER),eq("test-data-set"),any(ApplicationContext.class));
    }
}

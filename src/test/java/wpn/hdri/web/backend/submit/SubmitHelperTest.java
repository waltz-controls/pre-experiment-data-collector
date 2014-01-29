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
import org.junit.Test;
import org.slf4j.Logger;
import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.UsefulTestConstants;
import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.data.DataSet;
import wpn.hdri.web.data.User;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 31.05.12
 */
public class SubmitHelperTest {
    @Test
    public void testGetDataValues() throws Exception {
        SubmitHelper instance = new SubmitHelper() {
            @Override
            protected DataSet processSubmitInternal(User user, SubmitDataHandler.Parameters requestParameters, String dataSetName, ApplicationContext applicationContext, Logger log) throws BackendException {
                return null;
            }
        };

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("data", Base64.encode(UsefulTestConstants.TEST_DATA.getBytes(Charset.forName("utf-8"))));

        DynaBean result = instance.getDataSetValues(
                requestParameters.get("data"),
                UsefulTestConstants.TEST_META_DATA_HELPER.createMetaData()
        );

        assertEquals("some value", result.get("string"));
        assertEquals(1234, result.get("number"));
        //TODO arr
    }
}

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

package wpn.hdri.web.data;

import org.junit.Before;
import org.junit.Test;
import wpn.hdri.web.meta.MetaField;
import wpn.hdri.web.meta.MetaForm;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static wpn.hdri.web.UsefulTestConstants.*;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 28.03.12
 */
public class DataSetsTest {
    @Before
    public void before() {

    }

//    @Test
//    public void testUpdateFromString() throws Exception{
//        DataSet dataSet = DataSets.createDataSet(
//                UsefulTestConstants.TEST_USER,
//                UsefulTestConstants.TEST_META_DATA_HELPER.createMetaData(),
//                UsefulTestConstants.TEST_BEAMTIME_ID.getValue(),"test");
//        DataSet result = DataSets.update(UsefulTestConstants.TEST_DATA, dataSet);
//    }

    @Test
    public void testCreateDataSet() throws Exception {
        DataSet result = DataSets.createDataSet(
                TEST_USER, TEST_META_DATA_HELPER.createMetaData(), TEST_BEAMTIME_ID, "test-data");

        //check form
        MetaForm metaForm = result.getMeta().getForms().get(0);
        assertEquals("test", metaForm.getId());
        assertEquals("Test form", metaForm.getName());
        assertEquals("Some text with hints", metaForm.getHelp());
        assertEquals("fieldset", metaForm.getType());

        //check field
        MetaField metaField = metaForm.getFields().get(1);
        assertEquals("Test number", metaField.getName());
        assertEquals("number", metaField.getId());
        assertEquals("Field with number value", metaField.getDescription());
        assertEquals("number", metaField.getType());
        assertEquals("", metaField.getValidation());

        //all data is null
        //TODO support arrays
//        assertNull(result.get("string"));
//        assertNull(result.get("number"));
//        assertNull(result.get("arr"));
    }
}

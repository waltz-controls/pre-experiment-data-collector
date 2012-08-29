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

package wpn.hdri.web.meta;

import org.junit.Test;
import wpn.hdri.web.backend.submit.SubmitHelper;
import wpn.hdri.web.meta.core.MetaStructure;
import wpn.hdri.web.meta.json.JsonMetaSource;
import wpn.hdri.web.meta.json.JsonStreamFactory;

import java.util.Iterator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 31.01.12
 */
public class MetaDataHelpersTest {
//    public static final String PATH_TO_UPLOAD_TEST = "/Test.Upload.MetaData.json";
//    public static final String PATH_TO_TEST = "/Test.MetaData.json";
//
//    @Test
//    public void testCreateInstance() throws Exception {
//        MetaData meta = MetaDataHelper.createInstance(PATH_TO_TEST);
//
//        //==== check forms ====
//        assertNotNull(meta.forms);
//        assertNotNull(meta.forms[0]);
//        MetaForm metaForm = meta.forms[0];
//        assertEquals("Test form", metaForm.name);
//        assertEquals("test", metaForm.getId());
//        assertEquals("Some text with hints", metaForm.help);
//        //==== check fields ====
//        assertNotNull(metaForm.fields);
//        assertEquals(3, metaForm.fields.length);
//        //==== check string field ====
//        MetaField stringFld = metaForm.fields[0];
//        assertNotNull(stringFld);
//        assertEquals("Test string", stringFld.name);
//        assertEquals("string",stringFld.getId());
//        assertEquals("Field with string value",stringFld.description);
//        assertEquals("string",stringFld.getType());
//        assertEquals("required",stringFld.validation);
//        //==== check number field ====
//        MetaField numberFld = metaForm.fields[1];
//        assertNotNull(numberFld);
//        assertEquals("Test number", numberFld.name);
//        assertEquals("number",numberFld.getId());
//        assertEquals("Field with number value",numberFld.description);
//        assertEquals("number",numberFld.getType());
//        assertEquals("",numberFld.validation);
//        //==== check array field ====
//        MetaField arrFld = metaForm.fields[2];
//        assertNotNull(arrFld);
//        assertEquals("Test strings array", arrFld.name);
//        assertEquals("arr",arrFld.getId());
//        assertEquals("Field with array of string value",arrFld.description);
//        assertEquals("file_multiply",arrFld.getType());
//        assertEquals("",arrFld.validation);
//    }
//
//    @Test
//    public void testUpdate() throws Exception {
//        MetaData meta = MetaDataHelper.createInstance(PATH_TO_TEST);
//
//        String json = "{\"test\":{\"string\":\"Test String\",\"number\":\"12345\",\"arr\":\"1;2;3\"}}";
//
//        DynaBean result = MetaDataHelper.update(json, meta);
//
//        assertEquals("Test String",PropertyUtils.getNestedProperty(result,"test.string"));
//        assertEquals(12345, PropertyUtils.getNestedProperty(result, "test.number"));
//        assertArrayEquals(new String[]{"1","2","3"}, (String[]) PropertyUtils.getNestedProperty(result, "test.arr"));
//    }
//
//
//
//    @Test()
//    public void testUpdate_Upload() throws Exception {
//        MetaData meta = MetaDataHelper.createInstance(PATH_TO_UPLOAD_TEST);
//
//        String json = "{\"upload\":{\"pdfDoc\":\"test.pdf\",\"tifImg\":\"1.tif;2.tif;3.tif\"}}";
//
//        DynaBean result = MetaDataHelper.update(json, meta);
//
//        assertEquals("test.pdf",PropertyUtils.getNestedProperty(result,"upload.pdfDoc"));
//        assertArrayEquals(new String[]{"1.tif","2.tif","3.tif"}, (String[]) PropertyUtils.getNestedProperty(result, "upload.tifImg"));
//    }
//
//    @Test(expected = Exception.class)
//    public void testUpdate_MissingProperty() throws Exception {
//        MetaData meta = MetaDataHelper.createInstance(PATH_TO_TEST);
//
//        String json = "{\"test\":{\"string\":\"Test String\",\"arr\":\"1;2;3\"}}";
//
//        DynaBean result = MetaDataHelper.update(json, meta);
//    }
//
//    @Test(expected = Exception.class)
//    public void testUpdate_BadValue() throws Exception {
//        MetaData meta = MetaDataHelper.createInstance(PATH_TO_TEST);
//
//        String json = "{\"test\":{\"string\":\"Test String\",\"number\":\"1xxx5\",\"arr\":\"1;2;3\"}}";
//
//        DynaBean result = MetaDataHelper.update(json, meta);
//    }
    @Test
    public void testCompare() throws Exception{
        MetaDataHelpers helpers = new MetaDataHelpers(new MetaDataFactory<JsonMetaSource>(new JsonStreamFactory("/Test.MetaData.json")));
        MetaData meta1 = helpers.createMetaData();
        MetaData meta2 = helpers.createMetaData();

        assertEquals(0,MetaDataHelpers.compare(meta1,meta2));
    }

    @Test
    public void testCompare_Different() throws Exception{
        MetaDataHelpers helpers1 = new MetaDataHelpers(new MetaDataFactory<JsonMetaSource>(new JsonStreamFactory("/Test.MetaData.json")));
        MetaData meta1 = helpers1.createMetaData();

        MetaDataHelpers helpers2 = new MetaDataHelpers(new MetaDataFactory<JsonMetaSource>(new JsonStreamFactory("/Test.Choice.MetaData.json")));
        MetaData meta2 = helpers2.createMetaData();

        assertEquals(-1,MetaDataHelpers.compare(meta1,meta2));
    }

    @Test
    public void testGetAllFieldsWithValues() throws Exception {
        MetaDataHelpers helpers = new MetaDataHelpers(new MetaDataFactory<JsonMetaSource>(new JsonStreamFactory("/Test.MetaData.json")));
        MetaData meta = helpers.createMetaData();

        String jsonRequest = "{\"test\":{\n" +
                "                    \"string\":\"Some test value\",\n" +
                "                    \"number\":\"1234\",\n" +
                "                    \"arr\":\"a;b\"\n" +
                "                }\n" +
                "    }";

        Iterator<MetaStructure<? extends MetaStructure<?>>.Wrapper<? extends MetaStructure<?>, ?>> result = SubmitHelper.getAllFieldsWithValues(jsonRequest, meta).iterator();

        MetaStructure.Wrapper field0 = result.next();
        assertEquals("Some test value", field0.getValue());

        MetaStructure.Wrapper field1 = result.next();
        assertEquals(1234, field1.getValue());

        MetaStructure.Wrapper field2 = result.next();
        assertEquals("a;b", field2.getValue());
    }
}

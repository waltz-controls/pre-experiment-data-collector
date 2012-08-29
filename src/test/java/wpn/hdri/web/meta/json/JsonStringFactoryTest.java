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

package wpn.hdri.web.meta.json;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 05.03.12
 */
public class JsonStringFactoryTest {
    @Test
    public void testCreateInstance() throws Exception {
        JsonStringFactory instance = new JsonStringFactory("{\n" +
                "    \"forms\":[{\n" +
                "        \"name\":\"Test form\",\n" +
                "        \"id\":\"test\",\n" +
                "        \"help\":\"Some text with hints\",\n" +
                "        \"type\":\"fieldset\",\n" +
                "        \"fields\":[\n" +
                "                {\n" +
                "                    \"name\":\"Test string\",\n" +
                "                    \"id\":\"string\",\n" +
                "                    \"description\":\"Field with string value\",\n" +
                "                    \"type\":\"string\",\n" +
                "                    \"validation\":\"required\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\":\"Test number\",\n" +
                "                    \"id\":\"number\",\n" +
                "                    \"description\":\"Field with number value\",\n" +
                "                    \"type\":\"number\",\n" +
                "                    \"validation\":\"\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\":\"Test strings array\",\n" +
                "                    \"id\":\"arr\",\n" +
                "                    \"description\":\"Field with array of string value\",\n" +
                "                    \"type\":\"file_multiply\",\n" +
                "                    \"validation\":\"\"\n" +
                "                }\n" +
                "        ]\n" +
                "    }]\n" +
                "}");

        JsonMetaSource result = instance.createInstance();

        assertNotNull(result.get("forms"));

        JsonMetaSource forms = result.get("forms");

        assertNotNull(forms.get(0));

        JsonMetaSource form0 = forms.get(0);
        //assertSame(form0,result.get("forms",0));

        assertEquals("Test form", form0.get("name").getValue());
        //assertEquals("test",form0.getId());
        assertEquals("test", form0.get("id").getValue());
        assertEquals("Some text with hints", form0.get("help").getValue());

        assertNotNull(form0.get("fields"));

        JsonMetaSource fields = form0.get("fields");

        assertNotNull(fields.get(0));

        JsonMetaSource field0 = fields.get(0);

        assertEquals("Test string", field0.get("name").getValue());
        assertEquals("string", field0.get("id").getValue());
        assertEquals("Field with string value", field0.get("description").getValue());
        assertEquals("string", field0.get("type").getValue());
        assertEquals("required", field0.get("validation").getValue());
    }

    @Test
    public void testCreateInstance_Choice() throws Exception {
        JsonStringFactory instance = new JsonStringFactory("{\n" +
                "    \"forms\":[\n" +
                "        {\n" +
                "            \"name\":\"Multichoice test form\",\n" +
                "            \"id\":\"multichoice\",\n" +
                "            \"type\":\"multichoice\",\n" +
                "            \"help\":\"Test\",\n" +
                "            \"fields\":[\n" +
                "                {\n" +
                "                    \"name\":\"Choice 1\",\n" +
                "                    \"id\":\"choice1\",\n" +
                "                    \"description\":\"\",\n" +
                "                    \"type\":\"choice\",\n" +
                "                    \"fields\":[\n" +
                "                        {\n" +
                "                            \"name\":\"Field 1\",\n" +
                "                            \"id\":\"field-1\",\n" +
                "                            \"description\":\"Field 1\",\n" +
                "                            \"type\":\"string\",\n" +
                "                            \"validation\":\"\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"name\":\"Field 2\",\n" +
                "                            \"id\":\"field-2\",\n" +
                "                            \"description\":\"Field 2\",\n" +
                "                            \"type\":\"number\",\n" +
                "                            \"validation\":\"\"\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}");

        JsonMetaSource result = instance.createInstance();

        assertNotNull(result.get("forms"));

        JsonMetaSource forms = result.get("forms");

        assertNotNull(forms.get(0));

        JsonMetaSource form0 = forms.get(0);
        //assertSame(form0,result.get("forms",0));

        assertEquals("Multichoice test form", form0.get("name").getValue());
        //assertEquals("test",form0.getId());
        assertEquals("multichoice", form0.get("id").getValue());
        assertEquals("multichoice", form0.get("type").getValue());

        assertNotNull(form0.get("fields"));

        JsonMetaSource fields = form0.get("fields");

        assertNotNull(fields.get(0));

        JsonMetaSource field0 = fields.get(0);

        assertEquals("Choice 1", field0.get("name").getValue());
        assertEquals("choice1", field0.get("id").getValue());
        assertEquals("choice", field0.get("type").getValue());

        assertNotNull(field0.get("fields"));

        JsonMetaSource nestedFields = field0.get("fields");

        assertNotNull(nestedFields.get(1));

        JsonMetaSource nestedField1 = nestedFields.get(1);

        assertEquals("Field 2", nestedField1.get("name").getValue());
        assertEquals("field-2", nestedField1.get("id").getValue());
        assertEquals("number", nestedField1.get("type").getValue());
    }
}

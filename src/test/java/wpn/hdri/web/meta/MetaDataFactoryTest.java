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

import org.junit.Before;
import org.junit.Test;
import wpn.hdri.web.meta.core.TypeConverter;
import wpn.hdri.web.meta.core.TypeConverters;
import wpn.hdri.web.meta.json.JsonMetaSource;
import wpn.hdri.web.meta.json.JsonStreamFactory;

import java.util.Iterator;

import static junit.framework.Assert.*;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 06.03.12
 */
public class MetaDataFactoryTest {
    @Before
    public void before() {
        TypeConverters.registerConverter(Visibility.class, new TypeConverter<Visibility>() {
            public Visibility convert(String data) throws Exception {
                return Visibility.forAlias(data);
            }
        });
        MetaConversionStrategy.initialize();
    }

    @Test
    public void testCreateMetaData() throws Exception {
        MetaDataFactory<JsonMetaSource> instance = new MetaDataFactory<JsonMetaSource>(new JsonStreamFactory("/Test.MetaData.json"));

        MetaData result = instance.createMetaData();

        assertNotNull(result.getForms());

        MetaForm form0 = (MetaForm) result.getForms().get(0);

        assertEquals("Test form", form0.getName());
        assertEquals("test", form0.getId());
        assertEquals("fieldset", form0.getType());
        assertEquals("Some text with hints", form0.getHelp());

        assertNotNull(form0.getAllFields());

        Iterator it = form0.getAllFields().iterator();
        it.next();
        it.next();
        MetaField field2 = (MetaField) it.next();

        assertEquals("Test strings array", field2.getName());
        assertEquals("arr", field2.getId());
        assertEquals("file_multiply", field2.getType());
        assertEquals("Field with array of string value", field2.getDescription());
        assertEquals("", field2.getValidation());
    }

    @Test
    public void testCreateMetaData_Multichoice() throws Exception {
        MetaDataFactory<JsonMetaSource> instance = new MetaDataFactory<JsonMetaSource>(new JsonStreamFactory("/Test.Choice.MetaData.json"));

        MetaData result = instance.createMetaData();

        assertNotNull(result.getForms());

        MetaForm form0 = (MetaForm) result.getForms().get(0);

        assertEquals("Multichoice test form", form0.getName());
        assertEquals("multichoice", form0.getId());
        assertEquals("multichoice", form0.getType());
        assertEquals("Test", form0.getHelp());

        assertNotNull(form0.getAllFields());

        MetaChoice field0 = (MetaChoice) form0.getAllFields().iterator().next();

        assertEquals("Choice 1", field0.getName());
        assertEquals("choice1", field0.getId());
        assertEquals("choice", field0.getType());
        assertEquals("", field0.getDescription());
        assertNull(field0.getValidation());

        assertNotNull(field0.getAllFields());

        Iterator<MetaField> it = field0.getAllFields().iterator();
        it.next();
        it.next();
        MetaField nestedField1 = (MetaField) it.next();

        assertEquals("Field 2", nestedField1.getName());
        assertEquals("field-2", nestedField1.getId());
        assertEquals("number", nestedField1.getType());
        assertEquals("Field 2", nestedField1.getDescription());
        assertEquals("", nestedField1.getValidation());
    }

    @Test
    public void testCreateMetaData_Visibility() throws Exception {
        MetaDataFactory<JsonMetaSource> instance = new MetaDataFactory<JsonMetaSource>(new JsonStreamFactory("/Test.Visibility.MetaData.json"));

        MetaData result = instance.createMetaData();

        assertNotNull(result.getForms());

        MetaForm form0 = (MetaForm) result.getForms().get(0);

        assertEquals("Test visibility form", form0.getName());
        assertEquals("frmVisibility", form0.getId());
        assertEquals("fieldset", form0.getType());
        assertEquals("", form0.getHelp());

        Iterable<MetaField> allFields = form0.getAllFields();
        assertNotNull(allFields);

        Iterator<MetaField> iterator = allFields.iterator();
        MetaField field0 = iterator.next();

        assertEquals("Visibility WEB", field0.getName());
        assertEquals("fldVisWeb", field0.getId());
        assertEquals("string", field0.getType());
        assertEquals("", field0.getDescription());
        assertNull(field0.getValidation());
        assertSame(Visibility.WEB, field0.getVisibility());

        MetaField field1 = iterator.next();

        assertEquals("Visibility TANGO", field1.getName());
        assertEquals("fldVisTng", field1.getId());
        assertEquals("string", field1.getType());
        assertEquals("", field1.getDescription());
        assertNull(field1.getValidation());
        assertSame(Visibility.TANGO, field1.getVisibility());
    }

    @Test
    public void testCreateMetaData_Readonly() throws Exception {
        MetaDataFactory<JsonMetaSource> instance = new MetaDataFactory<JsonMetaSource>(new JsonStreamFactory("/Test.Readonly.MetaData.json"));

        MetaData result = instance.createMetaData();

        assertNotNull(result.getForms());

        MetaForm form0 = (MetaForm) result.getForms().get(0);

        assertEquals("Test readonly form", form0.getName());
        assertEquals("frmReadonly", form0.getId());
        assertEquals("fieldset", form0.getType());
        assertNull(form0.getHelp());

        Iterable<MetaField> allFields = form0.getAllFields();
        assertNotNull(allFields);

        Iterator<MetaField> iterator = allFields.iterator();
        MetaField field0 = iterator.next();

        assertEquals("Readonly TRUE", field0.getName());
        assertTrue(field0.isReadonly());

        MetaField field1 = iterator.next();

        assertEquals("Readonly FALSE", field1.getName());
        assertFalse(field1.isReadonly());
    }

    @Test
    public void testValidate() throws Exception {
        //TODO
    }
}

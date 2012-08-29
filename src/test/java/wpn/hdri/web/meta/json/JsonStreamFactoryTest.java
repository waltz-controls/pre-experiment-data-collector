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
 * @since 28.03.12
 */
public class JsonStreamFactoryTest {
    @Test
    public void testCreateInternal() throws Exception {
        JsonStreamFactory instance = new JsonStreamFactory("/Test.MetaData.json");

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
}

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

import com.google.gson.JsonObject;
import org.apache.commons.collections.keyvalue.UnmodifiableMapEntry;
import org.junit.Test;

import static junit.framework.Assert.*;
import static wpn.hdri.web.meta.json.JsonUtils.createObject;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 05.03.12
 */
public class JsonMetaSourceTest {
    @Test
    public void testContains() throws Exception {
        JsonObject obj = createObject(new UnmodifiableMapEntry("value", "test"));

        JsonMetaSource instance = new JsonMetaSource("root", obj);

        assertTrue(instance.contains("value", ""));
        assertFalse(instance.contains("test", ""));
    }

    @Test
    public void testContains_Nested() throws Exception {
        JsonObject obj = createObject(new UnmodifiableMapEntry("value", "test"), new UnmodifiableMapEntry("some", new UnmodifiableMapEntry("nested", "test")));

        JsonMetaSource instance = new JsonMetaSource("root", obj);

        assertTrue(instance.contains("nested", "some"));
        assertFalse(instance.contains("test", "some"));
    }

    @Test
    public void testGet_Property() throws Exception {
        JsonObject obj = createObject(new UnmodifiableMapEntry("value", "test"));

        JsonMetaSource instance = new JsonMetaSource("root", obj);

        assertEquals("test", instance.get("value").getValue());
        assertEquals("test", instance.getValue());
        assertNull(instance.get("test"));
    }

    @Test
    public void testGet_Indexed() throws Exception {
        JsonObject obj = createObject(new UnmodifiableMapEntry("value", new String[]{"test1", "test2", "test3", "test4"}));

        JsonMetaSource instance = new JsonMetaSource("root", obj);

        assertEquals("test3", instance.get("value", 2).getValue());
        assertEquals("test2", instance.get(1).getValue());
        assertNull(instance.get("test"));
    }

    @Test
    public void testGet_Nested() throws Exception {
        JsonObject obj = createObject(new UnmodifiableMapEntry("some", new UnmodifiableMapEntry("value", "test")));

        JsonMetaSource instance = new JsonMetaSource("root", obj);

        assertEquals("test", instance.get("value", "some").getValue());
        assertEquals("test", instance.get("some").getValue());
        assertNull(instance.get("test"));
    }

    @Test
    public void testGetDynaClass() throws Exception {
        //TODO
    }

    @Test
    public void testCache() throws Exception {
        //TODO
    }
}

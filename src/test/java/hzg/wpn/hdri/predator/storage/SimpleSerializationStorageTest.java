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

package hzg.wpn.hdri.predator.storage;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.junit.Test;

import java.io.File;
import java.io.Serializable;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 16.03.12
 */
public class SimpleSerializationStorageTest {
    @Test
    public void testSave_LoadInternal() throws Exception {
        SimpleSerializationStorage<DynaBean> instance = new SimpleSerializationStorage<DynaBean>();

        DynaBean expected = createTestData();

        DynaBean data = createTestData();

        instance.saveInternal(data, new File("/test"));

        DynaBean result = instance.loadInternal(new File("/test"));

        assertEquals(expected.get("string"), result.get("string"));
        assertEquals(expected.get("number"), result.get("number"));
        assertEquals(expected.get("int"), result.get("int"));
        assertArrayEquals((String[]) expected.get("arr"), (String[]) result.get("arr"));
        assertEquals(((Custom) expected.get("nested")).string, ((Custom) result.get("nested")).string);
    }

    private DynaBean createTestData() {
        DynaBean data = new LazyDynaBean();
        data.set("bool", true);
        data.set("number", 1234);
        data.set("string", "Hello World!");
        data.set("arr", new String[]{"a", "b", "c"});
        Custom custom = new Custom("Hello Nested.");
        data.set("nested", custom);
        return data;
    }

    private static class Custom implements Serializable {
        private final String string;

        private Custom(String string) {
            this.string = string;
        }
    }
}

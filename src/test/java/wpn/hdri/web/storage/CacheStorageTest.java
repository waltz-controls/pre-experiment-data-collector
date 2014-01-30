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

package wpn.hdri.web.storage;

import hzg.wpn.hdri.predator.ApplicationContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import wpn.hdri.ConcurrentUtils;
import wpn.hdri.web.data.User;
import wpn.hdri.web.data.Users;

import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.*;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 27.03.12
 */
public class CacheStorageTest {
    private Storage<TestData> mock;
    private CacheStorage<TestData> instance;

    @Before
    public void before() {
        mock = mock(Storage.class);
        instance = new CacheStorage<TestData>(mock);
    }

    @After
    public void after() {
        instance = null;
        mock = null;
    }

    @Test
    public void testSaveLoad() throws Exception {
        TestData data = new TestData();

        instance.save(data, ApplicationContext.NULL);

        verify(mock).save(data, ApplicationContext.NULL);

        TestData result = instance.load("test-save-load", ApplicationContext.NULL);

        verify(mock, never()).load("test-save-load", ApplicationContext.NULL);

        assertEquals("ABC", result.string);
        assertEquals(1234, result.number);
        assertArrayEquals(new String[]{"Hello", "World", "!"}, result.arr);
    }

    //@Test
    public void testCleanup() throws Exception {
        instance = new CacheStorage<TestData>(mock, 100, 100, TimeUnit.MILLISECONDS);

        //save data several times
        instance.save(new TestData(), ApplicationContext.NULL);
        instance.save(new TestData(), ApplicationContext.NULL);
        instance.save(new TestData(), ApplicationContext.NULL);

        verify(mock).save(new TestData(), ApplicationContext.NULL);
        //await until cache is cleaned up
        Thread.sleep(500);

        //save data several times more
        instance.save(new TestData(), ApplicationContext.NULL);
        instance.save(new TestData(), ApplicationContext.NULL);
        instance.save(new TestData(), ApplicationContext.NULL);

        //save should be called for the second time as instance.cachedValues is now empty
        verify(mock, times(2)).save(new TestData(), ApplicationContext.NULL);
    }

    @Test
    public void testSave_Concurrently() throws Exception {
        ConcurrentUtils.testConcurrently(instance.getClass().getMethod("save", Object.class, User.class, String.class, ApplicationContext.class), instance,
                //args
                new TestData(), Users.TEST_USER, "test-data", ApplicationContext.NULL);

        //underlying load method should be called only once
        verify(mock).save(new TestData(), ApplicationContext.NULL);
    }

    @Test
    public void testLoad_Concurrently() throws Exception {
        ConcurrentUtils.testConcurrently(instance.getClass().getMethod("load", User.class, String.class, ApplicationContext.class), instance,
                //args
                Users.TEST_USER, "test-data", ApplicationContext.NULL);

        //underlying load method should be called only once
        verify(mock).load("test-data", ApplicationContext.NULL);
    }

    @Test
    public void testSaveLoad_Updated() throws Exception {
        TestData data = new TestData();

        //store data
        instance.save(data, ApplicationContext.NULL);
        verify(mock).save(data, ApplicationContext.NULL);

        //load should return cached value
        TestData loaded1 = instance.load("test-data", ApplicationContext.NULL);
        verify(mock, never()).load("test-data", ApplicationContext.NULL);

        assertEquals("ABC", loaded1.string);
        assertEquals(1234, loaded1.number);
        assertArrayEquals(new String[]{"Hello", "World", "!"}, loaded1.arr);

        loaded1 = null;//prevent further usage

        //change data and store
        //this simulates DataSets.update use case
        data.string = "qwerty";
        instance.save(data, ApplicationContext.NULL);
        //underlying save second invocation
        verify(mock, times(2)).save(data, ApplicationContext.NULL);

        TestData loaded2 = instance.load("test-data", ApplicationContext.NULL);
        verify(mock, never()).load("test-data", ApplicationContext.NULL);

        assertEquals("qwerty", loaded2.string);
    }

    private static class TestData {
        private String string = "ABC";
        private int number = 1234;
        private String[] arr = new String[]{"Hello", "World", "!"};

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestData testData = (TestData) o;

            if (string != null ? !string.equals(testData.string) : testData.string != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return string != null ? string.hashCode() : 0;
        }
    }
}

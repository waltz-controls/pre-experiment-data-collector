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

import org.junit.Test;
import wpn.hdri.ConcurrentUtils;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertTrue;
import static wpn.hdri.web.data.Users.User;

/**
 * Created by khokhria
 * on 12.03.12
 */
public class UsersTest {
    @Test
    public void testCreate_concurrently() throws Exception {
        final Set<User> result = new HashSet<User>(4);

        final String userName = "Test";

        ConcurrentUtils.testConcurrently(new Runnable() {
            @Override
            public void run() {
                //users are compared by reference
                //at the moment this test was created User does not override .equals and .hashCode
                //if User overrides these methods test should be rewritten in a way it compares resulting Users
                //as the same object
                result.add(Users.createUser(userName));
            }
        });

        assertTrue(result.size() == 1);
    }

    @Test
    public void testGetUser_concurrently() throws Exception {
        final Set<User> result = new HashSet<User>();

        final String userName = "Test";

        ConcurrentUtils.testConcurrently(new Runnable() {
            @Override
            public void run() {
                //return value may be cached from the previous test
                //but it does not matter
                //the important thing is that the same user will be returned
                result.add(Users.getUser(userName, true));
            }
        });

        assertTrue(result.size() == 1);
    }
}

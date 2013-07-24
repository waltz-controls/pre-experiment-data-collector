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

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import wpn.hdri.web.ApplicationContext;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Provides useful utility method over {@link Users} instances.
 * <p/>
 * Implementation provides the same level of thread safeness as {@link ConcurrentHashMap}.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 12.03.12
 */
@ThreadSafe
public class Users {
    public static final User TEST_USER = new User("Test");

    private static ConcurrentMap<String, User> users = new ConcurrentHashMap<String, User>();

    /**
     * Returns stored user instance or null. If create is set to true new User instance will be returned.
     * <p/>
     * Implementation is not thread safe.
     *
     * @param userName a name of the new user
     * @param create   indicates whether new instance will be created
     * @return a new User instance or null
     */
    public static User getUser(String userName, boolean create) {
        return getUser(userName, create, null);
    }

    /**
     * Returns stored user instance or null.
     * <p/>
     * If ctx is not null and there is no stored user with the name an attempt to load a user from disk will be made.
     * <p/>
     * If create is set to true new User instance will be returned.
     * <p/>
     * Implementation is thread safe due to {@link ConcurrentHashMap} usage.
     *
     * @param userName a name of the new user
     * @param create   indicates whether new instance will be created
     * @param ctx      application context. May be null.
     * @return a new User instance or null
     */
    public static User getUser(String userName, boolean create, ApplicationContext ctx) {
        User user = users.get(userName);

        if (user != null) {
            return user;
        }

        //try to load user from disk
        if (ctx != null) {
            user = loadUser(userName, ctx);
        }

        if (user == null && create) {
            //force new user if true
            return createUser(userName);
        } else {
            //return the newest user at the moment or null
            return users.get(userName);
        }
    }

    /**
     * Creates new {@link User} instance
     * <p/>
     * Implementation provides the same level of thread safeness as {@link ConcurrentHashMap}
     *
     * @param username
     * @return
     */
    public static User createUser(String username) {
        User user = new User(username);
        if (users.putIfAbsent(username, user) == null) {
            return user;
        } else {
            return users.get(username);
        }
    }

    /**
     * Loads a user from storage if exists otherwise returns null.
     *
     * @param userName
     * @param ctx
     * @return
     */
    public static User loadUser(String userName, ApplicationContext ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("Application context can not be null.");
        }

        try {
            File homeDir = ctx.getHomeDir();

            for (File userDir : homeDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY)) {
                if (userDir.getName().equals(userName)) {
                    return createUser(userName);
                }
            }
        } catch (Exception ignored) {
            //just return null
        }

        return null;
    }

    public static Collection<String> loadUserNames(ApplicationContext ctx) throws IOException {
        File homeDir = ctx.getHomeDir();

        Set<String> result = new HashSet<String>();
        for (File userDir : homeDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY)) {
            result.add(userDir.getName());
        }

        return result;
    }

}

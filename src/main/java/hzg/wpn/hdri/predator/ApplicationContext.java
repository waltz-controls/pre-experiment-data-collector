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

package hzg.wpn.hdri.predator;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import hzg.wpn.hdri.predator.data.DataSetsManager;
import hzg.wpn.hdri.predator.data.User;
import hzg.wpn.hdri.predator.meta.Meta;
import hzg.wpn.hdri.predator.storage.Storage;
import org.apache.commons.beanutils.DynaClass;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides a number of useful methods to deal with agreed file structure on the server.
 * <p/>
 * Extends {@link ServletContext}
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 13.03.12
 */
@Immutable
public class ApplicationContext {
    private final String realPath;
    private final String contextPath;
    private final String beamtimeId;
    private final Storage storage;
    private final ApplicationProperties properties;
    private final Meta meta;
    private final DynaClass dataClass;
    private final DataSetsManager manager;

    /**
     * @param realPath    ends with '/'
     * @param contextPath without '/' on the end.
     * @param beamtimeId
     * @param storage
     * @param properties
     * @param meta
     * @param dataClass
     * @param manager
     */
    public ApplicationContext(String realPath, String contextPath, String beamtimeId, Storage storage, ApplicationProperties properties, Meta meta, DynaClass dataClass, DataSetsManager manager) {
        this.realPath = realPath;
        this.contextPath = contextPath;
        this.beamtimeId = beamtimeId;
        this.storage = storage;
        this.properties = properties;
        this.meta = meta;
        this.dataClass = dataClass;
        this.manager = manager;
    }


    /**
     * Returns current beamtimeId associated with this application. BeamtimeId is usually stored in BeamtimeId.txt resource file.
     * It is assumed beamtimeId is being set once during the experiment installation setup.
     *
     * @return beamtimeId
     */
    public String getBeamtimeId() {
        return beamtimeId;
    }

    /**
     * @return meta is a wrapper for the yaml description
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * @return a DynaClass that defines data set and it is also a factory for a new DynaBean instances
     */
    public DynaClass getDataClass() {
        return dataClass;
    }

    public DataSetsManager getManager() {
        return manager;
    }

    /**
     * Returns {@link Storage}
     *
     * @return storage
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * @return application properties
     */
    public ApplicationProperties getApplicationProperties() {
        return properties;
    }

    /**
     * @param relative
     * @return a path to the home dir on the server without ending '/'
     */
    private StringBuilder getHomeDirPath(boolean relative) {
        return new StringBuilder()
                .append(relative ? contextPath : realPath)
                .append(relative ? "/" : "")
                .append("home");
    }

    /**
     * @param user
     * @return a path to user's home dir on the server without ending '/'
     */
    private StringBuilder getUserHomeDirPath(String user) {
        StringBuilder result = new StringBuilder();
        result.append(realPath).append("home/").append(user);
        return result;
    }

    /**
     * Returns a {@link StringBuilder} representation of the users upload dir.
     *
     * @param user user
     * @return a path to user's upload dir on the server without ending '/'
     */
    private StringBuilder getUserUploadDirPath(String user) {
        //TODO optimize
        return getUserHomeDirPath(user).append("/upload");
    }

    public Path getHomeDir() throws IOException {
        Path homeDir = Paths.get(getHomeDirPath(false).toString());

        if (!Files.exists(homeDir)) {
            Files.createDirectories(homeDir);
        }

        return homeDir;
    }

    /**
     * Returns a {@link java.io.File} representation of the user's home dir on the server. Creates one if no exists.
     *
     * @param user
     * @return dir
     * @throws IOException if creation attempt failed
     */
    public Path getUserHomeDir(User user) throws IOException {
        Path dir = getHomeDir().resolve(user.getName());

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        return dir;
    }

    /**
     * Returns a {@link File} representation of the users upload dir. Creates one if no exists.
     *
     * @param user
     * @return a path to user's upload dir on the server without ending '/'
     * @throws IOException if creation attempt failed
     */
    public Path getUserUploadDir(String user) throws IOException {
        Path dir = Paths.get(getUserUploadDirPath(user).toString());

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        return dir;
    }

    public Iterable<String> getUsers() throws IOException {
        return Iterables.transform(Files.newDirectoryStream(getHomeDir()), new Function<Path, String>() {
            @Override
            public String apply(@Nullable Path input) {
                return input.getFileName().toString();
            }
        });
    }
}

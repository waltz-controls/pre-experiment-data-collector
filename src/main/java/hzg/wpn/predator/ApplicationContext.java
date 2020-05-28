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

package hzg.wpn.predator;

import hzg.wpn.predator.meta.Meta;
import hzg.wpn.predator.storage.Storage;
import hzg.wpn.predator.web.ApplicationProperties;
import hzg.wpn.predator.web.data.DataSetsManager;
import org.apache.commons.beanutils.DynaClass;

import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static hzg.wpn.predator.web.ApplicationLoader.VAR_PRE_EXPERIMENT_DATA_COLLECTOR;

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
    public static final String HOME = "home";
    private final String beamtimeId;
    private final Storage storage;
    private final ApplicationProperties properties;
    private final Meta meta;
    private final DynaClass dataClass;
    private final DataSetsManager manager;
    private final Path home;

    /**
     * @param beamtimeId
     * @param storage
     * @param properties
     * @param meta
     * @param dataClass
     */
    public ApplicationContext(String beamtimeId, Storage storage, ApplicationProperties properties, Meta meta, DynaClass dataClass) {
        this.beamtimeId = beamtimeId;
        this.storage = storage;
        this.properties = properties;
        this.meta = meta;
        this.dataClass = dataClass;
        this.home = Paths.get(
                System.getProperty("XENV_ROOT", System.getProperty("user.dir"))).resolve(VAR_PRE_EXPERIMENT_DATA_COLLECTOR).resolve(HOME);
        this.manager = new DataSetsManager(beamtimeId,
                home, dataClass, storage);
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
     * @param user
     * @return a path to user's home dir on the server without ending '/'
     */
    private Path getUserHomeDirPath(String user) {
        return home.resolve(user);
    }

    /**
     * Returns a {@link StringBuilder} representation of the users upload dir.
     *
     * @param user user
     * @return a path to user's upload dir on the server without ending '/'
     */
    private Path getUserUploadDirPath(String user) {
        //TODO optimize
        return getUserHomeDirPath(user).resolve("upload");
    }

    private Path getHomeDir() throws IOException {
        if (!Files.exists(home)) {
            Files.createDirectories(home);
        }

        return home;
    }

    /**
     * Returns a {@link File} representation of the users upload dir. Creates one if no exists.
     *
     * @param user
     * @return a path to user's upload dir on the server without ending '/'
     * @throws IOException if creation attempt failed
     */
    public Path getUserUploadDir(String user) throws IOException {
        Path dir = getUserUploadDirPath(user);

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        return dir;
    }

    public Iterable<String> getUsers() throws IOException {
        return StreamSupport.stream(Files.newDirectoryStream(getHomeDir()).spliterator(), false).map(input -> input.getFileName().toString()).collect(Collectors.toList());
    }
}

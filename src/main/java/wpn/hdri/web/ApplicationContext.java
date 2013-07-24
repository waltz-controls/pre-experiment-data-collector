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

package wpn.hdri.web;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.io.FileUtils;
import wpn.hdri.web.data.BeamtimeId;
import wpn.hdri.web.data.User;
import wpn.hdri.web.meta.MetaDataHelpers;
import wpn.hdri.web.storage.Storage;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

/**
 * Provides a number of useful methods to deal with agreed file structure on the server.
 * <p/>
 * Extends {@link ServletContext}
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 13.03.12
 */
public class ApplicationContext {
    public static final ApplicationContext NULL = new ApplicationContext(null, null, null, null, null, null);

    private final String realPath;
    private final String contextPath;
    private final BeamtimeId beamtimeId;
    private final Storage<DynaBean> storage;
    private final ApplicationProperties properties;
    private final MetaDataHelpers helper;

    /**
     * @param realPath    ends with '/'
     * @param contextPath without '/' on the end.
     * @param beamtimeId
     * @param storage
     * @param properties
     * @param helper
     */
    public ApplicationContext(String realPath, String contextPath, BeamtimeId beamtimeId, Storage<DynaBean> storage, ApplicationProperties properties, MetaDataHelpers helper) {
        this.realPath = realPath;
        this.contextPath = contextPath;
        this.beamtimeId = beamtimeId;
        this.storage = storage;
        this.properties = properties;
        this.helper = helper;
    }


    /**
     * Returns current beamtimeId associated with this application. BeamtimeId is usually stored in BeamtimeId.txt resource file.
     * It is assumed beamtimeId is being set once during the experiment installation setup.
     *
     * @return beamtimeId
     */
    public BeamtimeId getBeamtimeId() {
        return beamtimeId;
    }

    /**
     * @return metaDataHelper
     */
    public MetaDataHelpers getMetaDataHelper() {
        return helper;
    }

    /**
     * Returns {@link Storage}
     *
     * @return storage
     */
    public Storage<DynaBean> getStorage() {
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
    public StringBuilder getUserHomeDirPath(User user) {
        StringBuilder result = new StringBuilder();
        result.append(realPath).append("home/").append(user.getName());
        return result;
    }

    /**
     * Returns a {@link StringBuilder} representation of the users upload dir.
     *
     * @param user user
     * @return a path to user's upload dir on the server without ending '/'
     */
    public StringBuilder getUserUploadDirPath(User user) {
        //TODO optimize
        return getUserHomeDirPath(user).append("/upload");
    }

    /**
     * Returns a {@link StringBuilder} representation of the users upload dir.
     *
     * @param user user
     * @return a path to user's upload dir on the server without ending '/'
     */
    public StringBuilder getUserUploadDirRelativePath(User user) {
        //TODO optimize
        return getHomeDirPath(true).append("/").append(user.getName()).append("/upload");
    }

    public File getHomeDir() throws IOException {
        File homeDir = new File(getHomeDirPath(false).toString());

        if (!homeDir.exists()) {
            FileUtils.forceMkdir(homeDir);
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
    public File getUserHomeDir(User user) throws IOException {
        File dir = new File(getHomeDir(), user.getName());

        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }

        return dir;
    }

    /**
     * Returns a {@link java.io.File} representation of the associated with the user beamtime dir on the server. Creates one if no exists.
     *
     * @param user
     * @return dir
     * @throws IOException if creation attempt failed
     */
    public File getUserBeamtimeDir(User user) throws IOException {
        File beamtime = new File(getUserHomeDir(user), beamtimeId.getValue());

        if (!beamtime.exists()) {
            FileUtils.forceMkdir(beamtime);
        }

        return beamtime;
    }

    /**
     * Returns a {@link File} representation of the users upload dir. Creates one if no exists.
     *
     * @param user
     * @return a path to user's upload dir on the server without ending '/'
     * @throws IOException if creation attempt failed
     */
    public File getUserUploadDir(User user) throws IOException {
        File dir = new File(getUserUploadDirPath(user).toString());

        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }

        return dir;
    }
}

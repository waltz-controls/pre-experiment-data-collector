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

import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.data.Users;

import java.io.File;
import java.io.IOException;

/**
 * Skeletal implementation for {@link wpn.hdri.web.storage.Storage}
 *
 * @param <T>
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 */
public abstract class AbsFileStorage<T> implements Storage<T> {
    public static final String BINARY_FILE_EXTENSION = "dat";
    public static final String JSON_FILE_EXTENSION = "json";

    public AbsFileStorage() {
    }

    public final void save(T data, Users.User user, String dataSetName, ApplicationContext ctx) throws StorageException {
        try {
            File homeDir = ctx.getUserBeamtimeDir(user);

            File output = new File(getPath(homeDir, dataSetName));

            saveInternal(data, output);
        } catch (IOException e) {
            throw new StorageException("Can not get user beamtime dir:[user:" + user.getName() + ";beamtimeId:" + ctx.getBeamtimeId().getValue() + "]", e);
        }
    }

    private String getPath(File homeDir, String dataSetName) {
        StringBuilder path = new StringBuilder();
        path.append(homeDir.getAbsolutePath()).append("/").append(dataSetName).append(".").append(getExtension());
        return path.toString();
    }

    protected abstract void saveInternal(T bean, File output) throws StorageException;

    public final T load(Users.User user, String dataSetName, ApplicationContext ctx) throws StorageException {
        try {
            File homeDir = ctx.getUserBeamtimeDir(user);

            File input = new File(getPath(homeDir, dataSetName));

            return loadInternal(input);
        } catch (IOException e) {
            throw new StorageException("Can not get user beamtime dir:[user:" + user.getName() + ";beamtimeId:" + ctx.getBeamtimeId().getValue() + "]", e);
        }
    }

    protected abstract String getExtension();

    protected abstract T loadInternal(File input) throws StorageException;
}

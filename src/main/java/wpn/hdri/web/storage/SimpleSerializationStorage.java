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

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Saves and loads data instance.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 23.02.12
 */
public final class SimpleSerializationStorage<T> extends AbsFileStorage<T> {
    @Override
    protected String getExtension() {
        return BINARY_FILE_EXTENSION;
    }

    protected void saveInternal(T bean, File output) throws StorageException {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(output)));

            try {
                oos.writeObject(bean);
            } finally {
                IOUtils.closeQuietly(oos);
            }
        } catch (FileNotFoundException e) {
            throw new StorageException("Unable to locate file to store data in:" + output.getAbsolutePath(), e);
        } catch (IOException e) {
            throw new StorageException("Data serialization attempt failed.", e);
        }
    }

    /**
     * Loads data set from a file or returns null if file does not exists.
     *
     * @param input file to read from
     * @return object of type T or null
     * @throws StorageException if read file attempt failed.
     */
    protected T loadInternal(File input) throws StorageException {
        if(!input.exists()){
            return null;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(input)));

            try {
                Object o = ois.readObject();

                return (T) o;
            } finally {
                IOUtils.closeQuietly(ois);
            }
        } catch (IOException e) {
            throw new StorageException("Can not read from file:" + input.getAbsolutePath(), e);
        } catch (ClassNotFoundException e) {
            throw new StorageException("Data deserialization attempt failed.", e);
        }
    }

    @Override
    public void close() {

    }
}

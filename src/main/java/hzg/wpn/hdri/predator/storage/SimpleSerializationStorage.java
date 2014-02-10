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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Saves and loads data instance.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 23.02.12
 */
public final class SimpleSerializationStorage implements Storage {
    /**
     * Saves the bean instance as binary serialized java object
     *
     * @param bean
     * @param root
     * @throws IOException
     */
    public void save(DynaBean bean, Path root) throws IOException {
        if(!Files.exists(root)){
            Files.createDirectories(root);
        }
        String name = null;
        try {
            name = BeanUtils.getProperty(bean, "name");
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IOException(e);
        }
        Path output = root.resolve(name);
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(output.toFile())))) {
            oos.writeObject(bean);
        }
    }

    /**
     * Loads data set from a file or returns null if file does not exists.
     *
     * @param dataSetName file to read from
     * @param root        path to user's dir
     * @return object of type T or null
     * @throws IOException if read file attempt failed.
     */
    public DynaBean load(String dataSetName, Path root) throws IOException {
        Path input = root.resolve(dataSetName);
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(input.toFile())))) {
            Object o = ois.readObject();

            return (DynaBean) o;
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}

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

import java.lang.reflect.InvocationTargetException;

/**
 * Creates {@link Storage} instance through reflection.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 23.02.12
 */
public class StorageFactory {
    private final String storageClassName;

    public StorageFactory(String storageClassName) {
        this.storageClassName = storageClassName;
    }

    @SuppressWarnings("unchecked")
    public <T> Storage<T> createInstance() throws Exception {
        try {
            return Storage.class.cast(Class.forName(storageClassName).getConstructor().newInstance());
        } catch (InvocationTargetException e) {
            throw new Exception(e);
        } catch (NoSuchMethodException e) {
            throw new Exception(e);
        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        } catch (InstantiationException e) {
            throw new Exception(e);
        } catch (IllegalAccessException e) {
            throw new Exception(e);
        } catch (ClassCastException e) {
            throw new Exception(e);
        }
    }
}

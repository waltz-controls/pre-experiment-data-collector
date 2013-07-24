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

import org.apache.commons.beanutils.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.meta.MetaData;
import wpn.hdri.web.meta.MetaDataHelpers;
import wpn.hdri.web.meta.MetaField;
import wpn.hdri.web.storage.AbsFileStorage;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 26.03.12
 */
public class DataSets {
    // utility class
    private DataSets() {
    }

    /**
     * Creates new {@link DataSet} instance with empty values.
     *
     * @param user       a user to associated new DataSet with
     * @param meta
     * @param beamtimeId
     * @param id         new DataSet's id. Should be valid fileName  @return new DataSet
     * @throws Exception
     */
    public static DataSet createDataSet(User user, MetaData meta, BeamtimeId beamtimeId, String id) throws Exception {
        List<DynaProperty> properties = new ArrayList<DynaProperty>();

        for (MetaField fld : meta.getAllFields()) {
            properties.add(new DynaProperty(fld.getId(), MetaDataHelpers.getFieldTypeAdaptor(fld).getTargetClass()));
        }

        properties.add(new DynaProperty(DataSet.READONLY, boolean.class));

        BasicDynaBean values = new BasicDynaBean(
                new BasicDynaClass("Wrapped", BasicDynaBean.class, properties.toArray(new DynaProperty[properties.size()])));

        return createDataSet(user, meta, beamtimeId, id, values);
    }

    /**
     * Creates new {@link DataSet} instance.
     *
     * @param user       a user to associated new DataSet with
     * @param meta
     * @param beamtimeId
     * @param id         new DataSet's id. Should be valid fileName  @return new DataSet
     * @throws Exception
     */
    public static DataSet createDataSet(User user, MetaData meta, BeamtimeId beamtimeId, String id, DynaBean values) throws Exception {
        //TODO validate arguments
        return new DataSet(
                user, beamtimeId, id, meta, values, newTimestamp());
    }

    /**
     * Returns a collection of the {@link DataSet} names associated with the user
     *
     * @param user
     * @param ctx
     * @return a collection
     * @throws IOException
     */
    public static Collection<String> getUserDataSetNames(User user, ApplicationContext ctx) throws IOException {
        File userBeamtimeDir = ctx.getUserBeamtimeDir(user);

        if (!userBeamtimeDir.exists()) {
            return Collections.emptySet();
        }

        Collection<String> result = new LinkedHashSet<String>();
        for (File file : userBeamtimeDir.listFiles((FileFilter) new AndFileFilter(
                FileFileFilter.FILE,
                new SuffixFileFilter(AbsFileStorage.BINARY_FILE_EXTENSION, IOCase.INSENSITIVE)))) {
            result.add(FilenameUtils.removeExtension(file.getName()));
        }

        return result;
    }

    /**
     * Creates new instance of {@link DataSet} with readonly set to true.
     *
     * @param dataSet
     * @return
     */
    public static void setReadonly(DataSet dataSet) {
        dataSet.set(DataSet.READONLY, true);
    }

    /**
     * Creates new instance of {@link DataSet} with data replaced by newData.
     *
     * @param newData
     * @param dataSet
     * @return
     */
    public static void update(DynaBean newData, DataSet dataSet) {
        try {
            for (Map.Entry entry : (Set<Map.Entry>) PropertyUtils.describe(newData).entrySet()) {
                dataSet.set(String.valueOf(entry.getKey()), entry.getValue());
            }
        } catch (IllegalAccessException e) {
            //TODO
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            //TODO
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    private static long newTimestamp() {
        return System.nanoTime();
    }

    public static <T> void update(String fldId, T value, DataSet dataSet) {
        dataSet.set(fldId, value);
    }
}

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

package wpn.hdri.util.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 28.02.12
 */
public class ReflectionUtils {
    private ReflectionUtils() {
    }

    /**
     * Acts as {@link Class#getDeclaredFields()} but also includes inherited fields.
     *
     * @param clazz
     * @return
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();

        do {
            Field[] dclFields = clazz.getDeclaredFields();
            fields.addAll(Arrays.asList(dclFields));
        } while ((clazz = clazz.getSuperclass()) != null);

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Acts as {@link Class#getDeclaredField(String)} but also looks in super classes for the field.
     *
     * @param name  a name of the field to look up
     * @param clazz a class
     * @return Field or throws exception
     * @throws Exception if no field with the given name was found in the class hierarchy
     */
    public static Field getDeclaredField(String name, Class<?> clazz) throws Exception {
        do {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            } catch (SecurityException e) {
                throw new Exception(e);
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new NoSuchFieldException(name);
    }
}

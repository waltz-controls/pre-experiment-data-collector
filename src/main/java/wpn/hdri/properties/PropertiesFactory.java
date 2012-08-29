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

package wpn.hdri.properties;

import wpn.hdri.util.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 29.03.12
 */
public class PropertiesFactory<T> {
    private final Properties properties;
    private final Class<T> type;

    public PropertiesFactory(Properties properties, Class<T> type) {
        this.properties = properties;
        this.type = type;
    }

    public T createType() throws Exception {
        T instance = type.newInstance();
        Field[] fields = ReflectionUtils.getDeclaredFields(type);
        if (fields.length == 0) {
            throw new IllegalStateException("No @Property defined in the class " + type.getName());
        }
        for (Field field : fields) {
            Property property = field.getAnnotation(Property.class);
            if (property != null) {
                Object value = properties.get(property.name());
                if (value == null) {
                    if (!property.defaultValue().isEmpty()) {
                        //TODO different types
                        setField(instance, field, property);
                    } else {
                        throw new IllegalArgumentException("Property " + property.name() + " is not defined.");
                    }
                } else {
                    field.set(instance, value);
                }
            }
        }

        return instance;
    }

    private void setField(T instance, Field field, Property property) throws IllegalAccessException {
        try {
            field.setAccessible(true);
            field.set(instance, property.defaultValue());
        } finally {
            field.setAccessible(false);
        }
    }
}

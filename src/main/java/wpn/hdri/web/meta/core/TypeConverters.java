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

package wpn.hdri.web.meta.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 15.03.12
 */
public class TypeConverters {
    public static final TypeConverter<String> TO_STRING = new TypeConverter<String>() {
        public String convert(String data) throws Exception {
            return data;
        }
    };
    public static final TypeConverter<String[]> TO_STRING_ARR = new TypeConverter<String[]>() {
        public String[] convert(String data) throws Exception {
            return data.split(";");
        }
    };
    public static final TypeConverter<Integer> TO_INTEGER = new TypeConverter<Integer>() {
        public Integer convert(String data) throws Exception {
            if (data.isEmpty()) {
                return 0;
            }
            return Integer.valueOf(data);
        }
    };
    public static final TypeConverter<Double> TO_DOUBLE = new TypeConverter<Double>() {
        public Double convert(String data) throws Exception {
            if (data.isEmpty()) {
                return 0.;
            }
            return Double.valueOf(data);
        }
    };
    public static final TypeConverter<Boolean> TO_BOOLEAN = new TypeConverter<Boolean>() {
        public Boolean convert(String data) throws Exception {
            return Boolean.valueOf(data);
        }
    };

    private final static Map<Class<?>, TypeConverter<?>> converters = new ConcurrentHashMap<Class<?>, TypeConverter<?>>();

    static {
        registerConverter(String.class, TO_STRING);
        registerConverter(Integer.class, TO_INTEGER);
        registerConverter(Double.class, TO_DOUBLE);
        registerConverter(int.class, TO_INTEGER);
        registerConverter(Boolean.class, TO_BOOLEAN);
        registerConverter(boolean.class, TO_BOOLEAN);
        registerConverter(String[].class, TO_STRING_ARR);
    }

    public static <T, V extends TypeConverter<T>> void registerConverter(Class<T> key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Neither key nor value can not be null.");
        }
        converters.put(key, value);
    }

    public static <T, V extends TypeConverter<T>> V forType(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type can not be null");
        }
        if (!converters.containsKey(type)) {
            throw new IllegalArgumentException("no type converter is found for type:" + type.getName());
        }
        return (V) converters.get(type);
    }

    private TypeConverters() {
    }
}

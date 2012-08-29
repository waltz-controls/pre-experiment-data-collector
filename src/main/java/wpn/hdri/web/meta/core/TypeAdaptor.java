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
 * Encapsulates {@link TypeConverter}.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 02.03.12
 */
public final class TypeAdaptor<T, V> {
    private final String name;
    private final Class<T> metaStructureClass;
    private final Class<V> targetClass;
    private final TypeConverter<V> converter;

    private TypeAdaptor(String name, Class<T> metaStructureClass, Class<V> targetClass, TypeConverter<V> converter) {
        this.name = name;
        this.metaStructureClass = metaStructureClass;
        this.targetClass = targetClass;
        this.converter = converter;
    }

    private static final Map<String, TypeAdaptor<?, ?>> registry = new ConcurrentHashMap<String, TypeAdaptor<?, ?>>();

    /**
     * @param typeName
     * @param <T>
     * @param <V>
     * @return
     * @throws IllegalArgumentException if no adaptor was found for typeName
     */
    public static <T, V> TypeAdaptor<T, V> forName(String typeName) {
        TypeAdaptor<T, V> adaptor = (TypeAdaptor<T, V>) registry.get(typeName);
        if (adaptor == null) {
            throw new IllegalArgumentException("No type adaptor was found for type:" + typeName);
        }
        return adaptor;
    }


    public String getName() {
        return name;
    }

    public Class<T> getMetaStructureClass() {
        return metaStructureClass;
    }

    public Class<V> getTargetClass() {
        return targetClass;
    }

    public V convert(String value) throws Exception {
        return converter.convert(value);
    }

    /**
     * Creates new instance of {@link TypeAdaptor} that converts whatever to {@link String}
     *
     * @param name  name of new instance
     * @param clazz whatever class
     * @param <T>   whatever
     * @return new instance of TypeAdaptor&lt;T,String&gt;
     */
    public static <T> TypeAdaptor<T, String> createStringAdaptor(String name, Class<T> clazz) {
        return new TypeAdaptor<T, String>(name, clazz, String.class, TypeConverters.TO_STRING);
    }

    /**
     * Creates new instance of {@link TypeAdaptor} that converts whatever to whatever using specified {@link TypeConverter}
     *
     * @param name
     * @param sourceClass
     * @param targetClass
     * @param converter
     * @param <T>
     * @param <V>
     * @return
     */
    public static <T, V> TypeAdaptor<T, V> create(String name, Class<T> sourceClass, Class<V> targetClass, TypeConverter<V> converter) {
        return new TypeAdaptor<T, V>(name, sourceClass, targetClass, converter);
    }

    /**
     * Adds adaptor instance to the registry. Adaptor instance then could be retrieved using its name and {@link this#forName(String)} method.
     *
     * @param adaptor an adaptor instance
     * @param <T>     source type
     * @param <V>     target type
     */
    public static <T, V> void registerAdapter(TypeAdaptor<T, V> adaptor) {
        registry.put(adaptor.name, adaptor);
    }
}

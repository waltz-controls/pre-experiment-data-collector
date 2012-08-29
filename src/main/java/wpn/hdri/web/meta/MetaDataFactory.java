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

package wpn.hdri.web.meta;

import wpn.hdri.util.reflection.ReflectionUtils;
import wpn.hdri.web.meta.core.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates {@link MetaData} from {@link MetaStructure}.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 06.03.12
 */
public class MetaDataFactory<T extends MetaStructure<T>> {
    private final MetaFactory<?, T> srcFactory;

    public MetaDataFactory(MetaFactory<?, T> srcFactory) {
        this.srcFactory = srcFactory;
    }

    public MetaData createMetaData() throws Exception {
        T src = srcFactory.createInstance();
        return createMetaStructure(src, MetaData.class);
    }

    private <V> V createMetaStructure(T src, Class<V> clazz) throws Exception {
        V instance = clazz.newInstance();

        for (T child : src.getChildren()) {
            Field declaredField = ReflectionUtils.getDeclaredField(child.getId(), clazz);
            switch (MetaSourceType.valueOf(child.getType())) {
                case PRIMITIVE:
                    setFieldValue(declaredField, instance, child.getValue());
                    break;
                case COMPLEX:
                    setFieldValue(declaredField, instance, convertToMeta(child));
                    break;
                case ARRAY:
                    setFieldValue(declaredField, instance, convertToMetaArray(child));
                    break;
            }
        }

        return instance;
    }

    /**
     * Creates a raw list of MetaXXXs
     *
     * @param source
     * @return
     * @throws Exception
     */
    private List convertToMetaArray(T source) throws Exception {
        List result = new ArrayList();
        for (int i = 0; source.get("value", i) != null; i++) {
            T element = source.get("value", i);
            result.add(convertToMeta(element));
        }
        return result;
    }

    /**
     * Create MetaXXX from {@link MetaStructure}
     *
     * @param source
     * @return
     * @throws Exception
     */
    private Object convertToMeta(T source) throws Exception {
        String type = source.get("type").getValue();//string,number,fieldset...

        TypeAdaptor<?, ?> adaptor = TypeAdaptor.forName(type);

        return createMetaStructure(source, adaptor.getMetaStructureClass());

    }

    private static <T> void setFieldValue(Field field, T instance, Object value) throws NoSuchFieldException, IllegalAccessException {
        try {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            Object adjusted = adjustValue(value, fieldType);
            field.set(instance, adjusted);
        } finally {
            field.setAccessible(false);
        }
    }

    private static <T> Object adjustValue(Object value, Class<T> type) {
        if (type.equals(value.getClass())) {
            return value;
        } else {
            try {
                TypeConverter<T> converter = TypeConverters.<T, TypeConverter<T>>forType(type);
                return converter.convert(String.valueOf(value));
            } catch (Exception e) {
                //TODO maybe it is better to throw exception here
                return value;
            }
        }
    }

    /**
     * Validates {@link MetaStructure} instance against {@link Class}<? extends MetaStructure> scheme.
     *
     * @param src
     * @param scheme
     * @throws IllegalArgumentException if src does not match scheme
     */
    public static void validate(MetaStructure src, Class<? extends MetaStructure> scheme) {
        if (src.get("type") == null) {
            throw new IllegalArgumentException("type is not defined");
        }

        String type = String.valueOf(src.get("type").getValue());
        TypeAdaptor.forName(type);

        if (src.get("id") == null) {
            throw new IllegalArgumentException("id is not defined");
        }
    }
}

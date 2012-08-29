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

import wpn.hdri.web.meta.core.TypeAdaptor;
import wpn.hdri.web.meta.core.TypeConverters;

/**
 * Contains a set of useful strategies.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 08.03.12
 */
//TODO simplify
public enum MetaConversionStrategy {
    //MetaField adaptors
    STRING(TypeAdaptor.createStringAdaptor("string", MetaField.class)),
    TEXT(TypeAdaptor.createStringAdaptor("text", MetaField.class)),
    FILE(TypeAdaptor.createStringAdaptor("file", MetaField.class)),
    FILE_MULTIPLY(TypeAdaptor.createStringAdaptor("file_multiply", MetaField.class)),
    NUMBER(TypeAdaptor.create("number", MetaField.class, Integer.class, TypeConverters.TO_INTEGER)),
    DOUBLE(TypeAdaptor.create("double", MetaField.class, Double.class, TypeConverters.TO_DOUBLE)),
    BOOLEAN(TypeAdaptor.create("boolean", MetaField.class, Boolean.class, TypeConverters.TO_BOOLEAN)),
    //MetaChoice adaptors
    CHOICE(TypeAdaptor.create("choice", MetaChoice.class, Boolean.class, TypeConverters.TO_BOOLEAN)),
    //MetaForm adaptors
    FIELD_SET(TypeAdaptor.create("fieldset", MetaForm.class, MetaField[].class, null)),
    MULTI_CHOICE(TypeAdaptor.create("multichoice", MetaForm.class, MetaField[].class, null)),
    UPLOAD(TypeAdaptor.create("upload", MetaForm.class, MetaField[].class, null));

    private final TypeAdaptor<?, ?> adaptor;

    private MetaConversionStrategy(TypeAdaptor<?, ?> adaptor) {
        this.adaptor = adaptor;
        TypeAdaptor.registerAdapter(adaptor);
    }

    public TypeAdaptor<?, ?> getAsTypeAdaptor() {
        return adaptor;
    }

    /**
     * Dummy method. Call it to load {@link this} into ClassLoader
     */
    public static void initialize() {
        try {
            MetaConversionStrategy.class.getClassLoader().loadClass(MetaConversionStrategy.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

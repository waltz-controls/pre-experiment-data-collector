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

import java.io.Serializable;
import java.util.Iterator;

/**
 * Fields are set through reflection in {@link MetaDataFactory}
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.01.12
 */
public /*final*/ class MetaField {
    /**
     * the following fields are set through reflection in {@link wpn.hdri.web.meta.MetaDataFactory#createMetaData()}
     */
    private String id;
    private String name;
    private String type;
    private String description;
    private String validation;
    private Visibility visibility;
    private boolean readonly;

    public Iterable<MetaField> getAllFields() {
        return new Iterable<MetaField>() {
            public Iterator<MetaField> iterator() {
                return new Iterator<MetaField>() {
                    private volatile boolean hasBeenAccessed = false;

                    public boolean hasNext() {
                        return !hasBeenAccessed;
                    }

                    public MetaField next() {
                        hasBeenAccessed = true;
                        return MetaField.this;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException("This Iterator impl does not support remove method.");
                    }
                };
            }
        };
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getValidation() {
        return validation;
    }


    public Visibility getVisibility() {
        return visibility;
    }

    public boolean isVisibleInWeb() {
        return this.visibility == Visibility.WEB;
    }

    public boolean isReadonly() {
        return readonly;
    }
}

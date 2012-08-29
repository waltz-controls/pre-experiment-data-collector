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

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import wpn.hdri.web.meta.core.MetaStructure;
import wpn.hdri.web.meta.core.TypeAdaptor;
import wpn.hdri.web.meta.core.TypeConverter;
import wpn.hdri.web.meta.core.TypeConverters;
import wpn.hdri.web.meta.json.JsonMetaSource;
import wpn.hdri.web.meta.json.JsonStreamFactory;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Contains helper functions and factory methods for {@link MetaData}.
 * Also acts as a MetaData factory.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.01.12
 */
public class MetaDataHelpers {
    public static final MetaDataHelpers JSON_DEFAULT = new MetaDataHelpers(new MetaDataFactory<JsonMetaSource>(new JsonStreamFactory()));

    static {
        TypeConverters.registerConverter(Visibility.class, new TypeConverter<Visibility>() {
            public Visibility convert(String data) throws Exception {
                return Visibility.forAlias(data);
            }
        });
        MetaConversionStrategy.initialize();
    }

    private final MetaDataFactory<? extends MetaStructure<?>> metaFactory;
    /**
     * This comparator checks that at least all forms from left operand are contained in right operand.
     * MetaForms are compared according their ids.
     */
    public final static Comparator<MetaData> DEFAULT_META_COMPARATOR = new Comparator<MetaData>() {
        @Override
        public int compare(MetaData o1, MetaData o2) {
            Comparator<MetaForm> formComparator = new Comparator<MetaForm>() {
                @Override
                public int compare(MetaForm o1, MetaForm o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            };

            NavigableSet<MetaForm> forms2 = new TreeSet<MetaForm>(formComparator);

            forms2.addAll(o2.getForms());

            return forms2.containsAll(o1.getForms()) ? 0 : -1;
        }
    };


    public MetaDataHelpers(MetaDataFactory<? extends MetaStructure<?>> metaFactory) {
        this.metaFactory = metaFactory;
    }

    public MetaData createMetaData() throws Exception {
        MetaData metaData = metaFactory.createMetaData();
        return metaData;
    }

    /**
     * Returns cached instance of the {@link MetaData} or null.
     *
     * @return
     * @throws Exception
     */
    public MetaData getMetaData() throws Exception {
        //TODO return cached instance
        return createMetaData();
    }

    /**
     * Returns {@link TypeAdaptor} associated with the {@link MetaField}
     *
     * @param fld
     * @return
     */
    public static <T> TypeAdaptor<MetaField, T> getFieldTypeAdaptor(MetaField fld) {
        return TypeAdaptor.forName(fld.getType());
    }

    public static boolean isUpload(String metaStructureId, final MetaData meta) {
        for (MetaForm frm : getAllUploadForms(meta)) {
            for (MetaField fld : frm.getFields()) {
                if (fld.getId().equalsIgnoreCase(metaStructureId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Iterable<MetaForm> getAllUploadForms(final MetaData meta) {
        return new Iterable<MetaForm>() {
            public Iterator<MetaForm> iterator() {
                return new FilterIterator(meta.getForms().iterator(), new Predicate() {
                    public boolean evaluate(Object o) {
                        return ((MetaForm) o).getType().equals("upload");//TODO type enum?
                    }
                });
            }
        };
    }

    public static int compare(MetaData fromRequest, MetaData onServer) {
        return compare(fromRequest, onServer, DEFAULT_META_COMPARATOR);
    }

    public static int compare(MetaData fromRequest, MetaData onServer, Comparator<MetaData> comparator) {
        return DEFAULT_META_COMPARATOR.compare(fromRequest, onServer);
    }
}

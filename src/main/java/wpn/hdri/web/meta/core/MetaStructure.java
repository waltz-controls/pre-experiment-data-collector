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

import net.jcip.annotations.Immutable;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Base class for any meta structure (Data, Form, Field etc).
 * To introduce new structure user should provide an appropriate implementation of this class.
 * Implements {@link DynaBean} and could be used by commons-beanutils. This is needed because we
 * want to generate instances of this class "on fly" from the data source (json, xml, etc).
 * Supports only .get() methods to satisfy immutability.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.02.12
 */
@Immutable
public abstract class MetaStructure<T extends MetaStructure<T>> implements DynaBean, Wrappable {
    private final String id;
    private final String type;
    private final List<T> children;
    private final Map<String, T> attributes;
    private final DynaClass clazz;
    private final String value;

    protected MetaStructure(String id, String type, List<T> children, Map<String, T> attributes, String value) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.children = Collections.unmodifiableList(children);
        this.attributes = Collections.unmodifiableMap(attributes);

        this.clazz = generateDynaClass(id, type, attributes);
    }

    private static <T extends MetaStructure<T>> DynaClass generateDynaClass(String id, String type, Map<String, T> attributes) {
        DynaProperty[] properties = new DynaProperty[attributes.size()];

        int i = 0;
        for (Map.Entry<String, T> entry : attributes.entrySet()) {
            properties[i++] = generateProperty(entry.getKey(), entry.getValue());
        }

        DynaClass clazz = new BasicDynaClass(id + ":" + type, MetaStructure.class, properties);
        return clazz;
    }

    private static <T extends MetaStructure<T>> DynaProperty generateProperty(String name, T entry) {
        return new DynaProperty(name, entry.getClass());
    }

    /**
     * This constructor is to satisfy {@link DynaBean} agreement.
     *
     * @param clazz
     */
    public MetaStructure(DynaClass clazz) {
        this.id = clazz.getName().split(":")[0];
        this.type = clazz.getName().split(":")[1];
        this.children = Collections.emptyList();
        this.attributes = Collections.emptyMap();
        this.clazz = clazz;
        this.value = null;
    }

    //instance creation through reflection workaround
    //TODO
    public MetaStructure() {
        this(null, null, Collections.<T>emptyList(), Collections.<String, T>emptyMap(), null);
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public T get(String name) {
        return attributes.get(name);
    }

    public T get(String name, int index) {
        return children.get(index).get(name);
    }

    public T get(String name, String key) {
        return attributes.get(key).get(name);
    }

    public boolean contains(String name, String key) {
        return attributes.containsKey(name) || (attributes.containsKey(key) && attributes.get(key).contains(name, null));
    }

    public DynaClass getDynaClass() {
        return clazz;
    }

    public final void remove(String name, String key) {
        throw new UnsupportedOperationException();
    }

    public final void set(String name, Object value) {
        throw new UnsupportedOperationException();
    }

    public final void set(String name, int index, Object value) {
        throw new UnsupportedOperationException();
    }

    public final void set(String name, String key, Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Extracts underlying value
     *
     * @return underlying value or throws {@link UnsupportedOperationException}
     * @throws UnsupportedOperationException if structure does not have any associated value
     */
    public String getValue() {
        if (value != null)
            return value;
        else
            throw new UnsupportedOperationException();
    }

    public <V> Wrapper<T, V> createWrapper(String value, TypeAdaptor<?, V> adaptor) {
        return new Wrapper<T, V>(value, adaptor);
    }

    public Iterable<T> getChildren() {
        return children;
    }

    /**
     * This instance is equal to that if and only if their ids are equal.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetaStructure meta = (MetaStructure) o;

        if (id != null ? !id.equals(meta.id) : meta.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


    public class Wrapper<T extends MetaStructure<T>, V> implements Wrappable.Wrapper<V> {
        private final String value;
        private final TypeAdaptor<?, V> typeAdaptor;

        public Wrapper(String value, TypeAdaptor<?, V> typeAdaptor) {
            this.value = value;
            this.typeAdaptor = typeAdaptor;
        }

        public V getValue() {
            try {
                return typeAdaptor.convert(value);
            } catch (Exception e) {
                return null;
            }
        }

        public String getId() {
            return MetaStructure.this.getId();
        }

        public String getType() {
            return MetaStructure.this.getType();
        }

        public T getAsMetaStructure() {
            return (T) MetaStructure.this;
        }

        public Iterable<? extends MetaStructure> getChildren() {
            return MetaStructure.this.getChildren();
        }
    }

    /**
     * Defines important properties for the MetaStructure
     */
    public static enum ImportantProperty {
        ID,
        TYPE;

        public String getName() {
            return name().toLowerCase();
        }
    }
}

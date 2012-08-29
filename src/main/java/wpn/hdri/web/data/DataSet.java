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

import com.google.gson.*;
import net.jcip.annotations.NotThreadSafe;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import wpn.hdri.web.meta.MetaData;
import wpn.hdri.web.meta.MetaField;
import wpn.hdri.web.meta.MetaForm;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Represents collected data associated with user.
 * Instances of this class are stored in permanent storage defined by {@link wpn.hdri.web.storage.Storage} class
 * specified in application.properties.
 * Currently instances are stored as serialized Java objects in files named by {@link this#id}, e.g.
 * <p/>
 * <code>
 * DataSet dataSet = ...;
 * storage.save(dataSet,user,dataSet.getId(),ctx);
 * </code>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @see wpn.hdri.web.storage.SimpleSerializationStorage implementation.
 * @since 26.03.12
 */
@NotThreadSafe
public class DataSet implements JsonSerializable, Comparable<DataSet> {
    /*private*/ static final String READONLY = "readonly";

    private final Users.User user;
    private final BeamtimeId beamtimeId;
    private final String id;
    private final long timestamp;
    private final MetaData meta;
    private final DynaBean data;
    //we need this field in order to pass it to the client
    private final boolean readonly;

    DataSet(Users.User user, BeamtimeId beamtimeId, String id, MetaData meta, DynaBean data, long timestamp) {
        this.user = user;
        this.beamtimeId = beamtimeId;
        this.id = id;
        this.meta = meta;
        this.data = data;
        this.timestamp = timestamp;

        this.readonly = data.get(READONLY) != null ? get(READONLY,Boolean.class).booleanValue() : false;
    }

    public <T> void set(String name, T value){
        data.set(name,value);
    }

    public <T> T get(String fldId) {
        Class<?> clazz = data.getDynaClass().getDynaProperty(fldId).getType();
        return (T) get(fldId, clazz);
    }

    public <T> T get(String fldId, Class<T> clazz) {
        return clazz.cast(data.get(fldId));
    }

    public String getUserName() {
        return user.getName();
    }

    public String getId() {
        return id;
    }

    public MetaData getMeta() {
        return meta;
    }

    public Boolean isReadonly() {
        return readonly;
    }

    public DynaBean getData() {
        return data;
    }

    public BeamtimeId getBeamtimeId() {
        return beamtimeId;
    }

    public Users.User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSet dataSet = (DataSet) o;

        if (timestamp != dataSet.timestamp) return false;
        if (id != null ? !id.equals(dataSet.id) : dataSet.id != null) return false;
        if (user != null ? !user.equals(dataSet.user) : dataSet.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    public int compareTo(DataSet o) {
        return Long.valueOf(timestamp).compareTo(o.timestamp);
    }

    private transient final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeHierarchyAdapter(DynaBean.class, new DynaBeanJsonDeSerializer())
            .create();

    @Override
    public JsonElement toJson() {
        return gson.toJsonTree(this);
    }

    private class DynaBeanJsonDeSerializer implements JsonSerializer<DynaBean>,JsonDeserializer<DynaBean> {

        @Override
        public DynaBean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            throw new JsonParseException(new UnsupportedOperationException());
        }

        @Override
        public JsonElement serialize(DynaBean src, Type typeOfSrc, JsonSerializationContext context) {
            try {
                JsonObject result = new JsonObject();

                for(MetaForm frm : DataSet.this.meta.getForms()){
                    result.add(frm.getId(),toJsonElement(frm,src));
                }

                return result;
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }

        private JsonElement toJsonElement(final MetaForm frm, final DynaBean src) {
            JsonObject result = new JsonObject();

            final Collection<String> frmFieldIds = new HashSet<String>();
            for(MetaField fld : frm.getAllFields()){
                frmFieldIds.add(fld.getId());
            }

            for(Map.Entry entry : new Iterable<Map.Entry>() {
                @Override
                public Iterator<Map.Entry> iterator() {
                    try {
                        return new FilterIterator(getPropertiesMap(src).entrySet().iterator(),new Predicate() {
                            @Override
                            public boolean evaluate(Object o) {
                                return frmFieldIds.contains(((Map.Entry) o).getKey());
                            }
                    });
                    } catch (Exception e) {
                        throw new JsonParseException(e);
                    }
                }}){
                addProperty(result,String.valueOf(entry.getKey()),entry.getValue());
            }

            return result;
        }

        private void addProperty(JsonObject result, String name, Object value) {
            if (value == null) {
                result.add(name, JsonNull.INSTANCE);
            } else {
                result.addProperty(name, String.valueOf(value));
            }
        }

        private Map getPropertiesMap(DynaBean src) throws Exception {
            try {
                return PropertyUtils.describe(src);
            } catch (NoSuchMethodException e) {
                //workaround for empty beans
                return Collections.emptyMap();
            }
        }
    }
}

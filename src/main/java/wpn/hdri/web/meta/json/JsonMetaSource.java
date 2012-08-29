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

package wpn.hdri.web.meta.json;

import com.google.gson.*;
import net.jcip.annotations.Immutable;
import org.apache.commons.beanutils.*;
import wpn.hdri.web.meta.core.MetaSourceType;
import wpn.hdri.web.meta.core.MetaStructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Wraps {@link JsonElement} instance with {@link DynaBean} interface.
 * <p/>
 * The following convention is applied:
 * <pre>1) Simple Object
 * <p/>
 *     {
 *         some:test
 *     }
 *     ===>
 *     DynaBean {
 *         type=COMPLEX
 *         id="root"
 *         some=DynaBean{
 *             type=PRIMITIVE
 *             id="some"
 *             value=test
 *         }
 *     }
 * </pre>
 * <pre>2) Object that contains an array
 * <p/>
 *     {
 *         some:[
 *          value1,
 *          value2
 *         ]
 *     }
 *     ===>
 *     DynaBean {
 *         type=COMPLEX
 *         id="root"
 *         some=DynaBean {
 *             type:ARRAY
 *             id="some"
 *             value=[DynaBean{...},...]
 *         }
 *     }
 * </pre>
 * <pre>Other recursively constructed according to this rules</pre>
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 05.03.12
 */
@Immutable
public class JsonMetaSource extends MetaStructure<JsonMetaSource> {
    private final JsonElement element;
    private final DynaClass clazz;

    private JsonMetaSource(String id, MetaSourceType type, JsonElement element, String value) {
        super(id, type.name(), Collections.<JsonMetaSource>emptyList(), Collections.<String, JsonMetaSource>emptyMap(), value);
        this.element = element;

        this.clazz = generateDynaClass(element);
    }

    public JsonMetaSource(String id, JsonObject object) {
        this(id, MetaSourceType.COMPLEX, object, null);
    }

    public JsonMetaSource(String id, JsonPrimitive primitive) {
        this(id, MetaSourceType.PRIMITIVE, primitive, primitive.getAsString());
    }

    public JsonMetaSource(String id, JsonArray array) {
        this(id, MetaSourceType.ARRAY, array, null);
    }

    public JsonMetaSource(String id, JsonNull jsonNull) {
        this(id, MetaSourceType.PRIMITIVE, jsonNull, null);
    }

    private static DynaProperty generateProperty(String name, JsonElement element) {
        if (element.isJsonPrimitive() || element.isJsonNull()) {
            //all json properties are treated as Strings
            return new DynaProperty(name, String.class);
        } else if (element.isJsonArray()) {
            return new DynaProperty(name, String[].class);
        } else if (element.isJsonObject()) {
            return new DynaProperty(name, generateDynaClass(element).getClass());
        } else {
            throw new IllegalStateException("either primitive, array or object is expected here.");
        }
    }

    private static DynaClass generateDynaClass(JsonElement element) {
        if (element.isJsonPrimitive() || element.isJsonArray() || element.isJsonNull()) {
            return new BasicDynaClass(
                    "GeneratedPrimitive",
                    BasicDynaBean.class,
                    //Any value in data source is treated as String
                    new DynaProperty[]{generateProperty("value", element)});
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            List<DynaProperty> properties = new ArrayList<DynaProperty>();

            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                String name = entry.getKey();
                JsonElement el = entry.getValue();

                properties.add(generateProperty(name, el));
            }

            return new BasicDynaClass("GeneratedObject", BasicDynaBean.class, properties.toArray(new DynaProperty[properties.size()]));
        } else {
            throw new NullPointerException("json element is null");
        }
    }

    public boolean contains(String name, String key) {
        return element.getAsJsonObject().has(name) ||
                (element.getAsJsonObject().has(key) &&
                        element.getAsJsonObject().get(key).isJsonObject() &&
                        element.getAsJsonObject().get(key).getAsJsonObject().has(name));
    }

    /**
     * Generates new {@link JsonMetaSource} instance for the property specified by name if underlying {@link JsonElement}
     * contains one.
     *
     * @param name
     * @return
     */
    public JsonMetaSource get(String name) {
        if (element.isJsonObject() && contains(name, null)) {
            return get(name, element.getAsJsonObject().get(name));
        } else if (element.isJsonPrimitive()) {
            return get(name, element);
        } else
            return null;
    }

    private JsonMetaSource get(String name, JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            //TODO cache instance
            return new JsonMetaSource(name, object);
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            //TODO cache instance
            return new JsonMetaSource(name, primitive);
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            //TODO cache instance
            return new JsonMetaSource(name, array);
        } else if (element.isJsonNull()) {
            JsonNull nil = element.getAsJsonNull();
            //TODO cache instance
            return new JsonMetaSource(name, nil);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Takes child by name and returns an indexed child in taken child assuming that taken child is an array.
     *
     * @param name
     * @param index
     * @return {@link JsonMetaSource} referenced by index or null
     * @throws IllegalStateException if underlying {@link JsonElement} is not an array
     */
    public JsonMetaSource get(String name, int index) {
        return get(name, element, index);
    }

    /**
     * Returns indexed element in the underlying array. The array is referenced by the field 'value'.
     * Shorter version of {@link JsonMetaSource#get(String, int)} assuming name='value'.
     *
     * @param index
     * @return
     */
    public JsonMetaSource get(int index) {
        return get("value", element, index);
    }

    private JsonMetaSource get(String name, JsonElement element, int index) {
        try {
            if (element.isJsonArray()) {
                return get(name, element.getAsJsonArray().get(index));
            } else if (element.getAsJsonObject().get(name).isJsonArray()) {
                return get(name, element.getAsJsonObject().get(name).getAsJsonArray().get(index));
            } else {
                throw new IllegalStateException();
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public JsonMetaSource get(String name, String key) {
        if (element.isJsonObject() && contains(name, key)) {
            return get(name, element.getAsJsonObject().get(key).getAsJsonObject().get(name));
        } else {
            return null;
        }
    }

    public DynaClass getDynaClass() {
        return clazz;
    }

    /**
     * Could be used as shorter version to {@link JsonMetaSource#get(String)#getValue()} if this instance wraps {@link JsonObject}
     * with property 'value'.
     *
     * @return underlying value
     * @throws UnsupportedOperationException if this instance does not wrap {@link JsonPrimitive} element or an {@link JsonObject}
     *                                       with property 'value'
     */
    @Override
    public String getValue() {
        if (element.isJsonPrimitive()) {
            return element.getAsString();
        } else if (element.isJsonNull()) {
            return null;
        } else if (element.isJsonObject() && element.getAsJsonObject().has("value") && element.getAsJsonObject().get("value").isJsonPrimitive()) {
            return element.getAsJsonObject().get("value").getAsString();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterable<JsonMetaSource> getChildren() {
        List<JsonMetaSource> result = new ArrayList<JsonMetaSource>();
        for (Map.Entry<String, JsonElement> entry : this.element.getAsJsonObject().entrySet()) {
            String name = entry.getKey();
            JsonElement element = entry.getValue();
            result.add(get(name, element));
        }
        return result;
    }
}

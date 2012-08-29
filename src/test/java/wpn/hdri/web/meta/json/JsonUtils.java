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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Map;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 05.03.12
 */
public class JsonUtils {
    private JsonUtils() {
    }

    public static JsonObject createObject(Map.Entry<String, Object>... properties) {
        JsonObject obj = new JsonObject();

        for (Map.Entry<String, Object> property : properties) {
            String key = property.getKey();
            Object value = property.getValue();
            if (value instanceof String) {
                obj.add(key, createPrimitive((String) value));
            } else if (value instanceof Boolean) {
                obj.add(key, createPrimitive((Boolean) value));
            } else if (value instanceof Number) {
                obj.add(key, createPrimitive((Number) value));
            } else if (value instanceof Character) {
                obj.add(key, createPrimitive((Character) value));
            } else if (value.getClass().isArray()) {
                obj.add(key, createArray((Object[]) value));
            } else {
                if (value instanceof Map.Entry) {
                    obj.add(key, createObject((Map.Entry<String, Object>) value));//
                } else {
                    obj.add(key, createObject((Map.Entry<String, Object>[]) value));//
                }
            }
        }

        return obj;
    }

    private static JsonElement createArray(Object[] value) {
        JsonArray array = new JsonArray();

        for (Object o : value) {
            if (o instanceof String) {
                array.add(createPrimitive((String) o));
            } else {
                if (o.getClass().isArray()) {
                    array.add(createObject((Map.Entry<String, Object>[]) o));
                } else if (o instanceof JsonElement) {
                    array.add((JsonElement) o);
                } else {
                    array.add(createObject((Map.Entry<String, Object>) o));
                }
            }
        }

        return array;
    }

    public static JsonElement createPrimitive(Boolean value) {
        return new JsonPrimitive(value);
    }

    public static JsonElement createPrimitive(String value) {
        return new JsonPrimitive(value);
    }

    public static JsonElement createPrimitive(Number value) {
        return new JsonPrimitive(value);
    }

    public static JsonElement createPrimitive(Character value) {
        return new JsonPrimitive(value);
    }
}

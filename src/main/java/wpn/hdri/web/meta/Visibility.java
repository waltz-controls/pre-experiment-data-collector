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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents meta field's visibility attribute in .json
 * <p/>
 * WEB means that the field is visible in both web and tango interfaces.
 * TANGO means - only in tango.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 15.03.12
 */
public enum Visibility {
    WEB("web"),
    TANGO("tango");

    private final String alias;

    private Visibility(String alias) {
        this.alias = alias;
    }

    private final static Map<String, Visibility> instances = new HashMap<String, Visibility>();

    static {
        for (Visibility visibility : Visibility.values()) {
            instances.put(visibility.alias, visibility);
        }
    }

    public static Visibility forAlias(String alias) {
        if (alias == null) {
            throw new IllegalArgumentException("alias can not be null.");
        }
        if (!instances.containsKey(alias)) {
            throw new IllegalArgumentException("No instance can be found for alias:" + alias);
        }
        return instances.get(alias);
    }
}

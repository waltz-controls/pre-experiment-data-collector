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

package JsonDS;

import wpn.hdri.tango.attribute.EnumAttrWriteType;
import wpn.hdri.tango.attribute.TangoAttribute;
import wpn.hdri.tango.data.format.TangoDataFormat;
import wpn.hdri.tango.data.type.ScalarTangoDataTypes;

/**
 * An Enum of JsonDS main attributes.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 26.03.12
 */
public enum JsonDSAttr {
    CRT_USER_NAME(new TangoAttribute<String>("CRT_USER_NAME", TangoDataFormat.<String>createScalarDataFormat(), ScalarTangoDataTypes.STRING, EnumAttrWriteType.READ, null)),
    CRT_BEAMTIME_ID(new TangoAttribute<String>("CRT_BEAMTIME_ID", TangoDataFormat.<String>createScalarDataFormat(), ScalarTangoDataTypes.STRING, EnumAttrWriteType.READ, null)),
    CRT_USER_SCAN(new TangoAttribute<String>("CRT_USER_SCAN", TangoDataFormat.<String>createScalarDataFormat(), ScalarTangoDataTypes.STRING, EnumAttrWriteType.READ, null));

    private final TangoAttribute<?> attr;

    private JsonDSAttr(TangoAttribute<?> attr) {
        this.attr = attr;
    }

    public <T> TangoAttribute<T> toTangoAttribute() {
        return (TangoAttribute<T>) attr;
    }
}

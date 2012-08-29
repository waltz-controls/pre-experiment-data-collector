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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fields sre set through reflection in {@link MetaDataFactory}
 * <p/>
 * Represents MetaData.json.
 * This class is used only to improve readability of the server logic.
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.01.12
 */
public final class MetaData {
    private List<MetaForm> forms = new ArrayList<MetaForm>();

    /**
     * Returns all descendant fields containing in this meta.
     *
     * @return
     */
    public Iterable<MetaField> getAllFields() {
        List<MetaField> result = new ArrayList<MetaField>();

        for (MetaForm form : forms) {
            for (Object fld : form.getAllFields()) {
                result.add((MetaField) fld);
            }
        }

        return Collections.unmodifiableList(result);
    }

    public List<MetaForm> getForms() {
        return Collections.unmodifiableList(forms);
    }

    /**
     *
     * @param id
     * @return MetaField found or null
     */
    public MetaField getFieldById(String id) {
        for (MetaField fld : getAllFields()) {
            if (fld.getId().equals(id)) {
                return fld;
            }
        }
        return null;
    }
}

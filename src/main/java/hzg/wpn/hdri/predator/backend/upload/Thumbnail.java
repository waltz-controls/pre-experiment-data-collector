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

package hzg.wpn.hdri.predator.backend.upload;

import javax.annotation.Nullable;

/**
 * Represents supported thumbnails of the uploaded documents.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 23.02.12
 */
public enum Thumbnail {
    PDF("/images/thumbnails/pdf_ico.png"),
    TIF("/images/thumbnails/TIF-Image-icon.png"),
    YAML("/images/thumbnails/document-checkbox-icon.png"),
    EMPTY("/images/thumbnails/Document-icon.png");

    private final String path;

    private Thumbnail(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return this.path;
    }

    public String getPath() {
        return this.path;
    }

    /**
     * Determines thumbnail by file's extension
     *
     * @param file a full file name (or even path)
     * @return a thumbnail or null
     */
    public static
    Thumbnail getThumbnail(String file){
        if (file.endsWith(".pdf")) {
            return PDF;
        } else if (file.endsWith(".tif") || file.endsWith(".tiff")) {
            return TIF;
        } else if (file.endsWith(".json") || file.endsWith(".yaml")) {
            return YAML;
        } else {
            return EMPTY;
        }
    }
}

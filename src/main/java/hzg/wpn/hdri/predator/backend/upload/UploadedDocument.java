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

import javax.annotation.concurrent.Immutable;
import java.net.URL;

/**
 * Aggregates values valuable for FileUpload engine.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.02.12
 */
@Immutable
public class UploadedDocument {
    private final String name;
    private final long size;
    private final URL url;
    private final URL thumbnail_url;
    private final URL delete;
    private final String deleteType;

    public UploadedDocument(String name, long size, URL url, URL thumbnail, URL delete, String deleteType) {
        this.name = name;
        this.size = size;
        this.url = url;
        this.thumbnail_url = thumbnail;
        this.delete = delete;
        this.deleteType = deleteType;
    }
}

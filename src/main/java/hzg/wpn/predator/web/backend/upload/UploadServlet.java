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

package hzg.wpn.predator.web.backend.upload;

import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URL;

/**
 * Handles files upload. Files are stored to {WEB_APP_ROOT}/home/{user}/upload directory.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 06.01.12
 */
public final class UploadServlet extends AbsUploadServlet {

    @Override
    protected UploadedDocument doPostInternal(HttpServletRequest req, HttpServletResponse resp, String fileName, FileItem item) throws Exception{
        String user = req.getRemoteUser();

        StringBuilder url = getUrl(req);
        StringBuffer requestUrl = req.getRequestURL();

        File destination = appCtx.getUserUploadDir(user).toFile();

        File file = new File(destination, fileName);
        item.write(file);

        UploadedDocument document = new UploadedDocument(
                fileName,
                file.length(),
                new URL(url.append(file.getName()).toString()),
                Thumbnail.getThumbnail(fileName).toURL(requestUrl), null, "DELETE");
        return document;
    }
}

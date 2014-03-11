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

import com.google.gson.Gson;
import hzg.wpn.hdri.predator.ApplicationContext;
import hzg.wpn.util.servlet.ServletUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles files upload. Files are stored to {WEB_APP_ROOT}/home/{user}/upload directory.
 * //TODO customize destination through application.properties
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 06.01.12
 */
public final class UploadServlet extends AbsUploadServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = req.getRemoteUser();
        try {
            List<FileItem> items = uploadHandler.parseRequest(req);


            StringBuilder url = getUrl(req);
            StringBuffer requestUrl = req.getRequestURL();

            List<UploadedDocument> documents = new ArrayList<UploadedDocument>();

            File destination = appCtx.getUserUploadDir(user).toFile();

            for (FileItem item : items) {
                if (!item.isFormField()) {
                    //item.getName() in IE returns the full file path on the client
                    //wrapping it with the File to retrieve a file name only
                    String fileName = new File(item.getName()).getName();
                    File file = new File(destination, fileName);
                    item.write(file);

                    UploadedDocument document = new UploadedDocument(
                            fileName,
                            file.length(),
                            new URL(url.append(file.getName()).toString()),
                            getThumbnail(fileName, requestUrl), null, "DELETE");
                    documents.add(document);
                }
            }

            gson.toJson(documents,resp.getWriter());
        } catch (FileUploadException e) {
            throw new ServletException("Unable to upload file.", e);
        } catch (IOException e) {
            throw new ServletException("Unable to get user's upload dir [user:" + user + "].", e);
        } catch (Exception e) {
            throw new ServletException("Unable to write file.", e);
        }
    }

}

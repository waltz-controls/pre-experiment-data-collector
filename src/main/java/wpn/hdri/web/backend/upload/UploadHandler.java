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

package wpn.hdri.web.backend.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import wpn.hdri.util.servlet.ServletUtils;
import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.backend.RequestParameter;
import wpn.hdri.web.backend.jsonp.JsonBaseServlet;
import wpn.hdri.web.data.UploadedDocument;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static wpn.hdri.web.backend.upload.Thumbnails.PDF;
import static wpn.hdri.web.backend.upload.Thumbnails.TIF;
import static wpn.hdri.web.data.Users.User;

/**
 * Handles files upload. Files are stored to {WEB_APP_ROOT}/home/{user}/upload directory.
 * //TODO customize destination through application.properties
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 06.01.12
 */
public final class UploadHandler extends JsonBaseServlet<UploadedDocument> {
    private static final String TMP_DIR_PATH = System.getProperty("java.io.tmpdir");
    private ServletFileUpload uploadHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        fileItemFactory.setSizeThreshold(1024 * 1024);//1MB
        //the following is not necessary due to default implementation
        //fileItemFactory.setRepository(new File(TMP_DIR_PATH));

        uploadHandler = new ServletFileUpload(fileItemFactory);
    }

    @Override
    //TODO dirty hack with overriding doPost
    protected String doPostInternal(User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException {
        return super.doGetInternal(user, requestParameters, req);
    }

    private URL getUrl(HttpServletRequest req, File file) throws MalformedURLException {
        return new URL(ServletUtils.getUrl(req).append("/home/").append(req.getRemoteUser()).append("/upload/").append(file.getName()).toString());
    }

    private URL getThumbnail(HttpServletRequest req, String file) throws MalformedURLException {
        if (file.endsWith(".pdf")) {
            return new URL(req.getRequestURL().append(PDF).toString());
        } else if (file.endsWith(".tif")) {
            return new URL(req.getRequestURL().append(TIF).toString());
        } else {
            return null;
        }
    }

    public UploadedDocument doCreate(User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException {
        throw new BackendException("Upload can not create anything.", new UnsupportedOperationException());
    }

    /**
     * Stores files from the request and returns an array of the {@link UploadedDocument}s.
     * This array is essential for proper UI show the uploaded files.
     *
     * @param user              current user
     * @param requestParameters
     * @param req
     * @return an array of the UploadedDocuments
     * @throws BackendException
     */
    public UploadedDocument[] doFindAll(User user, Map<RequestParameter, String> requestParameters, HttpServletRequest req) throws BackendException {
        try {
            List<FileItem> items = uploadHandler.parseRequest(req);
            List<UploadedDocument> documents = new ArrayList<UploadedDocument>();

            File destination = getApplicationContext().getUserUploadDir(user);

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
                            getUrl(req, file),
                            getThumbnail(req, fileName), null, "DELETE");
                    documents.add(document);
                }
            }

            return documents.toArray(new UploadedDocument[documents.size()]);
        } catch (FileUploadException e) {
            throw new BackendException("Unable to upload file.", e);
        } catch (MalformedURLException e) {
            throw new BackendException("Unable to get file url.", e);
        } catch (IOException e) {
            throw new BackendException("Unable to get user's upload dir [user:" + user.getName() + "].", e);
        } catch (Exception e) {
            throw new BackendException("Unable to write file.", e);
        }
    }
}

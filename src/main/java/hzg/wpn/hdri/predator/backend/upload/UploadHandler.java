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

import hzg.wpn.hdri.predator.ApplicationContext;
import hzg.wpn.hdri.predator.data.User;
import hzg.wpn.hdri.predator.data.Users;
import hzg.wpn.util.servlet.ServletUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.bitbucket.ingvord.web.json.JsonBaseServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static hzg.wpn.hdri.predator.backend.upload.Thumbnails.PDF;
import static hzg.wpn.hdri.predator.backend.upload.Thumbnails.TIF;

/**
 * Handles files upload. Files are stored to {WEB_APP_ROOT}/home/{user}/upload directory.
 * //TODO customize destination through application.properties
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 06.01.12
 */
public final class UploadHandler extends JsonBaseServlet<UploadedDocument, Void> {
    private static final String TMP_DIR_PATH = System.getProperty("java.io.tmpdir");
    private final ThreadLocal<List<FileItem>> items = new ThreadLocal<List<FileItem>>();
    private final ThreadLocal<StringBuilder> url = new ThreadLocal<StringBuilder>();
    private final ThreadLocal<StringBuffer> requestUrl = new ThreadLocal<StringBuffer>();
    private volatile ApplicationContext appCtx;
    private volatile ServletFileUpload uploadHandler;

    @Override
    public void init() throws ServletException {
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        fileItemFactory.setSizeThreshold(1024 * 1024);//1MB
        //the following is not necessary due to default implementation
        //fileItemFactory.setRepository(new File(TMP_DIR_PATH));
        ServletConfig config = getServletConfig();

        uploadHandler = new ServletFileUpload(fileItemFactory);
        appCtx = (ApplicationContext) config.getServletContext().getAttribute(ApplicationContext.APPLICATION_CONTEXT);
    }

    @Override
    //TODO dirty hack with overriding doPost
    protected String doPostInternal(HttpServletRequest req, HttpServletResponse res, Void params) throws ServletException {
        return doGetInternal(req, res, params);
    }

    @Override
    protected String doGetInternal(HttpServletRequest req, HttpServletResponse res, Void params) throws ServletException {
        try {
            items.set(uploadHandler.parseRequest(req));
            requestUrl.set(req.getRequestURL());
            url.set(getUrl(req));
            return super.doGetInternal(req, res, params);
        } catch (FileUploadException|MalformedURLException e) {
            //TODO log
            throw new ServletException(e);
        }
    }

    private StringBuilder getUrl(HttpServletRequest req) throws MalformedURLException {
        return new StringBuilder(ServletUtils.getUrl(req).append("/home/").append(req.getRemoteUser()).append("/upload/"));
    }

    private URL getThumbnail(String file) throws MalformedURLException {
        if (file.endsWith(".pdf")) {
            return new URL(requestUrl.get().append(PDF).toString());
        } else if (file.endsWith(".tif")) {
            return new URL(requestUrl.get().append(TIF).toString());
        } else {
            return null;
        }
    }

    /**
     * Stores files from the request and returns an array of the {@link UploadedDocument}s.
     * This array is essential for proper UI show the uploaded files.
     *
     * @param req
     * @return an array of the UploadedDocuments
     */
    public Collection<UploadedDocument> find_all(HttpServletRequest req, HttpServletResponse res, Void params) throws ServletException{
        User user = Users.getUser(req.getRemoteUser(), false, appCtx);
        try {
            List<UploadedDocument> documents = new ArrayList<UploadedDocument>();

            File destination = appCtx.getUserUploadDir(user);

            for (FileItem item : items.get()) {
                if (!item.isFormField()) {
                    //item.getName() in IE returns the full file path on the client
                    //wrapping it with the File to retrieve a file name only
                    String fileName = new File(item.getName()).getName();
                    File file = new File(destination, fileName);
                    item.write(file);

                    UploadedDocument document = new UploadedDocument(
                            fileName,
                            file.length(),
                            new URL(url.get().append(file.getName()).toString()),
                            getThumbnail(fileName), null, "DELETE");
                    documents.add(document);
                }
            }

            return documents;
            //TODO log and properly handle exceptions
        } catch (FileUploadException e) {
            throw new ServletException("Unable to upload file.", e);
        } catch (IOException e) {
            throw new ServletException("Unable to get user's upload dir [user:" + user.getName() + "].", e);
        } catch (Exception e) {
            throw new ServletException("Unable to write file.", e);
        }
    }
}

package hzg.wpn.hdri.predator.backend.upload;

import com.google.gson.Gson;
import hzg.wpn.hdri.predator.ApplicationContext;
import hzg.wpn.hdri.predator.ApplicationLoader;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11.03.14
 */
public abstract class AbsUploadServlet extends HttpServlet {
    protected final Gson gson = new Gson();
    protected volatile ApplicationContext appCtx;
    protected volatile ServletFileUpload uploadHandler;

    @Override
    public void init() throws ServletException {
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        fileItemFactory.setSizeThreshold(1024 * 1024);//1MB
        ServletConfig config = getServletConfig();

        uploadHandler = new ServletFileUpload(fileItemFactory);
        appCtx = (ApplicationContext) config.getServletContext().getAttribute(ApplicationLoader.APPLICATION_CONTEXT);
    }

    protected StringBuilder getUrl(HttpServletRequest req) throws MalformedURLException {
        return new StringBuilder(ServletUtils.getUrl(req).append("/home/").append(req.getRemoteUser()).append("/upload/"));
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = req.getRemoteUser();
        try {
            List<FileItem> items = uploadHandler.parseRequest(req);

            List<UploadedDocument> documents = new ArrayList<UploadedDocument>();

            for (FileItem item : items) {
                if (!item.isFormField()) {
                    //item.getName() in IE returns the full file path on the client
                    //wrapping it with the File to retrieve a file name only
                    String fileName = new File(item.getName()).getName();
                    UploadedDocument document = doPostInternal(req, resp, fileName, item);

                    documents.add(document);
                }
            }

            gson.toJson(documents, resp.getWriter());
        } catch (FileUploadException e) {
            throw new ServletException("Unable to upload file.", e);
        } catch (IOException e) {
            throw new ServletException("Unable to get user's upload dir [user:" + user + "].", e);
        } catch (Exception e) {
            throw new ServletException("Unable to write file.", e);
        }
    }

    protected abstract UploadedDocument doPostInternal(HttpServletRequest req, HttpServletResponse resp, String fileName, FileItem item) throws Exception;
}

package hzg.wpn.hdri.predator.backend.upload;

import com.google.gson.Gson;
import hzg.wpn.hdri.predator.ApplicationContext;
import hzg.wpn.util.servlet.ServletUtils;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
        appCtx = (ApplicationContext) config.getServletContext().getAttribute(ApplicationContext.APPLICATION_CONTEXT);
    }

    protected StringBuilder getUrl(HttpServletRequest req) throws MalformedURLException {
        return new StringBuilder(ServletUtils.getUrl(req).append("/home/").append(req.getRemoteUser()).append("/upload/"));
    }

    protected URL getThumbnail(String file, StringBuffer requestUrl) throws MalformedURLException {
        Thumbnail thumbnail = Thumbnail.getThumbnail(file);
        return new URL(requestUrl.append(thumbnail).toString());
    }

    protected abstract void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
}

package hzg.wpn.hdri.predator.backend.upload;

import hzg.wpn.hdri.predator.data.DataSetsManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11.03.14
 */
public class DataSetUploadServlet extends AbsUploadServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = req.getRemoteUser();
        Path tempFile = null;
        try {
            List<FileItem> items = uploadHandler.parseRequest(req);

            StringBuffer requestUrl = req.getRequestURL();

            List<UploadedDocument> documents = new ArrayList<UploadedDocument>();


            DataSetsManager manager = appCtx.getManager();
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    //item.getName() in IE returns the full file path on the client
                    //wrapping it with the File to retrieve a file name only
                    String fileName = new File(item.getName()).getName();

                    tempFile = Files.createTempFile(fileName, null);
                    item.write(tempFile.toFile());
                    Map<String,Object> json = gson.fromJson(Files.newBufferedReader(tempFile, Charset.forName("UTF-8")),HashMap.class);


                    DynaBean data = manager.newDataSet(user, fileName);
                    BeanUtils.populate(data, json);
                    manager.save(data);

                    UploadedDocument document = new UploadedDocument(
                            fileName,
                            Files.size(tempFile),
                            null,
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
        } finally {
            if(tempFile != null)
                Files.deleteIfExists(tempFile);//this may throw IOException
        }
    }
}

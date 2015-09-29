package hzg.wpn.predator.web.backend.upload;

import hzg.wpn.predator.web.data.DataSetsManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11.03.14
 */
public class DataSetUploadServlet extends AbsUploadServlet {

    @Override
    protected UploadedDocument doPostInternal(HttpServletRequest req, HttpServletResponse resp, String fileName, FileItem item) throws Exception {
        Path tempFile = null;
        try {
            String user = req.getRemoteUser();

            StringBuffer requestUrl = req.getRequestURL();


            tempFile = Files.createTempFile(fileName, null);
            item.write(tempFile.toFile());
            Map<String, Object> json = gson.fromJson(Files.newBufferedReader(tempFile, Charset.defaultCharset()), HashMap.class);


            DataSetsManager manager = appCtx.getManager();
            DynaBean data = manager.newDataSet(user, fileName);
            BeanUtils.populate(data, json);
            manager.save(data);

            UploadedDocument document = new UploadedDocument(
                    fileName,
                    Files.size(tempFile),
                    null,
                    Thumbnail.getThumbnail(fileName).toURL(requestUrl), null, "DELETE");
            return document;
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}

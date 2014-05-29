package hzg.wpn.hdri.predator.backend;

import hzg.wpn.hdri.predator.ApplicationLoader;
import hzg.wpn.util.base64.Base64OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 5/29/14@1:21 PM
 */
public class MetaYamlServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String callback = req.getParameter("callback");
        if (callback == null) throw new ServletException("callback is not defined");//send error response

        res.setContentType("application/javascript");

        String realPath = getServletContext().getRealPath("/");
        Path pathToYaml = Paths.get(realPath, ApplicationLoader.META_YAML);
        try (BufferedReader rdr = Files.newBufferedReader(pathToYaml, Charset.defaultCharset())) {

            Writer out;
            if (res.getBufferSize() == 0)
                out = new BufferedWriter(res.getWriter());
            else
                out = res.getWriter();

            out.append(callback).append("({value:'");

            long size = Files.size(pathToYaml);
            if (size * 2 > Integer.MAX_VALUE) throw new ServletException("yaml file is too big!");//TODO
            char[] buff = new char[(int) size];
            int eof = rdr.read(buff);
            String yaml = new String(buff).replaceAll("\\r\\n", "\n");
            String encodedYaml = Base64OutputStream.encode(yaml.getBytes());
            out.write(encodedYaml);

            out.append("'})");

            res.flushBuffer();
        }
    }
}

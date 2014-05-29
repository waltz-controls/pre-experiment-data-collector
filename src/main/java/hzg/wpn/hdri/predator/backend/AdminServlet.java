package hzg.wpn.hdri.predator.backend;

import hzg.wpn.hdri.predator.ApplicationContext;
import hzg.wpn.hdri.predator.ApplicationLoader;
import hzg.wpn.hdri.predator.meta.Meta;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author ingvord <tla-ingvord@yandex.ru>
 * @since 5/29/14@12:28 PM
 */
public class AdminServlet extends HttpServlet {
    private IndexServlet indexServlet = new IndexServlet();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        indexServlet.init(config);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String yaml = request.getParameter("yaml").replaceAll("\\r\\n", "\n");
        String realPath = getServletContext().getRealPath("/");
        Path pathToYaml = Paths.get(realPath, ApplicationLoader.META_YAML);

        try (BufferedWriter out = Files.newBufferedWriter(pathToYaml, Charset.defaultCharset())) {
            out.write(yaml);
        }

        ApplicationContext oldCtx = (ApplicationContext) getServletContext().getAttribute(ApplicationLoader.APPLICATION_CONTEXT);

        Meta meta = new Meta(pathToYaml);
        ApplicationContext ctx = new ApplicationContext(realPath, request.getContextPath(),
                oldCtx.getBeamtimeId(), oldCtx.getStorage(), oldCtx.getApplicationProperties(), meta, meta.extractDynaClass());

        getServletContext().setAttribute(ApplicationLoader.APPLICATION_CONTEXT, ctx);

        indexServlet.doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        indexServlet.doGet(request, response);
    }
}

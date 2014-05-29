package hzg.wpn.hdri.predator.backend;

import hzg.wpn.hdri.predator.ApplicationLoader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
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
        String yaml = new String(request.getParameter("yaml").trim().getBytes(), Charset.forName("UTF-8"));
        String realPath = getServletContext().getRealPath("/");
        try (BufferedWriter out = Files.newBufferedWriter(Paths.get(realPath, ApplicationLoader.META_YAML), Charset.forName("UTF-8"))) {
            out.write(yaml);
        }

        indexServlet.doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        indexServlet.doGet(request, response);
    }
}

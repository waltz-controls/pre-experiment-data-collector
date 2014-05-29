package hzg.wpn.hdri.predator.backend;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        indexServlet.doGet(request, response);
    }
}

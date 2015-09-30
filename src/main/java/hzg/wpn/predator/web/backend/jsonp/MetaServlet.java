package hzg.wpn.predator.web.backend.jsonp;

import hzg.wpn.predator.ApplicationContext;
import hzg.wpn.predator.meta.Meta;
import hzg.wpn.predator.web.ApplicationLoader;
import org.bitbucket.ingvord.web.json.JsonpBaseServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 04.02.14
 */
public class MetaServlet extends HttpServlet {
    private final static Logger LOG = LoggerFactory.getLogger(MetaServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter output = resp.getWriter();

        String callback = req.getParameter(JsonpBaseServlet.CALLBACK);
        if (callback == null) {
            LOG.error("callback is not present in the request");
            //throws exception on the client side
            output.write(JsonpBaseServlet.ERROR_RESPONSE);
            return;
        }

        Meta meta = ((ApplicationContext) getServletContext().getAttribute(ApplicationLoader.APPLICATION_CONTEXT)).getMeta();

        output.append(callback).append("(");
        meta.writeAsJson(output);
        output.append(");");
        output.flush();
    }
}

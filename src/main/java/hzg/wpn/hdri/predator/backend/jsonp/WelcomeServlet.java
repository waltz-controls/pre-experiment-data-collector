package hzg.wpn.hdri.predator.backend.jsonp;

import com.google.common.base.Function;

import com.google.common.collect.Iterables;
import hzg.wpn.hdri.predator.ApplicationContext;
import hzg.wpn.hdri.predator.data.DataSetsManager;
import hzg.wpn.util.beanutils.BeanUtilsHelper;
import org.apache.commons.beanutils.DynaBean;
import org.bitbucket.ingvord.web.json.JsonpBaseServlet;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 03.02.14
 */
public class WelcomeServlet extends JsonpBaseServlet<WelcomeServlet.Response,Void> {
    @Override
    public Response create(HttpServletRequest req, HttpServletResponse res, Void params) {
        ApplicationContext ctx = (ApplicationContext)getServletContext().getAttribute(ApplicationContext.APPLICATION_CONTEXT);
        DataSetsManager manager = ctx.getManager();
        Response response = new Response();
            String[] names = Iterables.toArray(Iterables.transform(manager.getUserDataSets(req.getRemoteUser()), new Function<DynaBean, String>() {
                @Override
                public String apply(@Nullable DynaBean input) {
                    return BeanUtilsHelper.getProperty(input, "name", String.class);
                }
            }),String.class);


            response.data = names;
            return response;
    }

    public static class Response {
        private String[] data;
    }
}

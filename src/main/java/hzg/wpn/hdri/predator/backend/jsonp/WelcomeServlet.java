package hzg.wpn.hdri.predator.backend.jsonp;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import hzg.wpn.hdri.predator.ApplicationContext;
import hzg.wpn.hdri.predator.data.DataSetsManager;
import hzg.wpn.util.beanutils.BeanUtilsHelper;
import org.apache.commons.beanutils.DynaBean;
import su.clan.tla.web.backend.json.JsonRequest;
import su.clan.tla.web.backend.json.JsonpBaseServlet;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 03.02.14
 */
public class WelcomeServlet extends JsonpBaseServlet<WelcomeServlet.Response,WelcomeServlet.Request> {
    @Override
    public Response create(JsonRequest<Request> req) {
        ApplicationContext ctx = (ApplicationContext)getServletContext().getAttribute(ApplicationContext.APPLICATION_CONTEXT);
        DataSetsManager manager = ctx.getManager();
        Response response = new Response();
        try {
            String[] names = Iterables.toArray(Iterables.transform(manager.getUserDataSets(req.getRemoteUser()), new Function<DynaBean, String>() {
                @Override
                public String apply(@Nullable DynaBean input) {
                    return BeanUtilsHelper.getProperty(input, "name", String.class);
                }
            }),String.class);


            response.data = names;
            return response;
        } catch (IOException e) {
            response.errors = new String[]{e.getMessage()};
            return response;
        }
    }

    @Override
    public Response delete(JsonRequest<Request> req) {
        throw new UnsupportedOperationException("This method is not supported in " + this.getClass());
    }

    @Override
    public Collection<Response> findAll(JsonRequest<Request> req) {
        throw new UnsupportedOperationException("This method is not supported in " + this.getClass());
    }

    public static class Response {
        private String[] data;
        private String[] errors;
    }

    public static class Request{

    }
}

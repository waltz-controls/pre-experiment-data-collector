package hzg.wpn.hdri.predator.backend.jsonp;

import hzg.wpn.hdri.predator.ApplicationContext;
import hzg.wpn.hdri.predator.ApplicationLoader;
import hzg.wpn.hdri.predator.data.DataSetsManager;
import hzg.wpn.util.beanutils.BeanUtilsHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaBeanPropertyMapDecorator;
import org.bitbucket.ingvord.web.RequestParameter;
import org.bitbucket.ingvord.web.json.JsonpBaseServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 05.02.14
 */
public class DataServlet extends JsonpBaseServlet<Object, DataServlet.Request> {
    private static final Logger LOG = LoggerFactory.getLogger(DataServlet.class);

    /**
     * To save bandwidth this method returns only names
     *
     * @param req
     * @param res
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    protected Collection<Object> find_all(HttpServletRequest req, HttpServletResponse res, Request params) throws ServletException {
        String user = req.getRemoteUser();
        if (user == null) {
            LOG.error("user is not set in the request!");
            throw new ServletException("User is null");
        }

        ApplicationContext ctx = (ApplicationContext) getServletContext().getAttribute(ApplicationLoader.APPLICATION_CONTEXT);
        DataSetsManager manager = ctx.getManager();
        List<Object> response = new ArrayList<>();

        for (final DynaBean bean : manager.getUserDataSets(user)) {
            response.add(new FindAllResponse(BeanUtilsHelper.getProperty(bean, "name", String.class)));
        }

        return response;
    }

    public static class FindAllResponse {
        private final String name;

        public FindAllResponse(String name) {
            this.name = name;
        }
    }

    /**
     * This methods implements the following:
     * if there is a template name differs from 'none' try to load data set defined by the template name
     * otherwise try to load data set defined by data-set-name
     * if resulting data set is null create a new one
     * <p/>
     * despite the data set is just has been created or loaded set its name to data-set-name
     * save data set
     *
     * @param req
     * @param res
     * @param params
     * @return
     * @throws ServletException
     */
    @Override
    protected Object create(HttpServletRequest req, HttpServletResponse res, DataServlet.Request params) throws ServletException {
        String user = req.getRemoteUser();
        if (user == null) {
            LOG.error("user is not set in the request!");
            throw new ServletException("User is null");
        }

        String dataSetNameToLoad = params.templateName != null && !"none".equals(params.templateName) ? params.templateName : params.dataSetName;
        ApplicationContext applicationContext = (ApplicationContext) getServletContext().getAttribute(ApplicationLoader.APPLICATION_CONTEXT);
        DataSetsManager manager = applicationContext.getManager();

        DynaBean data = manager.getUserDataSet(user, dataSetNameToLoad);

        if (data == null)
            data = manager.newDataSet(user, params.dataSetName);

        if (data == null) {
            LOG.error("Failed to load data set!");
            throw new ServletException("Can not create a new dataset");
        }

        try {
            //set new name
            data.set("name", params.dataSetName);
            manager.save(data);
        } catch (IOException e) {
            LOG.error("Failed to save data set", e);
            throw new ServletException("Can not save data!", e);
        }

        return new DynaBeanPropertyMapDecorator(data);
    }

    @Override
    protected Object delete(HttpServletRequest req, HttpServletResponse res, Request params) throws ServletException {
        String user = req.getRemoteUser();
        if (user == null) {
            LOG.error("user is not set in the request!");
            throw new ServletException("User is null");
        }

        ApplicationContext applicationContext = (ApplicationContext) getServletContext().getAttribute(ApplicationLoader.APPLICATION_CONTEXT);
        DataSetsManager manager = applicationContext.getManager();
        DynaBean data = manager.getUserDataSet(user, params.dataSetName);
        if (data == null) {
            LOG.error("Failed to load data set!");
            throw new ServletException("Can not find data set[" + params.dataSetName + "] for user[" + user + "]");
        }

        try {
            manager.delete(data);
        } catch (IOException e) {
            LOG.error("Failed to delete data set", e);
            throw new ServletException("Can not delete data set[" + params.dataSetName + "] for user[" + user + "]", e);
        }

        return new DynaBeanPropertyMapDecorator(data);
    }

    @Override
    protected Object update(HttpServletRequest req, HttpServletResponse res, DataServlet.Request params) throws ServletException {
        String user = req.getRemoteUser();
        if (user == null) {
            LOG.error("user is not set in the request!");
            throw new ServletException("User is null");
        }

        ApplicationContext applicationContext = (ApplicationContext) getServletContext().getAttribute(ApplicationLoader.APPLICATION_CONTEXT);
        DataSetsManager manager = applicationContext.getManager();
        DynaBean data = manager.getUserDataSet(user, params.dataSetName);
        if (data == null) {
            LOG.error("Failed to load data set!");
            throw new ServletException("Can not find data set[" + params.dataSetName + "] for user[" + user + "]");
        }
        try {
            BeanUtils.populate(data, req.getParameterMap());
            manager.save(data);
        } catch (IllegalAccessException | InvocationTargetException | IOException e) {
            LOG.error("Failed to update data set", e);
            throw new ServletException(e);
        }

        return new DynaBeanPropertyMapDecorator(data);
    }

    @Override
    protected Request getParamsContainer() {
        return new Request();
    }

    public static class Request {
        @RequestParameter("name")
        public String dataSetName;
        @RequestParameter("template")
        public String templateName;
    }
}

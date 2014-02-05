package hzg.wpn.hdri.predator.backend.jsonp;

import hzg.wpn.hdri.predator.ApplicationContext;
import hzg.wpn.hdri.predator.data.DataSetsManager;
import hzg.wpn.hdri.predator.storage.Storage;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.bitbucket.ingvord.web.json.JsonSerializable;
import org.bitbucket.ingvord.web.json.JsonpBaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 05.02.14
 */
public class DataServlet extends JsonpBaseServlet<Void,Void> {
    @Override
    protected Void create(HttpServletRequest req, HttpServletResponse res, Void params) throws ServletException {
        //TODO
        return null;
    }

    @Override
    protected Void update(HttpServletRequest req, HttpServletResponse res, Void params) throws ServletException {
        String user = req.getRemoteUser();
        if(user == null){
            throw new ServletException("User is null");
        }
        String dataSetName = req.getParameter("name");
        if(dataSetName == null){
            throw new ServletException("DataSet name is null");
        }

        ApplicationContext applicationContext = (ApplicationContext) getServletContext().getAttribute(ApplicationContext.APPLICATION_CONTEXT);
        DataSetsManager manager = applicationContext.getManager();
        try {
            DynaBean data = manager.getUserDataSet(user, dataSetName);

            //TODO fill in data
            BeanUtils.populate(data,req.getParameterMap());
        } catch (NoSuchElementException|IllegalAccessException|InvocationTargetException e) {
            throw new ServletException(e);
        }

        //TODO save data
        //TODO load data
        //TODO return bean as map and compare values on the client
        return null;
    }
}

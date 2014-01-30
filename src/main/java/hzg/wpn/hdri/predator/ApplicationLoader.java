package hzg.wpn.hdri.predator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.01.14
 */
public class ApplicationLoader implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        throw new UnsupportedOperationException("This method is not supported in " + this.getClass());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        throw new UnsupportedOperationException("This method is not supported in " + this.getClass());
    }
}

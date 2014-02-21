package hzg.wpn.hdri.predator.backend;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This filter ensures that requests from one user will be processed sequentially in FIFO order
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 21.02.14
 */
public class UserFilter implements Filter {
    private final static ConcurrentMap<String,Object> USERS = new ConcurrentHashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String userName = ((HttpServletRequest)request).getRemoteUser();
        Object userLock = getOrCreate(userName);
        //ensure FIFO requests order
        synchronized (userLock){
            chain.doFilter(request,response);
        }
    }

    private Object getOrCreate(String userName) {
        USERS.putIfAbsent(userName, new Object());//consensus
        return USERS.get(userName);
    }

    @Override
    public void destroy() {

    }
}

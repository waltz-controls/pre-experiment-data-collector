package hzg.wpn.hdri.predator.backend;

import hzg.wpn.hdri.predator.data.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
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
        Object token = getOrCreate(userName);
        //ensure FIFO requests order
        synchronized (token){
            chain.doFilter(request,response);
        }
    }

    private Object getOrCreate(String userName) {
        Object token = USERS.get(userName);
        if(token == null){
            USERS.putIfAbsent(userName, new Object());//consensus value for all threads
        }
        return USERS.get(userName);
    }

    @Override
    public void destroy() {

    }
}

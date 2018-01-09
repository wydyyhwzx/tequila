package com.tequila.filter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by wangyudong on 2018/1/9.
 * 登录状态认证filter
 */
@Order(2)
@WebFilter(urlPatterns = "/*", initParams = {@WebInitParam(name="ignorePath",value="/user/login,/user/register")}, filterName = "loginFilter")
public class LoginFilter implements Filter{
    private Logger logger = LoggerFactory.getLogger(LoginFilter.class);
    private static String IGNORE_PATH = "ignorePath";

    Set<Pattern> ignorePath = new HashSet();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String p = filterConfig.getInitParameter(IGNORE_PATH);
        if(p == null) {
            p = "";
        }
        ArrayList list = new ArrayList();
        String[] disableRoot = p.split(",");
        int length = disableRoot.length;

        for(int i = 0; i < length; ++i) {
            String s = disableRoot[i];
            s = s.trim();
            if(StringUtils.isNotBlank(s) && !list.contains(s)) {
                list.add(s);
                this.ignorePath.add(Pattern.compile(s.replace("*", ".*")));
            }
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String uri = request.getRequestURI();
        Iterator<Pattern> result = this.ignorePath.iterator();

        while(result.hasNext()) {
            Pattern pattern = result.next();
            if(pattern.matcher(uri).matches()) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }

        logger.info("LoginFilter do...");
        //todo
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}

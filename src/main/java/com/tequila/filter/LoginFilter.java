package com.tequila.filter;

import com.tequila.common.Constants;
import com.tequila.common.StatusCode;
import com.tequila.common.UserUtil;
import com.tequila.domain.Result;
import com.tequila.mapper.UserMapper;
import com.tequila.model.UserDO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by wangyudong on 2018/1/9.
 * 登录状态认证filter
 */
@Order(2)
@WebFilter(urlPatterns = "/*", initParams = {@WebInitParam(name="ignorePath",value="/user/login,/user/register")}, filterName = "loginFilter")
public class LoginFilter implements Filter{
    private static  final Logger logger = LoggerFactory.getLogger(LoginFilter.class);
    private static final String IGNORE_PATH = "ignorePath";

    @Resource
    private UserMapper userMapper;

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
        try {
            String uri = request.getRequestURI();
            Iterator<Pattern> result = this.ignorePath.iterator();

            while(result.hasNext()) {
                Pattern pattern = result.next();
                if(pattern.matcher(uri).matches()) {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
            }

            int id = 0;
            String token = null;
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(Constants.uid))
                    id = Integer.parseInt(cookie.getValue());
                if (cookie.getName().equals(Constants.loginToken))
                    token = cookie.getValue();
            }

            if (id < 1 || StringUtils.isBlank(token)){
                sendError(response, StatusCode.LOGIN_ERROR);
                return;
            }

            UserDO user = userMapper.findById(id);
            if (null == user || !token.equals(user.getToken()) || new Date().after(user.getTokenExpire())) {
                sendError(response, StatusCode.LOGIN_ERROR);
                return;
            }
            UserUtil.setUser(user);
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            logger.error("[LoginFilter] err", e);
            sendError(response, StatusCode.SYSTEM_ERROR);
            return;
        } finally {
            UserUtil.clear();
        }
    }

    @Override
    public void destroy() {

    }

    private void sendError(HttpServletResponse response, StatusCode statusCode) throws IOException{
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.getOutputStream().write(Result.fail(statusCode).toErrorByte());
        response.getOutputStream().flush();
    }
}

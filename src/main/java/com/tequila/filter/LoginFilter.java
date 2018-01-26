package com.tequila.filter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tequila.common.*;
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
@WebFilter(urlPatterns = "/*", initParams = {@WebInitParam(name="ignorePath",value="/user/login,/user/register,/user/getVerifyCode,/user/findPassword,/user/activate/*")}, filterName = "loginFilter")
public class LoginFilter implements Filter{
    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);
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

            String stringId = CookieUtil.getValue(request, CookieEnum.UID);
            if (StringUtils.isBlank(stringId)) {
                sendError(response, StatusCode.LOGIN_ERROR);
                return;
            }
            int id = Integer.parseInt(stringId);
            if (id < 1){
                sendError(response, StatusCode.LOGIN_ERROR);
                return;
            }
            String token = CookieUtil.getValue(request, CookieEnum.LOGIN_TOKEN);
            if (StringUtils.isBlank(token)){
                sendError(response, StatusCode.LOGIN_ERROR);
                return;
            }

            UserDO user = userMapper.findById(id);
            if (null == user || !token.equals(user.getToken()) || new Date().after(user.getTokenExpire())) {
                sendError(response, StatusCode.LOGIN_ERROR);
                return;
            }

            ObjectNode extendJson = UserUtil.getExtendJson(user);
            if (extendJson.has(Constants.extendActivateCode)) {
                sendError(response, StatusCode.NO_ACTIVATE_ERROR);
                return;
            }
            user.transformExtendToJson(extendJson);

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

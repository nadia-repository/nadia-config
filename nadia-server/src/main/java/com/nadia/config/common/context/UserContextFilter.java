package com.nadia.config.common.context;

import com.alibaba.fastjson.JSONObject;
import com.nadia.config.redis.RedisService;
import com.nadia.config.utils.RedisKeyUtil;
import com.nadia.config.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@Slf4j
public class UserContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String token= ((HttpServletRequest)request).getHeader("X-Token");
        if(StringUtils.isEmpty(token)){
            token = ((HttpServletRequest)request).getParameter("X-Token");
        }
        String servletPath = ((RequestFacade) request).getServletPath();

        log.info("X-Token:{}  ,path:{}",token,servletPath);
        if(StringUtils.isEmpty(token)){
            log.info("doFilter token is empty");
            UserContextHolder.setUserDetail(new UserDetail());
        }else {
            RedisService redisService = SpringUtils.getApplicationContext().getBean(RedisService.class);
            String user = redisService.get(RedisKeyUtil.getToken(token));
            UserDetail userDetail = JSONObject.parseObject(user, UserDetail.class);
            userDetail.setToken(token);
            UserContextHolder.setUserDetail(userDetail);
        }
        UserContextHolder.setNow(new Date());
        try {
            chain.doFilter(request, response);
        }finally {
            UserContextHolder.clear();
        }
    }

    @Override
    public void destroy() {
    }
}

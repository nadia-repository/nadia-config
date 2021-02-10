package com.nadia.config.common.context;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.nadia.config.redis.ConfigCenterRedisService;
import com.nadia.config.utils.RedisKeyUtil;
import com.nadia.config.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
public class UserContextFilter implements Filter {

	private List<String> excludedUris = Lists.newArrayList();

	@Override
	public void init(FilterConfig filterConfig) {
		String excludedUriString = filterConfig.getInitParameter("excludedUri");
		if (!StringUtils.isEmpty(excludedUriString)) {
			excludedUris.addAll(Arrays.asList(excludedUriString.split("[,]")));
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		String token = ((HttpServletRequest) request).getHeader("X-Token");
		if (StringUtils.isEmpty(token)) {
			token = request.getParameter("X-Token");
		}
		String servletPath = ((HttpServletRequest) request).getServletPath();

		log.info("X-Token:{}, path:{}", token, servletPath);
		if (!excludeUri(servletPath)) {
			if (StringUtils.isEmpty(token)) {
				request.getRequestDispatcher("/error/session/empty").forward(request, response);
				return;
			} else {
				ConfigCenterRedisService configCenterRedisService = SpringUtils.getApplicationContext().getBean(ConfigCenterRedisService.class);
				String user = configCenterRedisService.get(RedisKeyUtil.getToken(token));
				if (StringUtils.isEmpty(user)) {
					request.getRequestDispatcher("/error/session/expired").forward(request, response);
					return;
				}
				UserDetail userDetail = JSONObject.parseObject(user, UserDetail.class);
				userDetail.setToken(token);
				UserContextHolder.setUserDetail(userDetail);
			}
			UserContextHolder.setNow(new Date());
		}
		try {
			chain.doFilter(request, response);
		} finally {
			UserContextHolder.clear();
		}
	}

	private boolean excludeUri(String uri) {
		if (CollectionUtils.isEmpty(excludedUris)) {
			return false;
		}
		for (String excludedUri : excludedUris) {
			if (uri.trim().toLowerCase().matches(excludedUri.trim().toLowerCase().replace("*", ".*"))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void destroy() {
	}
}

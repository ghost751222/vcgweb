package com.consilium.vcg.interceptors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        logger.info(request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView model) throws Exception {
        logger.info("Post handle method - check execution time of handling {}" ,request.getRequestURI());

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.info("afterCompletion handle method - check execution time of handling {}" ,request.getRequestURI());
        logger.info(handler.toString());
       // logger.info(ex.getMessage());
    }
}

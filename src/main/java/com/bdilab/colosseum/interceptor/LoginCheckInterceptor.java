package com.bdilab.colosseum.interceptor;

import com.bdilab.colosseum.response.HttpCode;
import com.bdilab.colosseum.response.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author Humphrey
 * @Date 2019/11/6 10:39
 * @Version 1.0
 **/
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Autowired
    private ObjectMapper mapper;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if (httpServletRequest.getSession().getAttribute("user_id") == null) {
            ResponseResult result = new ResponseResult(HttpCode.UNAUTHORIZED.getCode(), "未登录", false, null);
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            httpServletResponse.getWriter().write(mapper.writeValueAsString(result));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}

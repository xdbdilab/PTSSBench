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
import javax.servlet.http.HttpSession;

/**
 * @Author liran
 **/
@Component
public class AdminCheckInterceptor implements HandlerInterceptor {
    @Autowired
    private ObjectMapper mapper;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        if (session.getAttribute("user_id") == null || (Integer) session.getAttribute("role") != 1) {
            ResponseResult result = new ResponseResult(HttpCode.UNAUTHORIZED.getCode(), "无权限访问", false, null);
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.setCharacterEncoding("UTF-8");
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

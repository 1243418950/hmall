package com.hmall.common.interceptors;


import cn.hutool.core.util.StrUtil;
import com.hmall.common.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        return HandlerInterceptor.super.preHandle(request, response, handler);
        //获取用户信息
        String userInfo = request.getHeader("user-info");
        //判断是否获取用户，如果有，存入threadloacl
        if(StrUtil.isNotEmpty(userInfo)){
            UserContext.setUser(Long.valueOf(userInfo));
        }
        //放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        //清理用户
        UserContext.removeUser();
    }
}

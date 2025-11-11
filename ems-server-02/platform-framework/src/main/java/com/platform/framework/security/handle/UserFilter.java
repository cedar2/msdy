package com.platform.framework.security.handle;

import com.platform.common.constant.UserConstants;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserFilter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        LoginUser loginUser= SecurityUtils.getLoginUser();
        if(loginUser != null){
            ApiThreadLocalUtil.set(loginUser);
        }else {
            loginUser=new LoginUser();
            loginUser.setClientId(UserConstants.ADMIN_CLIENTID);
            ApiThreadLocalUtil.set(loginUser);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        ApiThreadLocalUtil.unset();
    }

}


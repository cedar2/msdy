package com.platform.common.redis.thread;

import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.LoginUser;
import org.springframework.lang.Nullable;

/**
 * 线程存储工具
 *
 * @author c
 */
public class ApiThreadLocalUtil {

    private static final ThreadLocal<LoginUser> LOCAL = new ThreadLocal<>();

    public static void set(LoginUser localVo) {
        LOCAL.set(localVo);
    }

    public static void unset() {
        LOCAL.remove();
    }

    public static LoginUser get() {
        return LOCAL.get();
    }

    public static String getLoginUserClientId() {
        return get().getClientId();
    }


    public static String getLoginUserUserName() {
        return get().getUsername();
    }

    @Nullable
    public static Long getLoginUserId() {
        LoginUser user = get();
        if (user == null) {
            return null;
        }

        if (user.getUserid() != null) {
            return user.getUserid();
        }

        SysUser sysUser = user.getSysUser();
        if (sysUser == null) {
            return null;
        }

        return sysUser.getUserId();
    }
}

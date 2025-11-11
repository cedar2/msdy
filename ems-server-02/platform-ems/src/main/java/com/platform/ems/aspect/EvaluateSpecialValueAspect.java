package com.platform.ems.aspect;

import cn.hutool.core.util.StrUtil;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.base.SysAuthorityEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Pointcut;

import java.util.List;

/**
 * @author Straw
 * @date 2023/1/13
 */

public class EvaluateSpecialValueAspect {

    // 配置织入点
    @Pointcut("bean(sysAuthority*ServiceImpl)")
    public void pointCut() {
    }

    // 在事件通知类型中申明returning即可获取返回值
    @SuppressWarnings("unchecked")
    @AfterReturning(value = "pointCut()",
                    returning = "returnValue")
    public void doAfterReturn(JoinPoint ignoredPoint, Object returnValue) {
        if (returnValue instanceof SysAuthorityEntity) {
            doEvaluation((SysAuthorityEntity) returnValue);
            return;
        }

        if (returnValue instanceof List) {
            ((List<SysAuthorityEntity>) returnValue).forEach(this::doEvaluation);
        }
    }

    private void doEvaluation(SysAuthorityEntity field) {
        if (StrUtil.isNotEmpty(field.getCreateSource())) {
            return;
        }

        String clientId = ApiThreadLocalUtil.getLoginUserClientId();
        if (StrUtil.isEmpty(clientId)) {
            return;
        }

        field.setCreateSource("10000".equals(clientId) ? "系统" : "用户");
    }

}

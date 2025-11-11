package com.platform.ems.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.platform.common.utils.StringUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.annotation.RoleDataScope;
import com.platform.api.service.RemoteSystemService;
import com.platform.common.core.domain.model.SysRoleDataAuthFieldValue;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.LoginUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据角色权限字段字段过滤处理
 *
 * @author platform
 */
@Aspect
@Component
public class RoleDataScopeAspect {

    @Autowired
    private static RemoteSystemService remoteSystemService;

    private static void init() {
        remoteSystemService = SpringUtil.getBean(RemoteSystemService.class);
    }

    /**
     * 配置织入点
     */
    @Pointcut("@annotation(com.platform.ems.annotation.RoleDataScope)")
    public void roleDataScopePointCut() { }

    @Before("roleDataScopePointCut()")
    public void doBefore(JoinPoint point) throws Throwable {
        init();
        handleFieldScope(point);
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private RoleDataScope getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(RoleDataScope.class);
        }
        return null;
    }

    /**
     * 处理
     */
    protected void handleFieldScope(final JoinPoint joinPoint) throws NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 获得注解
        RoleDataScope controllerDataScope = getAnnotationLog(joinPoint);
        if (controllerDataScope == null) {
            return;
        }
        // 获取当前的用户
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if (StringUtils.isNotNull(loginUser)) {
            SysUser currentUser = loginUser.getSysUser();
            // 如果是超级管理员，则不过滤数据 或者查无数据角色权限字段
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin()) {
                // 数据处理
                scopeFilter(joinPoint, currentUser, controllerDataScope.loc(), controllerDataScope.objectCode());
            }
        }
    }

    /**
     * 数据范围过滤
     * @param joinPoint 切点
     * @param user      用户
     */
    public static void scopeFilter(JoinPoint joinPoint, SysUser user, Integer loc, String objectCode) throws IllegalAccessException,
            NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        // 符合条件 用户有数据角色权限字段值 且 符合所传入的 权限对象
        if (CollUtil.isNotEmpty(user.getFieldValueList()) && StrUtil.isNotBlank(objectCode)) {
            List<SysRoleDataAuthFieldValue> list = user.getFieldValueList().stream()
                    .filter(o->objectCode.equals(o.getObjectCode()) && StrUtil.isNotBlank(o.getAuthorityFieldValue())).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(list)) {
                List<String> codeList = list.stream().map(SysRoleDataAuthFieldValue::getAuthorityFieldParam).collect(Collectors.toList());
                // 字段名 ， 字段值
                Map<String, String> codeMap = list.stream().collect(Collectors.toMap(SysRoleDataAuthFieldValue::getAuthorityFieldParam, SysRoleDataAuthFieldValue::getAuthorityFieldValue));
                Object params = joinPoint.getArgs()[loc];
                if (StringUtils.isNotNull(params)) {
                    Field[] fields = params.getClass().getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        Field field = fields[i];
                        if (codeList.contains(field.getName())) {
                            //获取该对象的字节码对象
                            Class<?> clazz = params.getClass();
                            String fieldMethodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                            // 获取对象属性的类型
                            String typeName = field.getGenericType().getTypeName();
                            //第一个参数是方法名，后续参数为该方法的参数的字节码对象，需要按顺序填写
                            Method method = clazz.getMethod(fieldMethodName, Class.forName(typeName));
                            //执行方法 把权限字段值写入 过滤字段中
                            method.invoke(params, codeMap.get(field.getName()));
                        }
                    }
                }
                else {
                    throw new RuntimeException("接口参数异常，请联系管理员");
                }
            }
        }
    }

}

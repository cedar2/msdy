package com.platform.ems.aspect;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.platform.common.exception.FieldAuthorizeException;
import com.platform.common.utils.StringUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.annotation.CreatorScope;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.api.service.RemoteSystemService;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.system.domain.SysRoleMenu;
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

/**
 * 字段过滤处理
 *
 * @author platform
 */
@Aspect
@Component
public class CreatorScopeAspect {

    @Autowired
    private static RemoteSystemService remoteSystemService;

    private static void init() {
        remoteSystemService = SpringUtil.getBean(RemoteSystemService.class);
    }

    // 配置织入点
    @Pointcut("@annotation(com.platform.ems.annotation.CreatorScope)")
    public void fieldScopePointCut() {
    }

    @Before("fieldScopePointCut()")
    public void doBefore(JoinPoint point) throws Throwable {
        init();
        handleFieldScope(point);
    }

    protected void handleFieldScope(final JoinPoint joinPoint) throws NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 获得注解
        CreatorScope controllerDataScope = getAnnotationLog(joinPoint);
        if (controllerDataScope == null) {
            return;
        }
        // 获取当前的用户
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if (StringUtils.isNotNull(loginUser)) {
            SysUser currentUser = loginUser.getSysUser();
            // 如果是超级管理员，则不过滤数据
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin()) {
                fieldScopeFilter(joinPoint, currentUser, controllerDataScope.loc(), controllerDataScope.fieldName(), controllerDataScope.perms(), controllerDataScope.note());
            }
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user      用户
     * @param fieldName 字段名
     */
    public static void fieldScopeFilter(JoinPoint joinPoint, SysUser user, Integer loc, String fieldName, String perms, String note) throws IllegalAccessException,
            NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        Object params = joinPoint.getArgs()[loc];
        if (StringUtils.isNotNull(params)) {
            Field[] fields = params.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (field.getName().equals(fieldName)) {
                    //获取该对象的字节码对象
                    Class<?> clazz = params.getClass();
                    String fieldMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    // 获取对象属性的类型
                    String typeName = field.getGenericType().getTypeName();
                    //第一个参数是方法名，后续参数为该方法的参数的字节码对象，需要按顺序填写
                    Method method = clazz.getMethod(fieldMethodName, Class.forName(typeName));
                    //
                    Long[] roleIds = null;
                    List<SysRole> roleList = user.getRoles();
                    if (CollectionUtil.isNotEmpty(roleList)){
                        roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
                    }
                    SysRoleMenu roleMenu = new SysRoleMenu();
                    roleMenu.setRoleIds(roleIds);
                    roleMenu.setPerms(perms);
                    boolean isAll = true;
                    if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
                        isAll = remoteSystemService.isHavePerms(roleMenu).getData();
                    }
                    if (!isAll){
                        String creatorAccount = ApiThreadLocalUtil.get().getSysUser().getUserName();
                        if (creatorAccount != null && !"".equals(creatorAccount)){
                            //执行方法
                            method.invoke(params, creatorAccount);
                            // 项目档案额外处理
                            if (ConstantsAuthorize.PDM_PROJECT_ALL.equals(perms)) {
                                // 若用户所属角色没有勾选菜单的“查询所有数据”权限，则用户查询该菜单时，
                                // 只能看到 该用户自己创建的数据 或 项目负责人是用户对应员工档案 的项目数据
                                Method method2 = clazz.getMethod("setCurrentUserIsLeaderSid", Class.forName("java.lang.Long"));
                                //执行方法
                                method2.invoke(params, user.getStaffSid());
                            }
                        }
                    }
                }
            }
        }
        else {
            throw new RuntimeException("接口参数异常，请联系管理员");
        }
    }


    /**
     * 是否存在注解，如果存在就获取
     */
    private CreatorScope getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(CreatorScope.class);
        }
        return null;
    }

}

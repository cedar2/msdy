package com.platform.ems.aspect;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.platform.common.exception.FieldAuthorizeException;
import com.platform.common.utils.StringUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.annotation.FieldScope;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.service.IBasStaffService;
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
public class FieldScopeAspect {

    @Autowired
    private static IBasStaffService basStaffService;
    @Autowired
    private static RemoteSystemService remoteSystemService;

    private static void init() {
        basStaffService = SpringUtil.getBean(IBasStaffService.class);
        remoteSystemService = SpringUtil.getBean(RemoteSystemService.class);
    }

    // 配置织入点
    @Pointcut("@annotation(com.platform.ems.annotation.FieldScope)")
    public void fieldScopePointCut() {
    }

    @Before("fieldScopePointCut()")
    public void doBefore(JoinPoint point) throws Throwable {
        init();
        handleFieldScope(point);
    }

    protected void handleFieldScope(final JoinPoint joinPoint) throws NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 获得注解
        FieldScope controllerDataScope = getAnnotationLog(joinPoint);
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
                    Class<?> clazz = params.getClass(); //获取该对象的字节码对象
                    String fieldMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    String typeName = field.getGenericType().getTypeName();	// 获取对象属性的类型
                    Method method = clazz.getMethod(fieldMethodName, Class.forName(typeName)); //第一个参数是方法名，后续参数为该方法的参数的字节码对象，需要按顺序填写
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
                        boolean flag = true;
                        if (ConstantsAuthorize.EMS_PLANT_ALL.equals(perms)){
                            /*
                             * 获取此账号档案的“员工sid”，在通过“员工sid”，在员工档案获取此员工所属的工厂；
                             * 如工厂为空，则无法查看任何数据；如工厂不为空，则显示“工厂”为此工厂的计薪量申报数据。
                             */
                            Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
                            if (staffSid != null){
                                BasStaff staff = basStaffService.selectBasStaffById(staffSid);
                                if (staff.getDefaultPlantSid() != null){
                                    method.invoke(params, staff.getDefaultPlantSid()); //执行方法
                                    flag = false;
                                }
                            }
                        }
                        if (flag) {
                            throw new FieldAuthorizeException(note);
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
    private FieldScope getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(FieldScope.class);
        }
        return null;
    }

}

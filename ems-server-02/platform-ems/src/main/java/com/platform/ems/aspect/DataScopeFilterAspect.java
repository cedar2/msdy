package com.platform.ems.aspect;

import cn.hutool.core.util.StrUtil;
import com.platform.common.utils.StringUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.annotation.DataScopeFilter;
import com.platform.ems.domain.SysRoleAuthorityFieldValue;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.service.ISysRoleAuthorityFieldValueService;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.LoginUser;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据过滤处理
 *
 * @author c
 */
@Aspect
@Component
@SuppressWarnings("all")
public class DataScopeFilterAspect {
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";


    private static ISysRoleAuthorityFieldValueService sysRoleAuthorityFieldValueService;

    @Autowired
    public DataScopeFilterAspect(ISysRoleAuthorityFieldValueService sysRoleAuthorityFieldValueService){
        DataScopeFilterAspect.sysRoleAuthorityFieldValueService = sysRoleAuthorityFieldValueService;
    }


    // 配置织入点
    @Pointcut("@annotation(com.platform.ems.annotation.DataScopeFilter)")
    public void dataScopePointCut() {
    }

    @Before("dataScopePointCut()")
    public void doBefore(JoinPoint point) throws Throwable {
        handleDataScope(point);
    }

    protected void handleDataScope(final JoinPoint joinPoint) {
        // 获得注解
        DataScopeFilter controllerDataScope = getAnnotationLog(joinPoint);
        if (controllerDataScope == null) {
            return;
        }
        // 获取当前的用户
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if (StringUtils.isNotNull(loginUser)) {
            SysUser currentUser = loginUser.getSysUser();
            // 如果是超级管理员，则不过滤数据
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin()) {
                if (StrUtil.isNotBlank(controllerDataScope.fieldName())){
                    dataScopeFilter(joinPoint, currentUser, controllerDataScope.fieldCode(), controllerDataScope.fieldName(), controllerDataScope.Alias());
                }
                else {
                    dataScopeFilter(joinPoint, currentUser, controllerDataScope.dataObject(), controllerDataScope.Alias());
                }
            }
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user      用户
     * @param dataObject 数据对象
     * @param Alias 表别名
     */
    public static void dataScopeFilter(JoinPoint joinPoint, SysUser user,String dataObject, String Alias) {
        StringBuilder sqlString = new StringBuilder();
        if (StrUtil.isBlank(Alias) || StrUtil.isBlank(dataObject)){
            return;
        }
        //得到角色id列表
        List<Long> roleIdList = user.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList());
        //获取所有角色所属权限
        SysRoleAuthorityFieldValue base = new SysRoleAuthorityFieldValue().setRoleIdList(roleIdList).setDataobjectCategoryCode(dataObject);
        List<SysRoleAuthorityFieldValue> roleAuthorityFieldValueList = sysRoleAuthorityFieldValueService.selectMoreRoleAuthorityFieldValueList(base);
        //如果存在权限控制
        if (CollectionUtils.isNotEmpty(roleAuthorityFieldValueList)){
            for (SysRoleAuthorityFieldValue fieldValue : roleAuthorityFieldValueList) {
                sqlString.append(StringUtils.format(
                        " AND {}.{} = '{}' ", Alias, fieldValue.getDatabaseFieldname(),fieldValue.getAuthorityFieldValue()));
            }
        }

        if (StringUtils.isNotBlank(sqlString.toString())) {
            Object params = joinPoint.getArgs()[0];
            if (StringUtils.isNotNull(params) && params instanceof EmsBaseEntity) {
                EmsBaseEntity baseEntity = (EmsBaseEntity)params;
                baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            }
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user      用户
     * @param fieldCode 权限字段
     * @param fieldName 字段名
     * @param Alias 表别名
     */
    public static void dataScopeFilter(JoinPoint joinPoint, SysUser user, String fieldCode, String fieldName, String Alias) {
        StringBuilder sqlString = new StringBuilder();
        if (StrUtil.isBlank(Alias) || StrUtil.isBlank(fieldName)){
            return;
        }
        //得到角色id列表
        List<Long> roleIdList = user.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList());
        //获取所有角色所属权限
        SysRoleAuthorityFieldValue base = new SysRoleAuthorityFieldValue()
                .setRoleIdList(roleIdList);
        if (StrUtil.isNotBlank(fieldCode)){
            base.setAuthorityFieldCode(fieldCode);
        }
        List<SysRoleAuthorityFieldValue> roleAuthorityFieldValueList = sysRoleAuthorityFieldValueService.selectMoreRoleAuthorityFieldValueList(base);
        //如果存在权限控制
        if (CollectionUtils.isNotEmpty(roleAuthorityFieldValueList)){
            for (SysRoleAuthorityFieldValue fieldValue : roleAuthorityFieldValueList) {
                sqlString.append(StringUtils.format(
                        " OR {}.{} = '{}' ", Alias, fieldName,fieldValue.getAuthorityFieldValue()));
            }
        }

        if (StringUtils.isNotBlank(sqlString.toString())) {
            Object params = joinPoint.getArgs()[0];
            if (StringUtils.isNotNull(params) && params instanceof EmsBaseEntity) {
                EmsBaseEntity baseEntity = (EmsBaseEntity)params;
                baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            }
        }
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private DataScopeFilter getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(DataScopeFilter.class);
        }
        return null;
    }
}

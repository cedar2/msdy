package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SysAuthorityObjectField;

/**
 * 权限对象-字段明细Service接口
 *
 * @author chenkw
 * @date 2021-12-28
 */
public interface ISysAuthorityObjectFieldService extends IService<SysAuthorityObjectField> {
    /**
     * 查询权限对象-字段明细
     *
     * @param authorityObjectFieldSid 权限对象-字段明细ID
     * @return 权限对象-字段明细
     */
    public SysAuthorityObjectField selectSysAuthorityObjectFieldById(Long authorityObjectFieldSid);

    /**
     * 查询权限对象-字段明细列表
     *
     * @param sysAuthorityObjectField 权限对象-字段明细
     * @return 权限对象-字段明细集合
     */
    public List<SysAuthorityObjectField> selectSysAuthorityObjectFieldList(SysAuthorityObjectField sysAuthorityObjectField);

    /**
     * 新增权限对象-字段明细
     *
     * @param sysAuthorityObjectField 权限对象-字段明细
     * @return 结果
     */
    public int insertSysAuthorityObjectField(SysAuthorityObjectField sysAuthorityObjectField);

    /**
     * 修改权限对象-字段明细
     *
     * @param sysAuthorityObjectField 权限对象-字段明细
     * @return 结果
     */
    public int updateSysAuthorityObjectField(SysAuthorityObjectField sysAuthorityObjectField);

    /**
     * 变更权限对象-字段明细
     *
     * @param sysAuthorityObjectField 权限对象-字段明细
     * @return 结果
     */
    public int changeSysAuthorityObjectField(SysAuthorityObjectField sysAuthorityObjectField);

    /**
     * 批量删除权限对象-字段明细
     *
     * @param authorityObjectFieldSids 需要删除的权限对象-字段明细ID
     * @return 结果
     */
    public int deleteSysAuthorityObjectFieldByIds(List<Long> authorityObjectFieldSids);

}

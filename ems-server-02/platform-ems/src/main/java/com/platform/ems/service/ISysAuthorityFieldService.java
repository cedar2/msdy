package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SysAuthorityField;

import java.util.List;

/**
 * 权限字段Service接口
 *
 * @author linxq
 * @date 2023-01-12
 */
public interface ISysAuthorityFieldService extends IService<SysAuthorityField>, HandleStatusInfoService {
    /**
     * 查询权限字段
     *
     * @param authorityFieldSid 权限字段ID
     * @return 权限字段
     */
    SysAuthorityField selectSysAuthorityFieldById(Long authorityFieldSid);

    /**
     * 查询权限字段列表
     *
     * @param sysAuthorityField 权限字段
     * @return 权限字段集合
     */
    List<SysAuthorityField> selectSysAuthorityFieldList(SysAuthorityField sysAuthorityField);

    /**
     * 新增权限字段
     *
     * @param sysAuthorityField 权限字段
     * @return 结果
     */
    int insertSysAuthorityField(SysAuthorityField sysAuthorityField);

    /**
     * 修改权限字段
     *
     * @param sysAuthorityField 权限字段
     * @return 结果
     */
    int updateSysAuthorityField(SysAuthorityField sysAuthorityField);

    /**
     * 变更权限字段
     *
     * @param sysAuthorityField 权限字段
     * @return 结果
     */
    int changeSysAuthorityField(SysAuthorityField sysAuthorityField);

    /**
     * 批量删除权限字段
     *
     * @param authorityFieldSids 需要删除的权限字段ID
     * @return 结果
     */
    int deleteSysAuthorityFieldByIds(List<Long> authorityFieldSids);

    /**
     * 启用/停用
     *
     * @param sysAuthorityField
     * @return
     */
    int changeStatus(SysAuthorityField sysAuthorityField);

    /**
     * 更改确认状态
     *
     * @param sysAuthorityField
     * @return
     */
    int check(SysAuthorityField sysAuthorityField);

}

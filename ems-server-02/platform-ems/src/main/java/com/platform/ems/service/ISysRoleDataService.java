package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SysRoleData;

/**
 * 数据角色Service接口
 *
 * @author chenkw
 * @date 2023-05-16
 */
public interface ISysRoleDataService extends IService<SysRoleData> {
    /**
     * 查询数据角色
     *
     * @param roleDataSid 数据角色ID
     * @return 数据角色
     */
    public SysRoleData selectSysRoleDataById(Long roleDataSid);

    /**
     * 查询数据角色列表
     *
     * @param sysRoleData 数据角色
     * @return 数据角色集合
     */
    public List<SysRoleData> selectSysRoleDataList(SysRoleData sysRoleData);

    /**
     * 新增数据角色
     *
     * @param sysRoleData 数据角色
     * @return 结果
     */
    public int insertSysRoleData(SysRoleData sysRoleData);

    /**
     * 修改数据角色
     *
     * @param sysRoleData 数据角色
     * @return 结果
     */
    public int updateSysRoleData(SysRoleData sysRoleData);

    /**
     * 变更数据角色
     *
     * @param sysRoleData 数据角色
     * @return 结果
     */
    public int changeSysRoleData(SysRoleData sysRoleData);

    /**
     * 批量删除数据角色
     *
     * @param roleDataSids 需要删除的数据角色ID
     * @return 结果
     */
    public int deleteSysRoleDataByIds(List<Long> roleDataSids);

    /**
     * 启用/停用
     *
     * @param sysRoleData
     * @return
     */
    int changeStatus(SysRoleData sysRoleData);

    /**
     * 更改确认状态
     *
     * @param sysRoleData
     * @return
     */
    int check(SysRoleData sysRoleData);

}

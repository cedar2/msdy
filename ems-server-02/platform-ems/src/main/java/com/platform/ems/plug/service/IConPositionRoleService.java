package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConPositionRole;

/**
 * 工作角色Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConPositionRoleService extends IService<ConPositionRole>{
    /**
     * 查询工作角色
     * 
     * @param sid 工作角色ID
     * @return 工作角色
     */
    public ConPositionRole selectConPositionRoleById(Long sid);

    /**
     * 查询工作角色列表
     * 
     * @param conPositionRole 工作角色
     * @return 工作角色集合
     */
    public List<ConPositionRole> selectConPositionRoleList(ConPositionRole conPositionRole);

    /**
     * 新增工作角色
     * 
     * @param conPositionRole 工作角色
     * @return 结果
     */
    public int insertConPositionRole(ConPositionRole conPositionRole);

    /**
     * 修改工作角色
     * 
     * @param conPositionRole 工作角色
     * @return 结果
     */
    public int updateConPositionRole(ConPositionRole conPositionRole);

    /**
     * 变更工作角色
     *
     * @param conPositionRole 工作角色
     * @return 结果
     */
    public int changeConPositionRole(ConPositionRole conPositionRole);

    /**
     * 批量删除工作角色
     * 
     * @param sids 需要删除的工作角色ID
     * @return 结果
     */
    public int deleteConPositionRoleByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conPositionRole
    * @return
    */
    int changeStatus(ConPositionRole conPositionRole);

    /**
     * 更改确认状态
     * @param conPositionRole
     * @return
     */
    int check(ConPositionRole conPositionRole);

}

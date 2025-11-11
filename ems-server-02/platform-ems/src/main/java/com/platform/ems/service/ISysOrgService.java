package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.entity.SysOrg;

/**
 * 组织架构信息Service接口
 *
 * @author qhq
 * @date 2021-03-18
 */
public interface ISysOrgService extends IService<SysOrg>{
    /**
     * 查询组织架构信息
     *
     * @param nodeSid 组织架构信息ID
     * @return 组织架构信息
     */
    public SysOrg selectSysOrgById(Long nodeSid);

    /**
     * 查询组织架构信息列表
     *
     * @param sysOrg 组织架构信息
     * @return 组织架构信息集合
     */
    public List<SysOrg> selectSysOrgList(SysOrg sysOrg);

    /**
     * 新增组织架构信息
     *
     * @param sysOrg 组织架构信息
     * @return 结果
     */
    public int insertSysOrg(SysOrg sysOrg);

    /**
     * 修改组织架构信息
     *
     * @param sysOrg 组织架构信息
     * @return 结果
     */
    public int updateSysOrg(SysOrg sysOrg);

    /**
     * 变更组织架构信息
     *
     * @param sysOrg 组织架构信息
     * @return 结果
     */
    public int changeSysOrg(SysOrg sysOrg);

    /**
     * 批量删除组织架构信息
     *
     * @param nodeSids 需要删除的组织架构信息ID
     * @return 结果
     */
    public int deleteSysOrgByIds(List<Long> nodeSids);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param sysOrgList 组织列表
     * @return 下拉树结构列表
     */
    //public List<TreeSelect> buildTreeSelect(List<SysOrg> organizationInforList);

    public List<SysOrg> buildTreeSelect(List<SysOrg> sysOrgList);

    /**
     * 提示员工在其他地方已存在是否继续创建
     * @param sysOrg
     * @return
     */
    boolean checkStaff(SysOrg sysOrg);
}

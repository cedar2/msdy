package com.platform.flowable.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.flowable.domain.SysForm;
import com.platform.system.domain.SysDeployForm;

import java.util.List;

/**
 * 单与流程实例关联Service接口
 *
 * @author qhq
 * @date 2021-09-03
 */
public interface ISysDeployFormService extends IService<SysDeployForm> {
    /**
     * 查询单与流程实例关联
     *
     * @param formProcessRelatSid 单与流程实例关联ID
     * @return 单与流程实例关联
     */
    public SysDeployForm selectSysDeployFormById(Long formProcessRelatSid);

    /**
     * 查询单与流程实例关联列表
     *
     * @param sysDeployForm 单与流程实例关联
     * @return 单与流程实例关联集合
     */
    public List<SysDeployForm> selectSysDeployFormList(SysDeployForm sysDeployForm);

    /**
     * 新增单与流程实例关联
     *
     * @param sysDeployForm 单与流程实例关联
     * @return 结果
     */
    public int insertSysDeployForm(SysDeployForm sysDeployForm);

    /**
     * 修改单与流程实例关联
     *
     * @param sysDeployForm 单与流程实例关联
     * @return 结果
     */
    public int updateSysDeployForm(SysDeployForm sysDeployForm);

    /**
     * 变更单与流程实例关联
     *
     * @param sysDeployForm 单与流程实例关联
     * @return 结果
     */
    public int changeSysDeployForm(SysDeployForm sysDeployForm);
}

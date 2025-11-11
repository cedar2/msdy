package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.system.domain.SysProcessTaskConfig;

import java.util.List;

/**
 * 流程任务节点个性化配置参数Service接口
 *
 * @author qhq
 * @date 2021-10-11
 */
public interface ISysProcessTaskConfigService extends IService<SysProcessTaskConfig>{
    /**
     * 查询流程任务节点个性化配置参数
     *
     * @param id 流程任务节点个性化配置参数ID
     * @return 流程任务节点个性化配置参数
     */
    public SysProcessTaskConfig selectSysProcessTaskConfigById (Long id);

    /**
     * 查询流程任务节点个性化配置参数列表
     *
     * @param sysProcessTaskConfig 流程任务节点个性化配置参数
     * @return 流程任务节点个性化配置参数集合
     */
    public List<SysProcessTaskConfig> selectSysProcessTaskConfigList (SysProcessTaskConfig sysProcessTaskConfig);

    /**
     * 新增流程任务节点个性化配置参数
     *
     * @param sysProcessTaskConfig 流程任务节点个性化配置参数
     * @return 结果
     */
    public int insertSysProcessTaskConfig (SysProcessTaskConfig sysProcessTaskConfig);

    /**
     * 修改流程任务节点个性化配置参数
     *
     * @param sysProcessTaskConfig 流程任务节点个性化配置参数
     * @return 结果
     */
    public int updateSysProcessTaskConfig (SysProcessTaskConfig sysProcessTaskConfig);

    /**
     * 变更流程任务节点个性化配置参数
     *
     * @param sysProcessTaskConfig 流程任务节点个性化配置参数
     * @return 结果
     */
    public int changeSysProcessTaskConfig (SysProcessTaskConfig sysProcessTaskConfig);

    /**
     * 批量删除流程任务节点个性化配置参数
     *
     * @param ids 需要删除的流程任务节点个性化配置参数ID
     * @return 结果
     */
    public int deleteSysProcessTaskConfigByIds (List<Long> ids);

    /**
    * 启用/停用
    * @param sysProcessTaskConfig
    * @return
    */
    int changeStatus (SysProcessTaskConfig sysProcessTaskConfig);

    /**
     * 更改确认状态
     * @param sysProcessTaskConfig
     * @return
     */
    int check (SysProcessTaskConfig sysProcessTaskConfig);

}

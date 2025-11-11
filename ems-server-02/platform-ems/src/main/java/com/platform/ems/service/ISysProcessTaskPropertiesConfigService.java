package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.system.domain.SysProcessTaskPropertiesConfig;

import java.util.List;

/**
 * 流程节点属性配置Service接口
 *
 * @author qhq
 * @date 2021-10-11
 */
public interface ISysProcessTaskPropertiesConfigService extends IService<SysProcessTaskPropertiesConfig>{
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public SysProcessTaskPropertiesConfig selectSysProcessTaskPropertiesConfigById (Long id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param sysProcessTaskPropertiesConfig 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<SysProcessTaskPropertiesConfig> selectSysProcessTaskPropertiesConfigList (SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig);

    /**
     * 新增【请填写功能名称】
     *
     * @param sysProcessTaskPropertiesConfig 【请填写功能名称】
     * @return 结果
     */
    public int insertSysProcessTaskPropertiesConfig (SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig);

    /**
     * 修改【请填写功能名称】
     *
     * @param sysProcessTaskPropertiesConfig 【请填写功能名称】
     * @return 结果
     */
    public int updateSysProcessTaskPropertiesConfig (SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig);

    /**
     * 变更【请填写功能名称】
     *
     * @param sysProcessTaskPropertiesConfig 【请填写功能名称】
     * @return 结果
     */
    public int changeSysProcessTaskPropertiesConfig (SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteSysProcessTaskPropertiesConfigByIds (List<Long> ids);

    /**
    * 启用/停用
    * @param sysProcessTaskPropertiesConfig
    * @return
    */
    int changeStatus (SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig);

}

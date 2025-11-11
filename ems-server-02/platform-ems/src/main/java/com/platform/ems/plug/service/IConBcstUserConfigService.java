package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBcstUserConfig;

import java.util.List;

/**
 * 通知用户配置Service接口
 *
 * @author linhongwei
 * @date 2021-10-12
 */
public interface IConBcstUserConfigService extends IService<ConBcstUserConfig> {
    /**
     * 查询通知用户配置
     *
     * @param sid 通知用户配置ID
     * @return 通知用户配置
     */
    public ConBcstUserConfig selectConBcstUserConfigById(Long sid);

    /**
     * 查询通知用户配置列表
     *
     * @param conBcstUserConfig 通知用户配置
     * @return 通知用户配置集合
     */
    public List<ConBcstUserConfig> selectConBcstUserConfigList(ConBcstUserConfig conBcstUserConfig);

    /**
     * 新增通知用户配置
     *
     * @param conBcstUserConfig 通知用户配置
     * @return 结果
     */
    public int insertConBcstUserConfig(ConBcstUserConfig conBcstUserConfig);

    /**
     * 修改通知用户配置
     *
     * @param conBcstUserConfig 通知用户配置
     * @return 结果
     */
    public int updateConBcstUserConfig(ConBcstUserConfig conBcstUserConfig);

    /**
     * 变更通知用户配置
     *
     * @param conBcstUserConfig 通知用户配置
     * @return 结果
     */
    public int changeConBcstUserConfig(ConBcstUserConfig conBcstUserConfig);

    /**
     * 批量删除通知用户配置
     *
     * @param sids 需要删除的通知用户配置ID
     * @return 结果
     */
    public int deleteConBcstUserConfigByIds(List<Long> sids);

    /**
     * 更改确认状态
     *
     * @param conBcstUserConfig
     * @return
     */
    int check(ConBcstUserConfig conBcstUserConfig);

}

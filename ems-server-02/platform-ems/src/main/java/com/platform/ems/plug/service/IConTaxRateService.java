package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConTaxRate;

/**
 * 税率配置Service接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConTaxRateService extends IService<ConTaxRate>{
    /**
     * 查询税率配置
     *
     * @param taxRateSid 税率配置ID
     * @return 税率配置
     */
    public ConTaxRate selectConTaxRateById(Long taxRateSid);

    /**
     * 查询税率配置列表
     *
     * @param conTaxRate 税率配置
     * @return 税率配置集合
     */
    public List<ConTaxRate> selectConTaxRateList(ConTaxRate conTaxRate);

    /**
     * 新增税率配置
     *
     * @param conTaxRate 税率配置
     * @return 结果
     */
    public int insertConTaxRate(ConTaxRate conTaxRate);

    /**
     * 修改税率配置
     *
     * @param conTaxRate 税率配置
     * @return 结果
     */
    public int updateConTaxRate(ConTaxRate conTaxRate);

    /**
     * 变更税率配置
     *
     * @param conTaxRate 税率配置
     * @return 结果
     */
    public int changeConTaxRate(ConTaxRate conTaxRate);

    /**
     * 批量删除税率配置
     *
     * @param taxRateSids 需要删除的税率配置ID
     * @return 结果
     */
    public int deleteConTaxRateByIds(List<Long> taxRateSids);

    /**
    * 启用/停用
    * @param conTaxRate
    * @return
    */
    int changeStatus(ConTaxRate conTaxRate);

    /**
     * 更改确认状态
     * @param conTaxRate
     * @return
     */
    int check(ConTaxRate conTaxRate);

    /**  获取下拉列表 */
    List<ConTaxRate> getConTaxRateList();

    List<ConTaxRate> getList(ConTaxRate conTaxRate);
}

package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ConBarcodeRangeConfig;

/**
 * 物料/商品/服务条码号码段配置Service接口
 * 
 * @author linhongwei
 * @date 2021-06-03
 */
public interface IConBarcodeRangeConfigService extends IService<ConBarcodeRangeConfig>{
    /**
     * 查询物料/商品/服务条码号码段配置
     * 
     * @param rangeConfigSid 物料/商品/服务条码号码段配置ID
     * @return 物料/商品/服务条码号码段配置
     */
    public ConBarcodeRangeConfig selectConBarcodeRangeConfigById(Long rangeConfigSid);

    public Long nextId();
    /**
     * 查询物料/商品/服务条码号码段配置列表
     * 
     * @param conBarcodeRangeConfig 物料/商品/服务条码号码段配置
     * @return 物料/商品/服务条码号码段配置集合
     */
    public List<ConBarcodeRangeConfig> selectConBarcodeRangeConfigList(ConBarcodeRangeConfig conBarcodeRangeConfig);

    /**
     * 新增物料/商品/服务条码号码段配置
     * 
     * @param conBarcodeRangeConfig 物料/商品/服务条码号码段配置
     * @return 结果
     */
    public int insertConBarcodeRangeConfig(ConBarcodeRangeConfig conBarcodeRangeConfig);

    /**
     * 修改物料/商品/服务条码号码段配置
     * 
     * @param conBarcodeRangeConfig 物料/商品/服务条码号码段配置
     * @return 结果
     */
    public int updateConBarcodeRangeConfig(ConBarcodeRangeConfig conBarcodeRangeConfig);

    /**
     * 变更物料/商品/服务条码号码段配置
     *
     * @param conBarcodeRangeConfig 物料/商品/服务条码号码段配置
     * @return 结果
     */
    public int changeConBarcodeRangeConfig(ConBarcodeRangeConfig conBarcodeRangeConfig);

    /**
     * 批量删除物料/商品/服务条码号码段配置
     * 
     * @param rangeConfigSids 需要删除的物料/商品/服务条码号码段配置ID
     * @return 结果
     */
    public int deleteConBarcodeRangeConfigByIds(List<Long> rangeConfigSids);

    /**
    * 启用/停用
    * @param conBarcodeRangeConfig
    * @return
    */
    int changeStatus(ConBarcodeRangeConfig conBarcodeRangeConfig);

    /**
     * 更改确认状态
     * @param conBarcodeRangeConfig
     * @return
     */
    int check(ConBarcodeRangeConfig conBarcodeRangeConfig);

}

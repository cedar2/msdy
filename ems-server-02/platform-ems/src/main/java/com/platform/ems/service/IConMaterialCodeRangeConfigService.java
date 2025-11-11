package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ConMaterialCodeRangeConfig;
import org.redisson.liveobject.resolver.LongGenerator;

/**
 * 物料/商品/服务编码号码段配置Service接口
 * 
 * @author linhongwei
 * @date 2021-06-03
 */
public interface IConMaterialCodeRangeConfigService extends IService<ConMaterialCodeRangeConfig>{
    /**
     * 查询物料/商品/服务编码号码段配置
     * 
     * @param rangeConfigSid 物料/商品/服务编码号码段配置ID
     * @return 物料/商品/服务编码号码段配置
     */
    public ConMaterialCodeRangeConfig selectConMaterialCodeRangeConfigById(Long rangeConfigSid);

    public Long nextId(String param);
    /**
     * 查询当前编码
     * @param category 物料类别
     * @return
     */
    public ConMaterialCodeRangeConfig getNext(String category);


    /**
     * 查询物料/商品/服务编码号码段配置列表
     * 
     * @param conMaterialCodeRangeConfig 物料/商品/服务编码号码段配置
     * @return 物料/商品/服务编码号码段配置集合
     */
    public List<ConMaterialCodeRangeConfig> selectConMaterialCodeRangeConfigList(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig);

    /**
     * 新增物料/商品/服务编码号码段配置
     * 
     * @param conMaterialCodeRangeConfig 物料/商品/服务编码号码段配置
     * @return 结果
     */
    public int insertConMaterialCodeRangeConfig(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig);

    /**
     * 修改物料/商品/服务编码号码段配置
     * 
     * @param conMaterialCodeRangeConfig 物料/商品/服务编码号码段配置
     * @return 结果
     */
    public int updateConMaterialCodeRangeConfig(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig);

    /**
     * 变更物料/商品/服务编码号码段配置
     *
     * @param conMaterialCodeRangeConfig 物料/商品/服务编码号码段配置
     * @return 结果
     */
    public int changeConMaterialCodeRangeConfig(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig);

    /**
     * 批量删除物料/商品/服务编码号码段配置
     * 
     * @param rangeConfigSids 需要删除的物料/商品/服务编码号码段配置ID
     * @return 结果
     */
    public int deleteConMaterialCodeRangeConfigByIds(List<Long> rangeConfigSids);

    /**
    * 启用/停用
    * @param conMaterialCodeRangeConfig
    * @return
    */
    int changeStatus(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig);

    /**
     * 更改确认状态
     * @param conMaterialCodeRangeConfig
     * @return
     */
    int check(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig);

}

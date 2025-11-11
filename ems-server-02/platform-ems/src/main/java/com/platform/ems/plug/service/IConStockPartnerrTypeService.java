package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConStockPartnerrType;

/**
 * 类型_库存合作伙伴Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConStockPartnerrTypeService extends IService<ConStockPartnerrType>{
    /**
     * 查询类型_库存合作伙伴
     * 
     * @param sid 类型_库存合作伙伴ID
     * @return 类型_库存合作伙伴
     */
    public ConStockPartnerrType selectConStockPartnerrTypeById(Long sid);

    /**
     * 查询类型_库存合作伙伴列表
     * 
     * @param conStockPartnerrType 类型_库存合作伙伴
     * @return 类型_库存合作伙伴集合
     */
    public List<ConStockPartnerrType> selectConStockPartnerrTypeList(ConStockPartnerrType conStockPartnerrType);

    /**
     * 新增类型_库存合作伙伴
     * 
     * @param conStockPartnerrType 类型_库存合作伙伴
     * @return 结果
     */
    public int insertConStockPartnerrType(ConStockPartnerrType conStockPartnerrType);

    /**
     * 修改类型_库存合作伙伴
     * 
     * @param conStockPartnerrType 类型_库存合作伙伴
     * @return 结果
     */
    public int updateConStockPartnerrType(ConStockPartnerrType conStockPartnerrType);

    /**
     * 变更类型_库存合作伙伴
     *
     * @param conStockPartnerrType 类型_库存合作伙伴
     * @return 结果
     */
    public int changeConStockPartnerrType(ConStockPartnerrType conStockPartnerrType);

    /**
     * 批量删除类型_库存合作伙伴
     * 
     * @param sids 需要删除的类型_库存合作伙伴ID
     * @return 结果
     */
    public int deleteConStockPartnerrTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conStockPartnerrType
    * @return
    */
    int changeStatus(ConStockPartnerrType conStockPartnerrType);

    /**
     * 更改确认状态
     * @param conStockPartnerrType
     * @return
     */
    int check(ConStockPartnerrType conStockPartnerrType);

}

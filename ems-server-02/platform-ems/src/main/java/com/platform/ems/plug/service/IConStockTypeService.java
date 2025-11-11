package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConStockType;

/**
 * 库存类型Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConStockTypeService extends IService<ConStockType>{
    /**
     * 查询库存类型
     * 
     * @param sid 库存类型ID
     * @return 库存类型
     */
    public ConStockType selectConStockTypeById(Long sid);

    /**
     * 查询库存类型列表
     * 
     * @param conStockType 库存类型
     * @return 库存类型集合
     */
    public List<ConStockType> selectConStockTypeList(ConStockType conStockType);

    /**
     * 新增库存类型
     * 
     * @param conStockType 库存类型
     * @return 结果
     */
    public int insertConStockType(ConStockType conStockType);

    /**
     * 修改库存类型
     * 
     * @param conStockType 库存类型
     * @return 结果
     */
    public int updateConStockType(ConStockType conStockType);

    /**
     * 变更库存类型
     *
     * @param conStockType 库存类型
     * @return 结果
     */
    public int changeConStockType(ConStockType conStockType);

    /**
     * 批量删除库存类型
     * 
     * @param sids 需要删除的库存类型ID
     * @return 结果
     */
    public int deleteConStockTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conStockType
    * @return
    */
    int changeStatus(ConStockType conStockType);

    /**
     * 更改确认状态
     * @param conStockType
     * @return
     */
    int check(ConStockType conStockType);

}

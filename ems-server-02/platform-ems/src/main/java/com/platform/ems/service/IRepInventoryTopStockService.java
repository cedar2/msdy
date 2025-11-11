package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepInventoryTopStock;

/**
 * T100库存Service接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface IRepInventoryTopStockService extends IService<RepInventoryTopStock> {
    /**
     * 查询T100库存
     *
     * @param dataRecordSid T100库存ID
     * @return T100库存
     */
    public RepInventoryTopStock selectRepInventoryTopStockById(Long dataRecordSid);

    /**
     * 查询T100库存列表
     *
     * @param repInventoryTopStock T100库存
     * @return T100库存集合
     */
    public List<RepInventoryTopStock> selectRepInventoryTopStockList(RepInventoryTopStock repInventoryTopStock);

    /**
     * 新增T100库存
     *
     * @param repInventoryTopStock T100库存
     * @return 结果
     */
    public int insertRepInventoryTopStock(RepInventoryTopStock repInventoryTopStock);

    /**
     * 批量删除T100库存
     *
     * @param dataRecordSids 需要删除的T100库存ID
     * @return 结果
     */
    public int deleteRepInventoryTopStockByIds(List<Long> dataRecordSids);

}

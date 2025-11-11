package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepInventoryTopStockMapper;
import com.platform.ems.domain.RepInventoryTopStock;
import com.platform.ems.service.IRepInventoryTopStockService;

/**
 * T100库存Service业务层处理
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepInventoryTopStockServiceImpl extends ServiceImpl<RepInventoryTopStockMapper, RepInventoryTopStock> implements IRepInventoryTopStockService {
    @Autowired
    private RepInventoryTopStockMapper repInventoryTopStockMapper;

    /**
     * 查询T100库存
     *
     * @param dataRecordSid T100库存ID
     * @return T100库存
     */
    @Override
    public RepInventoryTopStock selectRepInventoryTopStockById(Long dataRecordSid) {
        RepInventoryTopStock repInventoryTopStock = repInventoryTopStockMapper.selectRepInventoryTopStockById(dataRecordSid);
        return repInventoryTopStock;
    }

    /**
     * 查询T100库存列表
     *
     * @param repInventoryTopStock T100库存
     * @return T100库存
     */
    @Override
    public List<RepInventoryTopStock> selectRepInventoryTopStockList(RepInventoryTopStock repInventoryTopStock) {
        return repInventoryTopStockMapper.selectRepInventoryTopStockList(repInventoryTopStock);
    }

    /**
     * 新增T100库存
     * 需要注意编码重复校验
     *
     * @param repInventoryTopStock T100库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepInventoryTopStock(RepInventoryTopStock repInventoryTopStock) {
        int row = repInventoryTopStockMapper.insert(repInventoryTopStock);
        return row;
    }

    /**
     * 批量删除T100库存
     *
     * @param dataRecordSids 需要删除的T100库存ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepInventoryTopStockByIds(List<Long> dataRecordSids) {
        return repInventoryTopStockMapper.deleteBatchIds(dataRecordSids);
    }

}

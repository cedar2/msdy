package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.InvInventoryAdjustItemMapper;
import com.platform.ems.domain.InvInventoryAdjustItem;
import com.platform.ems.service.IInvInventoryAdjustItemService;

/**
 * 库存调整单-明细Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-19
 */
@Service
@SuppressWarnings("all")
public class InvInventoryAdjustItemServiceImpl extends ServiceImpl<InvInventoryAdjustItemMapper,InvInventoryAdjustItem>  implements IInvInventoryAdjustItemService {
    @Autowired
    private InvInventoryAdjustItemMapper invInventoryAdjustItemMapper;

    /**
     * 查询库存调整单-明细
     * 
     * @param inventoryAdjustItemSid 库存调整单-明细ID
     * @return 库存调整单-明细
     */
    @Override
    public List<InvInventoryAdjustItem> selectInvInventoryAdjustItemById(Long inventoryAdjustItemSid) {
        return invInventoryAdjustItemMapper.selectInvInventoryAdjustItemById(inventoryAdjustItemSid);
    }

    /**
     * 查询库存调整单-明细列表
     * 
     * @param invInventoryAdjustItem 库存调整单-明细
     * @return 库存调整单-明细
     */
    @Override
    public List<InvInventoryAdjustItem> selectInvInventoryAdjustItemList(InvInventoryAdjustItem invInventoryAdjustItem) {
        return invInventoryAdjustItemMapper.selectInvInventoryAdjustItemList(invInventoryAdjustItem);
    }

    /**
     * 新增库存调整单-明细
     * 需要注意编码重复校验
     * @param invInventoryAdjustItem 库存调整单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvInventoryAdjustItem(InvInventoryAdjustItem invInventoryAdjustItem) {
        return invInventoryAdjustItemMapper.insert(invInventoryAdjustItem);
    }

    /**
     * 修改库存调整单-明细
     * 
     * @param invInventoryAdjustItem 库存调整单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvInventoryAdjustItem(InvInventoryAdjustItem invInventoryAdjustItem) {
        return invInventoryAdjustItemMapper.updateById(invInventoryAdjustItem);
    }

    /**
     * 批量删除库存调整单-明细
     * 
     * @param inventoryAdjustItemSids 需要删除的库存调整单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvInventoryAdjustItemByIds(List<Long> inventoryAdjustItemSids) {
        return invInventoryAdjustItemMapper.deleteBatchIds(inventoryAdjustItemSids);
    }


}

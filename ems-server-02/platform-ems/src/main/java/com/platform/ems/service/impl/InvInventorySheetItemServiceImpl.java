package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.InvInventorySheetItemMapper;
import com.platform.ems.domain.InvInventorySheetItem;
import com.platform.ems.service.IInvInventorySheetItemService;

/**
 * 盘点单-明细Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
@Service
@SuppressWarnings("all")
public class InvInventorySheetItemServiceImpl extends ServiceImpl<InvInventorySheetItemMapper,InvInventorySheetItem>  implements IInvInventorySheetItemService {
    @Autowired
    private InvInventorySheetItemMapper invInventorySheetItemMapper;

    /**
     * 查询盘点单-明细
     * 
     * @param inventorySheetItemSid 盘点单-明细ID
     * @return 盘点单-明细
     */
    @Override
    public List<InvInventorySheetItem> selectInvInventorySheetItemById(Long inventorySheetItemSid) {
        return invInventorySheetItemMapper.selectInvInventorySheetItemById(inventorySheetItemSid);
    }

    /**
     * 查询盘点单-明细列表
     * 
     * @param invInventorySheetItem 盘点单-明细
     * @return 盘点单-明细
     */
    @Override
    public List<InvInventorySheetItem> selectInvInventorySheetItemList(InvInventorySheetItem invInventorySheetItem) {
        return invInventorySheetItemMapper.selectInvInventorySheetItemList(invInventorySheetItem);
    }

    /**
     * 新增盘点单-明细
     * 需要注意编码重复校验
     * @param invInventorySheetItem 盘点单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvInventorySheetItem(InvInventorySheetItem invInventorySheetItem) {
        return invInventorySheetItemMapper.insert(invInventorySheetItem);
    }

    /**
     * 修改盘点单-明细
     * 
     * @param invInventorySheetItem 盘点单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvInventorySheetItem(InvInventorySheetItem invInventorySheetItem) {
        return invInventorySheetItemMapper.updateById(invInventorySheetItem);
    }

    /**
     * 批量删除盘点单-明细
     * 
     * @param inventorySheetItemSids 需要删除的盘点单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvInventorySheetItemByIds(List<Long> inventorySheetItemSids) {
        return invInventorySheetItemMapper.deleteBatchIds(inventorySheetItemSids);
    }


}

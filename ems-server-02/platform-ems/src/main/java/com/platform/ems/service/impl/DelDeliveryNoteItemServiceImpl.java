package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.InvInventoryDocumentItem;
import com.platform.ems.enums.BusinessType;
import com.platform.ems.enums.DocCategory;
import com.platform.ems.mapper.InvInventoryDocumentItemMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.DelDeliveryNoteItemMapper;
import com.platform.ems.domain.DelDeliveryNoteItem;
import com.platform.ems.service.IDelDeliveryNoteItemService;

/**
 * 交货单-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-21
 */
@Service
@SuppressWarnings("all")
public class DelDeliveryNoteItemServiceImpl extends ServiceImpl<DelDeliveryNoteItemMapper,DelDeliveryNoteItem>  implements IDelDeliveryNoteItemService {
    @Autowired
    private DelDeliveryNoteItemMapper delDeliveryNoteItemMapper;
    @Autowired
    private InvInventoryDocumentItemMapper invInventoryDocumentItemMapper;

    /**
     * 查询交货单-明细
     *
     * @param clientId 交货单-明细ID
     * @return 交货单-明细
     */
    @Override
    public DelDeliveryNoteItem selectDelDeliveryNoteItemById(String clientId) {
        return delDeliveryNoteItemMapper.selectDelDeliveryNoteItemById(clientId);
    }

    /**
     * 查询交货单-明细列表
     *
     * @param delDeliveryNoteItem 交货单-明细
     * @return 交货单-明细
     */
    @Override
    public List<DelDeliveryNoteItem> selectDelDeliveryNoteItemList(DelDeliveryNoteItem delDeliveryNoteItem) {
        /*List<DelDeliveryNoteItem> list = new ArrayList<>();
        //采购交货明细
        if (BusinessType.DELIVERY.getCode().equals(delDeliveryNoteItem.getDeliveryType())){
            List<DelDeliveryNoteItem> delDeliveryNoteItemList = delDeliveryNoteItemMapper.selectShipmentsNoteItemList(delDeliveryNoteItem);
            list.addAll(delDeliveryNoteItemList);
        }
        //销售发货明细
        if (BusinessType.SHIPMENTS.getCode().equals(delDeliveryNoteItem.getDeliveryType())){
            List<DelDeliveryNoteItem> delDeliveryNoteItemList = delDeliveryNoteItemMapper.selectDelDeliveryNoteItemList(delDeliveryNoteItem);
            list.addAll(delDeliveryNoteItemList);
        }*/
        return delDeliveryNoteItemMapper.selectDelDeliveryNoteItemList(delDeliveryNoteItem);
    }

    /**
     * 新增交货单-明细
     * 需要注意编码重复校验
     * @param delDeliveryNoteItem 交货单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDelDeliveryNoteItem(DelDeliveryNoteItem delDeliveryNoteItem) {
        return delDeliveryNoteItemMapper.insert(delDeliveryNoteItem);
    }

    /**
     * 修改交货单-明细
     *
     * @param delDeliveryNoteItem 交货单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDelDeliveryNoteItem(DelDeliveryNoteItem delDeliveryNoteItem) {
        return delDeliveryNoteItemMapper.updateById(delDeliveryNoteItem);
    }

    /**
     * 批量删除交货单-明细
     *
     * @param clientIds 需要删除的交货单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDelDeliveryNoteItemByIds(List<String> clientIds) {
        return delDeliveryNoteItemMapper.deleteBatchIds(clientIds);
    }

    /**
     * 采购交货单明细报表
     */
    @Override
    public List<DelDeliveryNoteItem> getDeliveryItemList(DelDeliveryNoteItem delDeliveryNoteItem) {
        return delDeliveryNoteItemMapper.getDeliveryItemList(delDeliveryNoteItem);
    }

    /**
     * 销售发货单明细报表
     */
    @Override
    public List<DelDeliveryNoteItem> getShipmentsItemList(DelDeliveryNoteItem delDeliveryNoteItem) {
        return delDeliveryNoteItemMapper.getShipmentsItemList(delDeliveryNoteItem);
    }

    @Override
    public void handleInoutStatus(List<DelDeliveryNoteItem> list) {
        list.forEach(item->{
            String code = item.getDocumentType();
            BigDecimal inOutStockQuantity = item.getInOutStockQuantity();
            if(inOutStockQuantity!=null){
                if(DocCategory.SALE_RU.getCode().equals(code)||DocCategory.RETURN_BACK_SALE_RECEPIT.getCode().equals(code)){
                    if(inOutStockQuantity.compareTo(BigDecimal.ZERO)==0){
                        item.setInOutStockStatus(code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode())?ConstantsEms.IN_STORE_STATUS_NOT:ConstantsEms.OUT_STORE_STATUS_NOT);
                    }else if(inOutStockQuantity.compareTo(item.getDeliveryQuantity())==-1){
                        item.setInOutStockStatus(code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode())?ConstantsEms.IN_STORE_STATUS_LI:ConstantsEms.OUT_STORE_STATUS_LI);
                    }else{
                        item.setInOutStockStatus(code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode())?ConstantsEms.IN_STORE_STATUS:ConstantsEms.OUT_STORE_STATUS);
                    }
                }else{
                    if(inOutStockQuantity.compareTo(BigDecimal.ZERO)==0){
                        item.setInOutStockStatus(code.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode())?ConstantsEms.OUT_STORE_STATUS_NOT:ConstantsEms.IN_STORE_STATUS_NOT);
                    }else if(inOutStockQuantity.compareTo(item.getDeliveryQuantity())==-1){
                        item.setInOutStockStatus(code.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode())?ConstantsEms.OUT_STORE_STATUS_LI:ConstantsEms.IN_STORE_STATUS_LI);
                    }else{
                        item.setInOutStockStatus(code.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode())?ConstantsEms.OUT_STORE_STATUS:ConstantsEms.IN_STORE_STATUS);
                    }
                }

            }else{
                if(DocCategory.SALE_RU.getCode().equals(code)||DocCategory.RETURN_BACK_SALE_RECEPIT.getCode().equals(code)){
                    item.setInOutStockStatus(code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode())?ConstantsEms.IN_STORE_STATUS_NOT:ConstantsEms.OUT_STORE_STATUS_NOT);
                }else{
                    item.setInOutStockStatus(code.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode())?ConstantsEms.OUT_STORE_STATUS_NOT:ConstantsEms.IN_STORE_STATUS_NOT);
                }
            }
        });
    }

}

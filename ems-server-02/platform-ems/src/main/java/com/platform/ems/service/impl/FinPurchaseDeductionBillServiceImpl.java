package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.FinPurchaseDeductionBill;
import com.platform.ems.domain.FinPurchaseDeductionBillAttachment;
import com.platform.ems.domain.FinPurchaseDeductionBillItem;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.FinPurchaseDeductionBillAttachmentMapper;
import com.platform.ems.mapper.FinPurchaseDeductionBillItemMapper;
import com.platform.ems.mapper.FinPurchaseDeductionBillMapper;
import com.platform.ems.service.IFinPurchaseDeductionBillService;

/**
 * 采购扣款单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-10
 */
@Service
@SuppressWarnings("all")
public class FinPurchaseDeductionBillServiceImpl extends ServiceImpl<FinPurchaseDeductionBillMapper,FinPurchaseDeductionBill>  implements IFinPurchaseDeductionBillService {
    @Autowired
    private FinPurchaseDeductionBillMapper FinPurchaseDeductionBillMapper;

    @Autowired
    private FinPurchaseDeductionBillItemMapper FinPurchaseDeductionBillItemMapper;

    @Autowired
    private FinPurchaseDeductionBillAttachmentMapper FinPurchaseDeductionBillAttachmentMapper;

    /**
     * 查询采购扣款单
     *
     * @param purchaseDeductionSid 采购扣款单ID
     * @return 采购扣款单
     */
    @Override
    public FinPurchaseDeductionBill selectFinPurchaseDeductionById(Long purchaseDeductionSid) {
        FinPurchaseDeductionBill FinPurchaseDeductionBill = FinPurchaseDeductionBillMapper.selectFinPurchaseDeductionById(purchaseDeductionSid);
        if (FinPurchaseDeductionBill == null){
            return null;
        }
        //采购扣款单-明细
        FinPurchaseDeductionBillItem FinPurchaseDeductionBillItem = new FinPurchaseDeductionBillItem();
        FinPurchaseDeductionBillItem.setPurchaseDeductionBillSid(purchaseDeductionSid);
        List<FinPurchaseDeductionBillItem> finPurchaseDeductionItemList = FinPurchaseDeductionBillItemMapper.selectFinPurchaseDeductionItemList(FinPurchaseDeductionBillItem);
        //采购扣款单-附件
        FinPurchaseDeductionBillAttachment FinPurchaseDeductionBillAttachment = new FinPurchaseDeductionBillAttachment();
        FinPurchaseDeductionBillAttachment.setPurchaseDeductionBillSid(purchaseDeductionSid);
        List<FinPurchaseDeductionBillAttachment> finPurchaseDeductionAttachmentList =
                FinPurchaseDeductionBillAttachmentMapper.selectFinPurchaseDeductionAttachmentList(FinPurchaseDeductionBillAttachment);

        FinPurchaseDeductionBill.setFinPurchaseDeductionItemList(finPurchaseDeductionItemList);
        FinPurchaseDeductionBill.setFinPurchaseDeductionAttachmentList(finPurchaseDeductionAttachmentList);
        return FinPurchaseDeductionBill;
    }

    /**
     * 查询采购扣款单列表
     *
     * @param FinPurchaseDeductionBill 采购扣款单
     * @return 采购扣款单
     */
    @Override
    public List<FinPurchaseDeductionBill> selectFinPurchaseDeductionList(FinPurchaseDeductionBill FinPurchaseDeductionBill) {
        return FinPurchaseDeductionBillMapper.selectFinPurchaseDeductionList(FinPurchaseDeductionBill);
    }

    /**
     * 新增采购扣款单
     * 需要注意编码重复校验
     * @param FinPurchaseDeductionBill 采购扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPurchaseDeduction(FinPurchaseDeductionBill FinPurchaseDeductionBill) {
        setConfirmInfo(FinPurchaseDeductionBill);
        FinPurchaseDeductionBillMapper.insert(FinPurchaseDeductionBill);
        //采购扣款单-明细对象
        List<FinPurchaseDeductionBillItem> finPurchaseDeductionItemList = FinPurchaseDeductionBill.getFinPurchaseDeductionItemList();
        if (CollectionUtils.isNotEmpty(finPurchaseDeductionItemList)) {
            addFinPurchaseDeductionItem(FinPurchaseDeductionBill, finPurchaseDeductionItemList);
        }
        //采购扣款单-附件对象
        List<FinPurchaseDeductionBillAttachment> finPurchaseDeductionAttachmentList = FinPurchaseDeductionBill.getFinPurchaseDeductionAttachmentList();
        if (CollectionUtils.isNotEmpty(finPurchaseDeductionAttachmentList)) {
            addFinPurchaseDeductionAttachment(FinPurchaseDeductionBill, finPurchaseDeductionAttachmentList);
        }
        return 1;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(FinPurchaseDeductionBill o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 采购扣款单-明细对象
     */
    private void addFinPurchaseDeductionItem(FinPurchaseDeductionBill FinPurchaseDeductionBill, List<FinPurchaseDeductionBillItem> finPurchaseDeductionItemList) {
        FinPurchaseDeductionBillItemMapper.delete(
                new UpdateWrapper<FinPurchaseDeductionBillItem>()
                        .lambda()
                        .eq(FinPurchaseDeductionBillItem::getPurchaseDeductionBillSid, FinPurchaseDeductionBill.getPurchaseDeductionBillSid())
        );
        finPurchaseDeductionItemList.forEach(o -> {
            o.setPurchaseDeductionBillSid(FinPurchaseDeductionBill.getPurchaseDeductionBillSid());
            FinPurchaseDeductionBillItemMapper.insert(o);
        });
    }

    /**
     * 采购扣款单-附件对象
     */
    private void addFinPurchaseDeductionAttachment(FinPurchaseDeductionBill FinPurchaseDeductionBill, List<FinPurchaseDeductionBillAttachment> finPurchaseDeductionAttachmentList) {
        FinPurchaseDeductionBillAttachmentMapper.delete(
                new UpdateWrapper<FinPurchaseDeductionBillAttachment>()
                        .lambda()
                        .eq(FinPurchaseDeductionBillAttachment::getPurchaseDeductionBillSid, FinPurchaseDeductionBill.getPurchaseDeductionBillSid())
        );
        finPurchaseDeductionAttachmentList.forEach(o -> {
            o.setPurchaseDeductionBillSid(FinPurchaseDeductionBill.getPurchaseDeductionBillSid());
            FinPurchaseDeductionBillAttachmentMapper.insert(o);
        });
    }

    /**
     * 修改采购扣款单
     *
     * @param FinPurchaseDeductionBill 采购扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPurchaseDeduction(FinPurchaseDeductionBill FinPurchaseDeductionBill) {
        setConfirmInfo(FinPurchaseDeductionBill);
        FinPurchaseDeductionBillMapper.updateAllById(FinPurchaseDeductionBill);
        //采购扣款单-明细对象
        List<FinPurchaseDeductionBillItem> finPurchaseDeductionItemList = FinPurchaseDeductionBill.getFinPurchaseDeductionItemList();
        if (CollectionUtils.isNotEmpty(finPurchaseDeductionItemList)) {
            addFinPurchaseDeductionItem(FinPurchaseDeductionBill, finPurchaseDeductionItemList);
        }
        //采购扣款单-附件对象
        List<FinPurchaseDeductionBillAttachment> finPurchaseDeductionAttachmentList = FinPurchaseDeductionBill.getFinPurchaseDeductionAttachmentList();
        if (CollectionUtils.isNotEmpty(finPurchaseDeductionAttachmentList)) {
            addFinPurchaseDeductionAttachment(FinPurchaseDeductionBill, finPurchaseDeductionAttachmentList);
        }
        return 1;
    }

    /**
     * 批量删除采购扣款单
     *
     * @param purchaseDeductionSids 需要删除的采购扣款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPurchaseDeductionByIds(Long[] purchaseDeductionSids) {
        FinPurchaseDeductionBill params = new FinPurchaseDeductionBill();
        params.setPurchaseDeductionBillSids(purchaseDeductionSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = FinPurchaseDeductionBillMapper.countByDomain(params);
        if (count != purchaseDeductionSids.length){
            throw new BaseException("仅保存状态才允许删除");
        }
        //删除采购扣款单
        FinPurchaseDeductionBillMapper.deleteFinPurchaseDeductionByIds(purchaseDeductionSids);
        //删除采购扣款单明细
        FinPurchaseDeductionBillItemMapper.deleteFinPurchaseDeductionItemByIds(purchaseDeductionSids);
        //删除采购扣款单附件
        FinPurchaseDeductionBillAttachmentMapper.deleteFinPurchaseDeductionAttachmentByIds(purchaseDeductionSids);
        return purchaseDeductionSids.length;
    }

    /**
     * 采购扣款单确认
     */
    @Override
    public int confirm(FinPurchaseDeductionBill FinPurchaseDeductionBill) {
        //采购扣款单sids
        Long[] purchaseDeductionSids = FinPurchaseDeductionBill.getPurchaseDeductionBillSids();
        FinPurchaseDeductionBill params = new FinPurchaseDeductionBill();
        params.setPurchaseDeductionBillSids(purchaseDeductionSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = FinPurchaseDeductionBillMapper.countByDomain(params);
        if (count != purchaseDeductionSids.length){
            throw new BaseException("仅保存状态才允许确认");
        }
        FinPurchaseDeductionBill.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        FinPurchaseDeductionBill.setConfirmDate(new Date());
        return FinPurchaseDeductionBillMapper.confirm(FinPurchaseDeductionBill);
    }

    /**
     * 采购扣款单变更
     */
    @Override
    public int change(FinPurchaseDeductionBill FinPurchaseDeductionBill) {
        Long purchaseDeductionSid = FinPurchaseDeductionBill.getPurchaseDeductionBillSid();
        FinPurchaseDeductionBill purchaseDeduction = FinPurchaseDeductionBillMapper.selectFinPurchaseDeductionById(purchaseDeductionSid);
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(purchaseDeduction.getHandleStatus())){
            throw new BaseException("仅确认状态才允许变更");
        }
        FinPurchaseDeductionBillMapper.updateAllById(FinPurchaseDeductionBill);
        //采购扣款单-明细对象
        List<FinPurchaseDeductionBillItem> finPurchaseDeductionItemList = FinPurchaseDeductionBill.getFinPurchaseDeductionItemList();
        if (CollectionUtils.isNotEmpty(finPurchaseDeductionItemList)) {
            addFinPurchaseDeductionItem(FinPurchaseDeductionBill, finPurchaseDeductionItemList);
        }
        //采购扣款单-附件对象
        List<FinPurchaseDeductionBillAttachment> finPurchaseDeductionAttachmentList = FinPurchaseDeductionBill.getFinPurchaseDeductionAttachmentList();
        if (CollectionUtils.isNotEmpty(finPurchaseDeductionAttachmentList)) {
            addFinPurchaseDeductionAttachment(FinPurchaseDeductionBill, finPurchaseDeductionAttachmentList);
        }
        return 1;
    }
}

package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.domain.FinSaleDeductionBill;
import com.platform.ems.domain.FinSaleDeductionBillAttachment;
import com.platform.ems.domain.FinSaleDeductionBillItem;
import com.platform.ems.domain.SalServiceAcceptance;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.FinSaleDeductionBillAttachmentMapper;
import com.platform.ems.mapper.FinSaleDeductionBillItemMapper;
import com.platform.ems.mapper.FinSaleDeductionBillMapper;
import com.platform.ems.service.IFinSaleDeductionBillService;

/**
 * 销售扣款单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-09
 */
@Service
@SuppressWarnings("all")
public class FinSaleDeductionBillServiceImpl extends ServiceImpl<FinSaleDeductionBillMapper,FinSaleDeductionBill>  implements IFinSaleDeductionBillService {
    @Autowired
    private FinSaleDeductionBillMapper FinSaleDeductionBillMapper;

    @Autowired
    private FinSaleDeductionBillItemMapper finSaleDeductionItemMapper;

    @Autowired
    private FinSaleDeductionBillAttachmentMapper finSaleDeductionAttachmentMapper;

    /**
     * 查询销售扣款单
     *
     * @param saleDeductionSid 销售扣款单ID
     * @return 销售扣款单
     */
    @Override
    public FinSaleDeductionBill selectFinSaleDeductionById(Long saleDeductionSid) {
        FinSaleDeductionBill FinSaleDeductionBill = FinSaleDeductionBillMapper.selectFinSaleDeductionById(saleDeductionSid);
        if (FinSaleDeductionBill == null){
            return null;
        }
        //销售扣款单-明细
        FinSaleDeductionBillItem FinSaleDeductionBillItem = new FinSaleDeductionBillItem();
        FinSaleDeductionBillItem.setSaleDeductionBillSid(saleDeductionSid);
        List<FinSaleDeductionBillItem> finSaleDeductionItemList = finSaleDeductionItemMapper.selectFinSaleDeductionItemList(FinSaleDeductionBillItem);
        //销售扣款单-附件
        FinSaleDeductionBillAttachment FinSaleDeductionBillAttachment = new FinSaleDeductionBillAttachment();
        FinSaleDeductionBillAttachment.setSaleDeductionBillSid(saleDeductionSid);
        List<FinSaleDeductionBillAttachment> finSaleDeductionAttachmentList =
                finSaleDeductionAttachmentMapper.selectFinSaleDeductionAttachmentList(FinSaleDeductionBillAttachment);

        FinSaleDeductionBill.setFinSaleDeductionItemList(finSaleDeductionItemList);
        FinSaleDeductionBill.setFinSaleDeductionAttachmentList(finSaleDeductionAttachmentList);
        return FinSaleDeductionBill;
    }

    /**
     * 查询销售扣款单列表
     *
     * @param FinSaleDeductionBill 销售扣款单
     * @return 销售扣款单
     */
    @Override
    public List<FinSaleDeductionBill> selectFinSaleDeductionList(FinSaleDeductionBill FinSaleDeductionBill) {
        return FinSaleDeductionBillMapper.selectFinSaleDeductionList(FinSaleDeductionBill);
    }

    /**
     * 新增销售扣款单
     * 需要注意编码重复校验
     * @param FinSaleDeductionBill 销售扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinSaleDeduction(FinSaleDeductionBill FinSaleDeductionBill) {
        setConfirmInfo(FinSaleDeductionBill);
        FinSaleDeductionBillMapper.insert(FinSaleDeductionBill);
        //销售扣款单-明细对象
        List<FinSaleDeductionBillItem> finSaleDeductionItemList = FinSaleDeductionBill.getFinSaleDeductionItemList();
        if (CollectionUtils.isNotEmpty(finSaleDeductionItemList)) {
            addFinSaleDeductionItem(FinSaleDeductionBill, finSaleDeductionItemList);
        }
        //销售扣款单-附件对象
        List<FinSaleDeductionBillAttachment> finSaleDeductionAttachmentList = FinSaleDeductionBill.getFinSaleDeductionAttachmentList();
        if (CollectionUtils.isNotEmpty(finSaleDeductionAttachmentList)) {
            addFinSaleDeductionAttachment(FinSaleDeductionBill, finSaleDeductionAttachmentList);
        }
        return 1;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(FinSaleDeductionBill o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 服务销售验收单-明细对象
     */
    private void addFinSaleDeductionItem(FinSaleDeductionBill FinSaleDeductionBill, List<FinSaleDeductionBillItem> finSaleDeductionItemList) {
        finSaleDeductionItemMapper.delete(
                new UpdateWrapper<FinSaleDeductionBillItem>()
                        .lambda()
                        .eq(FinSaleDeductionBillItem::getSaleDeductionBillSid, FinSaleDeductionBill.getSaleDeductionBillSid())
        );
        finSaleDeductionItemList.forEach(o -> {
            o.setClientId(SecurityUtils.getClientId());
            o.setSaleDeductionBillItemSid(IdWorker.getId());
            o.setSaleDeductionBillSid(FinSaleDeductionBill.getSaleDeductionBillSid());
            o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            o.setCreateDate(new Date());
            finSaleDeductionItemMapper.insert(o);
        });
    }

    /**
     * 服务销售验收单-附件对象
     */
    private void addFinSaleDeductionAttachment(FinSaleDeductionBill FinSaleDeductionBill, List<FinSaleDeductionBillAttachment> finSaleDeductionAttachmentList) {
        finSaleDeductionAttachmentMapper.delete(
                new UpdateWrapper<FinSaleDeductionBillAttachment>()
                        .lambda()
                        .eq(FinSaleDeductionBillAttachment::getSaleDeductionBillSid, FinSaleDeductionBill.getSaleDeductionBillSid())
        );
        finSaleDeductionAttachmentList.forEach(o -> {
            o.setClientId(SecurityUtils.getClientId());
            o.setSaleDeductionBillAttachmentSid(IdWorker.getId());
            o.setSaleDeductionBillSid(FinSaleDeductionBill.getSaleDeductionBillSid());
            o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            o.setCreateDate(new Date());
            finSaleDeductionAttachmentMapper.insert(o);
        });
    }

    /**
     * 修改销售扣款单
     *
     * @param FinSaleDeductionBill 销售扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinSaleDeduction(FinSaleDeductionBill FinSaleDeductionBill) {
        setConfirmInfo(FinSaleDeductionBill);
        FinSaleDeductionBillMapper.updateAllById(FinSaleDeductionBill);
        //销售扣款单-明细对象
        List<FinSaleDeductionBillItem> finSaleDeductionItemList = FinSaleDeductionBill.getFinSaleDeductionItemList();
        if (CollectionUtils.isNotEmpty(finSaleDeductionItemList)) {
            addFinSaleDeductionItem(FinSaleDeductionBill, finSaleDeductionItemList);
        }
        //销售扣款单-附件对象
        List<FinSaleDeductionBillAttachment> finSaleDeductionAttachmentList = FinSaleDeductionBill.getFinSaleDeductionAttachmentList();
        if (CollectionUtils.isNotEmpty(finSaleDeductionAttachmentList)) {
            addFinSaleDeductionAttachment(FinSaleDeductionBill, finSaleDeductionAttachmentList);
        }
        return 1;
    }

    /**
     * 批量删除销售扣款单
     *
     * @param saleDeductionSids 需要删除的销售扣款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinSaleDeductionByIds(Long[] saleDeductionSids) {
        SalServiceAcceptance params = new SalServiceAcceptance();
        params.setServiceAcceptanceSids(saleDeductionSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = FinSaleDeductionBillMapper.countByDomain(params);
        if (count != saleDeductionSids.length){
            throw new BaseException("仅保存状态才允许删除");
        }
        //删除销售扣款单
        FinSaleDeductionBillMapper.deleteFinSaleDeductionByIds(saleDeductionSids);
        //删除销售扣款单明细
        finSaleDeductionItemMapper.deleteFinSaleDeductionItemByIds(saleDeductionSids);
        //删除销售扣款单附件
        finSaleDeductionAttachmentMapper.deleteFinSaleDeductionAttachmentByIds(saleDeductionSids);
        return saleDeductionSids.length;
    }

    /**
     * 销售扣款单确认
     */
    @Override
    public int confirm(FinSaleDeductionBill FinSaleDeductionBill) {
        //销售扣款单sids
        Long[] saleDeductionSids = FinSaleDeductionBill.getSaleDeductionSids();
        SalServiceAcceptance params = new SalServiceAcceptance();
        params.setServiceAcceptanceSids(saleDeductionSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = FinSaleDeductionBillMapper.countByDomain(params);
        if (count != saleDeductionSids.length){
            throw new BaseException("仅保存状态才允许确认");
        }
        FinSaleDeductionBill.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        FinSaleDeductionBill.setConfirmDate(new Date());
        return FinSaleDeductionBillMapper.confirm(FinSaleDeductionBill);
    }

    /**
     * 销售扣款单变更
     */
    @Override
    public int change(FinSaleDeductionBill FinSaleDeductionBill) {
        Long saleDeductionSid = FinSaleDeductionBill.getSaleDeductionBillSid();
        FinSaleDeductionBill saleDeduction = FinSaleDeductionBillMapper.selectFinSaleDeductionById(saleDeductionSid);
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(saleDeduction.getHandleStatus())){
            throw new BaseException("仅确认状态才允许变更");
        }
        FinSaleDeductionBillMapper.updateAllById(FinSaleDeductionBill);
        //销售扣款单-明细对象
        List<FinSaleDeductionBillItem> finSaleDeductionItemList = FinSaleDeductionBill.getFinSaleDeductionItemList();
        if (CollectionUtils.isNotEmpty(finSaleDeductionItemList)) {
            addFinSaleDeductionItem(FinSaleDeductionBill, finSaleDeductionItemList);
        }
        //销售扣款单-附件对象
        List<FinSaleDeductionBillAttachment> finSaleDeductionAttachmentList = FinSaleDeductionBill.getFinSaleDeductionAttachmentList();
        if (CollectionUtils.isNotEmpty(finSaleDeductionAttachmentList)) {
            addFinSaleDeductionAttachment(FinSaleDeductionBill, finSaleDeductionAttachmentList);
        }
        return 1;
    }
}

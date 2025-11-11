package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IPurServiceAcceptanceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 服务采购验收单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-07
 */
@Service
@SuppressWarnings("all")
public class PurServiceAcceptanceServiceImpl extends ServiceImpl<PurServiceAcceptanceMapper,PurServiceAcceptance>  implements IPurServiceAcceptanceService {
    @Autowired
    private PurServiceAcceptanceMapper purServiceAcceptanceMapper;

    @Autowired
    private PurServiceAcceptanceItemMapper purServiceAcceptanceItemMapper;

    @Autowired
    private PurServiceAcceptanceAttachmentMapper purServiceAcceptanceAttachmentMapper;

    @Autowired
    private PurServiceAcceptancePartnerMapper purServiceAcceptancePartnerMapper;

    @Autowired
    private PurPurchaseOrderMapper purPurchaseOrderMapper;

    /**
     * 查询服务采购验收单
     *
     * @param serviceAcceptanceSid 服务采购验收单ID
     * @return 服务采购验收单
     */
    @Override
    public PurServiceAcceptance selectPurServiceAcceptanceById(Long serviceAcceptanceSid) {
        PurServiceAcceptance purServiceAcceptance = purServiceAcceptanceMapper.selectPurServiceAcceptanceById(serviceAcceptanceSid);
        if (purServiceAcceptance == null){
            return null;
        }
        //服务采购验收单-明细
        PurServiceAcceptanceItem purServiceAcceptanceItem = new PurServiceAcceptanceItem();
        purServiceAcceptanceItem.setServiceAcceptanceSid(serviceAcceptanceSid);
        List<PurServiceAcceptanceItem> purServiceAcceptanceItemList = purServiceAcceptanceItemMapper.selectPurServiceAcceptanceItemList(purServiceAcceptanceItem);
        //服务采购验收单-附件
        PurServiceAcceptanceAttachment purServiceAcceptanceAttachment = new PurServiceAcceptanceAttachment();
        purServiceAcceptanceAttachment.setServiceAcceptanceSid(serviceAcceptanceSid);
        List<PurServiceAcceptanceAttachment> purServiceAcceptanceAttachmentList = purServiceAcceptanceAttachmentMapper.selectPurServiceAcceptanceAttachmentList(purServiceAcceptanceAttachment);
        //服务采购验收单-合作伙伴
        PurServiceAcceptancePartner purServiceAcceptancePartner = new PurServiceAcceptancePartner();
        purServiceAcceptancePartner.setServiceAcceptanceSid(serviceAcceptanceSid);
        List<PurServiceAcceptancePartner> purServiceAcceptancePartnerList = purServiceAcceptancePartnerMapper.selectPurServiceAcceptancePartnerList(purServiceAcceptancePartner);

        purServiceAcceptance.setPurServiceAcceptanceItemList(purServiceAcceptanceItemList);
        purServiceAcceptance.setPurServiceAcceptanceAttachmentList(purServiceAcceptanceAttachmentList);
        purServiceAcceptance.setPurServiceAcceptancePartnerList(purServiceAcceptancePartnerList);
        return purServiceAcceptance;
    }

    /**
     * 查询服务采购验收单列表
     *
     * @param purServiceAcceptance 服务采购验收单
     * @return 服务采购验收单
     */
    @Override
    public List<PurServiceAcceptance> selectPurServiceAcceptanceList(PurServiceAcceptance purServiceAcceptance) {
        return purServiceAcceptanceMapper.selectPurServiceAcceptanceList(purServiceAcceptance);
    }

    /**
     * 新增服务采购验收单
     * 需要注意编码重复校验
     * @param purServiceAcceptance 服务采购验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurServiceAcceptance(PurServiceAcceptance purServiceAcceptance) {
        setConfirmInfo(purServiceAcceptance);
        purServiceAcceptanceMapper.insert(purServiceAcceptance);
        //服务采购确认单-明细对象
        List<PurServiceAcceptanceItem> purServiceAcceptanceItemList = purServiceAcceptance.getPurServiceAcceptanceItemList();
        if (CollectionUtils.isNotEmpty(purServiceAcceptanceItemList)) {
            addPurServiceAcceptanceItem(purServiceAcceptance, purServiceAcceptanceItemList);
        }
        //服务采购确认单-附件对象
        List<PurServiceAcceptanceAttachment> purServiceAcceptanceAttachmentList = purServiceAcceptance.getPurServiceAcceptanceAttachmentList();
        if (CollectionUtils.isNotEmpty(purServiceAcceptanceAttachmentList)) {
            addPurServiceAcceptanceAttachment(purServiceAcceptance, purServiceAcceptanceAttachmentList);
        }
        //服务采购确认单-合作伙伴对象
        List<PurServiceAcceptancePartner> purServiceAcceptancePartnerList = purServiceAcceptance.getPurServiceAcceptancePartnerList();
        if (CollectionUtils.isNotEmpty(purServiceAcceptancePartnerList)) {
            addPurServiceAcceptancePartne(purServiceAcceptance, purServiceAcceptancePartnerList);
        }
        return 1;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(PurServiceAcceptance o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 服务采购确认单-明细对象
     */
    private void addPurServiceAcceptanceItem(PurServiceAcceptance purServiceAcceptance, List<PurServiceAcceptanceItem> purServiceAcceptanceItemList) {
        purServiceAcceptanceItemMapper.delete(
                new UpdateWrapper<PurServiceAcceptanceItem>()
                        .lambda()
                        .eq(PurServiceAcceptanceItem::getServiceAcceptanceSid, purServiceAcceptance.getServiceAcceptanceSid())
        );
        purServiceAcceptanceItemList.forEach(o -> {
            o.setServiceAcceptanceSid(purServiceAcceptance.getServiceAcceptanceSid());
            purServiceAcceptanceItemMapper.insert(o);
        });
    }

    /**
     * 服务采购确认单-附件对象
     */
    private void addPurServiceAcceptanceAttachment(PurServiceAcceptance purServiceAcceptance, List<PurServiceAcceptanceAttachment> purServiceAcceptanceAttachmentList) {
        purServiceAcceptanceAttachmentMapper.delete(
                new UpdateWrapper<PurServiceAcceptanceAttachment>()
                        .lambda()
                        .eq(PurServiceAcceptanceAttachment::getServiceAcceptanceSid, purServiceAcceptance.getServiceAcceptanceSid())
        );
        purServiceAcceptanceAttachmentList.forEach(o -> {
            o.setServiceAcceptanceSid(purServiceAcceptance.getServiceAcceptanceSid());
            purServiceAcceptanceAttachmentMapper.insert(o);
        });
    }

    /**
     * 服务采购确认单-合作伙伴对象
     */
    private void addPurServiceAcceptancePartne(PurServiceAcceptance purServiceAcceptance, List<PurServiceAcceptancePartner> purServiceAcceptancePartnerList) {
        purServiceAcceptancePartnerMapper.delete(
                new UpdateWrapper<PurServiceAcceptancePartner>()
                        .lambda()
                        .eq(PurServiceAcceptancePartner::getServiceAcceptanceSid, purServiceAcceptance.getServiceAcceptanceSid())
        );
        purServiceAcceptancePartnerList.forEach(o -> {
            o.setServiceAcceptanceSid(purServiceAcceptance.getServiceAcceptanceSid());
            purServiceAcceptancePartnerMapper.insert(o);
        });
    }

    /**
     * 修改服务采购验收单
     *
     * @param purServiceAcceptance 服务采购验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurServiceAcceptance(PurServiceAcceptance purServiceAcceptance) {
        setConfirmInfo(purServiceAcceptance);
        purServiceAcceptanceMapper.updateAllById(purServiceAcceptance);
        //服务采购确认单-明细对象
        List<PurServiceAcceptanceItem> purServiceAcceptanceItemList = purServiceAcceptance.getPurServiceAcceptanceItemList();
        if (CollectionUtils.isNotEmpty(purServiceAcceptanceItemList)) {
            purServiceAcceptanceItemList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addPurServiceAcceptanceItem(purServiceAcceptance, purServiceAcceptanceItemList);
        }
        //服务采购确认单-附件对象
        List<PurServiceAcceptanceAttachment> purServiceAcceptanceAttachmentList = purServiceAcceptance.getPurServiceAcceptanceAttachmentList();
        if (CollectionUtils.isNotEmpty(purServiceAcceptanceAttachmentList)) {
            purServiceAcceptanceAttachmentList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addPurServiceAcceptanceAttachment(purServiceAcceptance, purServiceAcceptanceAttachmentList);
        }
        //服务采购确认单-合作伙伴对象
        List<PurServiceAcceptancePartner> purServiceAcceptancePartnerList = purServiceAcceptance.getPurServiceAcceptancePartnerList();
        if (CollectionUtils.isNotEmpty(purServiceAcceptancePartnerList)) {
            purServiceAcceptancePartnerList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addPurServiceAcceptancePartne(purServiceAcceptance, purServiceAcceptancePartnerList);
        }
        return 1;
    }

    /**
     * 批量删除服务采购验收单
     *
     * @param clientIds 需要删除的服务采购验收单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurServiceAcceptanceByIds(Long[] serviceAcceptanceSids) {
        PurServiceAcceptance params = new PurServiceAcceptance();
        params.setServiceAcceptanceSids(serviceAcceptanceSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = purServiceAcceptanceMapper.countByDomain(params);
        if (count != serviceAcceptanceSids.length){
            throw new BaseException("仅保存状态才允许删除");
        }
        //删除服务采购验收单
        purServiceAcceptanceMapper.deletePurServiceAcceptanceByIds(serviceAcceptanceSids);
        //删除服务采购验收单明细
        purServiceAcceptanceItemMapper.deletePurServiceAcceptanceItemByIds(serviceAcceptanceSids);
        //删除服务采购验收单附件
        purServiceAcceptanceAttachmentMapper.deletePurServiceAcceptanceAttachmentByIds(serviceAcceptanceSids);
        //删除服务采购验收单合作伙伴
        purServiceAcceptancePartnerMapper.deletePurServiceAcceptancePartnerByIds(serviceAcceptanceSids);
        return serviceAcceptanceSids.length;
    }

    /**
     * 服务采购验收单确认
     */
    @Override
    public int confirm(PurServiceAcceptance purServiceAcceptance) {
        //服务采购验收单sids
        Long[] serviceAcceptanceSids = purServiceAcceptance.getServiceAcceptanceSids();
        PurServiceAcceptance params = new PurServiceAcceptance();
        params.setServiceAcceptanceSids(serviceAcceptanceSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = purServiceAcceptanceMapper.countByDomain(params);
        if (count != serviceAcceptanceSids.length){
            throw new BaseException("仅保存状态才允许确认");
        }
        purServiceAcceptance.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        purServiceAcceptance.setConfirmDate(new Date());
        return purServiceAcceptanceMapper.confirm(purServiceAcceptance);
    }

    /**
     * 服务采购验收单变更
     */
    @Override
    public int change(PurServiceAcceptance purServiceAcceptance) {
        Long serviceAcceptanceSid = purServiceAcceptance.getServiceAcceptanceSid();
        PurServiceAcceptance serviceAcceptance = purServiceAcceptanceMapper.selectPurServiceAcceptanceById(serviceAcceptanceSid);
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(serviceAcceptance.getHandleStatus())){
            throw new BaseException("仅确认状态才允许变更");
        }
        purServiceAcceptanceMapper.updateAllById(purServiceAcceptance);
        //服务采购确认单-明细对象
        List<PurServiceAcceptanceItem> purServiceAcceptanceItemList = purServiceAcceptance.getPurServiceAcceptanceItemList();
        if (CollectionUtils.isNotEmpty(purServiceAcceptanceItemList)) {
            purServiceAcceptanceItemList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addPurServiceAcceptanceItem(purServiceAcceptance, purServiceAcceptanceItemList);
        }
        //服务采购确认单-附件对象
        List<PurServiceAcceptanceAttachment> purServiceAcceptanceAttachmentList = purServiceAcceptance.getPurServiceAcceptanceAttachmentList();
        if (CollectionUtils.isNotEmpty(purServiceAcceptanceAttachmentList)) {
            purServiceAcceptanceAttachmentList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addPurServiceAcceptanceAttachment(purServiceAcceptance, purServiceAcceptanceAttachmentList);
        }
        //服务采购确认单-合作伙伴对象
        List<PurServiceAcceptancePartner> purServiceAcceptancePartnerList = purServiceAcceptance.getPurServiceAcceptancePartnerList();
        if (CollectionUtils.isNotEmpty(purServiceAcceptancePartnerList)) {
            purServiceAcceptancePartnerList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addPurServiceAcceptancePartne(purServiceAcceptance, purServiceAcceptancePartnerList);
        }
        return 1;
    }
}

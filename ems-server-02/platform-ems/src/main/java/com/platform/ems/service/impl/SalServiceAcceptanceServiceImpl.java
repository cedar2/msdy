package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISalServiceAcceptanceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 服务销售验收单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-06
 */
@Service
@SuppressWarnings("all")
public class SalServiceAcceptanceServiceImpl extends ServiceImpl<SalServiceAcceptanceMapper,SalServiceAcceptance>  implements ISalServiceAcceptanceService {
    @Autowired
    private SalServiceAcceptanceMapper salServiceAcceptanceMapper;

    @Autowired
    private SalServiceAcceptanceItemMapper salServiceAcceptanceItemMapper;

    @Autowired
    private SalServiceAcceptanceAttachmentMapper salServiceAcceptanceAttachmentMapper;

    @Autowired
    private SalServiceAcceptancePartnerMapper salServiceAcceptancePartnerMapper;

    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;

    /**
     * 查询服务销售验收单
     *
     * @param serviceAcceptanceSid 服务销售验收单ID
     * @return 服务销售验收单
     */
    @Override
    public SalServiceAcceptance selectSalServiceAcceptanceById(Long serviceAcceptanceSid) {
        SalServiceAcceptance salServiceAcceptance = salServiceAcceptanceMapper.selectSalServiceAcceptanceById(serviceAcceptanceSid);
        if (salServiceAcceptance == null){
            return null;
        }
        //服务销售验收单-明细
        SalServiceAcceptanceItem serviceAcceptanceItem = new SalServiceAcceptanceItem();
        serviceAcceptanceItem.setServiceAcceptanceSid(serviceAcceptanceSid);
        List<SalServiceAcceptanceItem> salServiceAcceptanceItemList =
                salServiceAcceptanceItemMapper.selectSalServiceAcceptanceItemList(serviceAcceptanceItem);
        //服务销售验收单-附件
        SalServiceAcceptanceAttachment serviceAcceptanceAttachment = new SalServiceAcceptanceAttachment();
        serviceAcceptanceAttachment.setServiceAcceptanceSid(serviceAcceptanceSid);
        List<SalServiceAcceptanceAttachment> salServiceAcceptanceAttachmentList =
                salServiceAcceptanceAttachmentMapper.selectSalServiceAcceptanceAttachmentList(serviceAcceptanceAttachment);
        //服务销售验收单-合作伙伴
        SalServiceAcceptancePartner salServiceAcceptancePartner = new SalServiceAcceptancePartner();
        salServiceAcceptancePartner.setServiceAcceptanceSid(serviceAcceptanceSid);
        List<SalServiceAcceptancePartner> salServiceAcceptancePartnerList =
                salServiceAcceptancePartnerMapper.selectSalServiceAcceptancePartnerList(salServiceAcceptancePartner);

        salServiceAcceptance.setSalServiceAcceptanceItemList(salServiceAcceptanceItemList);
        salServiceAcceptance.setSalServiceAcceptanceAttachmentList(salServiceAcceptanceAttachmentList);
        salServiceAcceptance.setSalServiceAcceptancePartnerList(salServiceAcceptancePartnerList);
        return salServiceAcceptance;
    }

    /**
     * 查询服务销售验收单列表
     *
     * @param salServiceAcceptance 服务销售验收单
     * @return 服务销售验收单
     */
    @Override
    public List<SalServiceAcceptance> selectSalServiceAcceptanceList(SalServiceAcceptance salServiceAcceptance) {
        return salServiceAcceptanceMapper.selectSalServiceAcceptanceList(salServiceAcceptance);
    }

    /**
     * 新增服务销售验收单
     * 需要注意编码重复校验
     * @param salServiceAcceptance 服务销售验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalServiceAcceptance(SalServiceAcceptance salServiceAcceptance) {
        setConfirmInfo(salServiceAcceptance);
        salServiceAcceptanceMapper.insert(salServiceAcceptance);
        //服务销售验收单-明细对象
        List<SalServiceAcceptanceItem> salServiceAcceptanceItemList = salServiceAcceptance.getSalServiceAcceptanceItemList();
        if (CollectionUtils.isNotEmpty(salServiceAcceptanceItemList)) {
            addSalServiceAcceptanceItem(salServiceAcceptance, salServiceAcceptanceItemList);
        }
        //服务销售验收单-附件对象
        List<SalServiceAcceptanceAttachment> salServiceAcceptanceAttachmentList = salServiceAcceptance.getSalServiceAcceptanceAttachmentList();
        if (CollectionUtils.isNotEmpty(salServiceAcceptanceAttachmentList)) {
            addSalServiceAcceptanceAttachment(salServiceAcceptance, salServiceAcceptanceAttachmentList);
        }
        //服务销售验收单-合作伙伴对象
        List<SalServiceAcceptancePartner> salServiceAcceptancePartnerList = salServiceAcceptance.getSalServiceAcceptancePartnerList();
        if (CollectionUtils.isNotEmpty(salServiceAcceptancePartnerList)) {
            addSalServiceAcceptancePartne(salServiceAcceptance, salServiceAcceptancePartnerList);
        }
        return 1;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(SalServiceAcceptance o) {
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
    private void addSalServiceAcceptanceItem(SalServiceAcceptance salServiceAcceptance, List<SalServiceAcceptanceItem> salServiceAcceptanceItemList) {
        salServiceAcceptanceItemMapper.delete(
                new UpdateWrapper<SalServiceAcceptanceItem>()
                        .lambda()
                        .eq(SalServiceAcceptanceItem::getServiceAcceptanceSid, salServiceAcceptance.getServiceAcceptanceSid())
        );
        salServiceAcceptanceItemList.forEach(o -> {
            o.setServiceAcceptanceSid(salServiceAcceptance.getServiceAcceptanceSid());
            salServiceAcceptanceItemMapper.insert(o);
        });
    }

    /**
     * 服务销售验收单-附件对象
     */
    private void addSalServiceAcceptanceAttachment(SalServiceAcceptance salServiceAcceptance, List<SalServiceAcceptanceAttachment> salServiceAcceptanceAttachmentList) {
        salServiceAcceptanceAttachmentMapper.delete(
                new UpdateWrapper<SalServiceAcceptanceAttachment>()
                        .lambda()
                        .eq(SalServiceAcceptanceAttachment::getServiceAcceptanceSid, salServiceAcceptance.getServiceAcceptanceSid())
        );
        salServiceAcceptanceAttachmentList.forEach(o -> {
            o.setServiceAcceptanceSid(salServiceAcceptance.getServiceAcceptanceSid());
            salServiceAcceptanceAttachmentMapper.insert(o);
        });
    }

    /**
     * 服务销售验收单-合作伙伴对象
     */
    private void addSalServiceAcceptancePartne(SalServiceAcceptance salServiceAcceptance, List<SalServiceAcceptancePartner> salServiceAcceptancePartnerList) {
        salServiceAcceptancePartnerMapper.delete(
                new UpdateWrapper<SalServiceAcceptancePartner>()
                        .lambda()
                        .eq(SalServiceAcceptancePartner::getServiceAcceptanceSid, salServiceAcceptance.getServiceAcceptanceSid())
        );
        salServiceAcceptancePartnerList.forEach(o -> {
            o.setServiceAcceptanceSid(salServiceAcceptance.getServiceAcceptanceSid());
            salServiceAcceptancePartnerMapper.insert(o);
        });
    }

    /**
     * 修改服务销售验收单
     *
     * @param salServiceAcceptance 服务销售验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalServiceAcceptance(SalServiceAcceptance salServiceAcceptance) {
        setConfirmInfo(salServiceAcceptance);
        salServiceAcceptanceMapper.updateAllById(salServiceAcceptance);
        //服务销售验收单-明细对象
        List<SalServiceAcceptanceItem> salServiceAcceptanceItemList = salServiceAcceptance.getSalServiceAcceptanceItemList();
        if (CollectionUtils.isNotEmpty(salServiceAcceptanceItemList)) {
            salServiceAcceptanceItemList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addSalServiceAcceptanceItem(salServiceAcceptance, salServiceAcceptanceItemList);
        }
        //服务销售验收单-附件对象
        List<SalServiceAcceptanceAttachment> salServiceAcceptanceAttachmentList = salServiceAcceptance.getSalServiceAcceptanceAttachmentList();
        if (CollectionUtils.isNotEmpty(salServiceAcceptanceAttachmentList)) {
            salServiceAcceptanceAttachmentList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addSalServiceAcceptanceAttachment(salServiceAcceptance, salServiceAcceptanceAttachmentList);
        }
        //服务销售验收单-合作伙伴对象
        List<SalServiceAcceptancePartner> salServiceAcceptancePartneList = salServiceAcceptance.getSalServiceAcceptancePartnerList();
        if (CollectionUtils.isNotEmpty(salServiceAcceptancePartneList)) {
            salServiceAcceptancePartneList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addSalServiceAcceptancePartne(salServiceAcceptance, salServiceAcceptancePartneList);
        }
        return 1;
    }

    /**
     * 批量删除服务销售验收单
     *
     * @param serviceAcceptanceSids 需要删除的服务销售验收单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalServiceAcceptanceByIds(Long[] serviceAcceptanceSids) {
        SalServiceAcceptance params = new SalServiceAcceptance();
        params.setServiceAcceptanceSids(serviceAcceptanceSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = salServiceAcceptanceMapper.countByDomain(params);
        if (count != serviceAcceptanceSids.length){
            throw new BaseException("仅保存状态才允许删除");
        }
        //删除服务销售验收单
        salServiceAcceptanceMapper.deleteServiceAcceptanceByIds(serviceAcceptanceSids);
        //删除服务销售验收单明细
        salServiceAcceptanceItemMapper.deleteServiceAcceptanceItemByIds(serviceAcceptanceSids);
        //删除服务销售验收单附件
        salServiceAcceptanceAttachmentMapper.deleteServiceAcceptanceAttachmentByIds(serviceAcceptanceSids);
        //删除服务销售验收单合作伙伴
        salServiceAcceptancePartnerMapper.deleteServiceAcceptancePartnerByIds(serviceAcceptanceSids);
        return serviceAcceptanceSids.length;
    }

    /**
     * 服务销售验收单确认
     */
    @Override
    public int confirm(SalServiceAcceptance salServiceAcceptance) {
        //服务销售验收单sids
        Long[] serviceAcceptanceSids = salServiceAcceptance.getServiceAcceptanceSids();
        SalServiceAcceptance params = new SalServiceAcceptance();
        params.setServiceAcceptanceSids(serviceAcceptanceSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = salServiceAcceptanceMapper.countByDomain(params);
        if (count != serviceAcceptanceSids.length){
            throw new BaseException("仅保存状态才允许确认");
        }
        salServiceAcceptance.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        salServiceAcceptance.setConfirmDate(new Date());
        return salServiceAcceptanceMapper.confirm(salServiceAcceptance);
    }

    /**
     * 服务销售验收单变更
     */
    @Override
    public int change(SalServiceAcceptance salServiceAcceptance) {
        Long serviceAcceptanceSid = salServiceAcceptance.getServiceAcceptanceSid();
        SalServiceAcceptance serviceAcceptance = salServiceAcceptanceMapper.selectSalServiceAcceptanceById(serviceAcceptanceSid);
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(serviceAcceptance.getHandleStatus())){
            throw new BaseException("仅确认状态才允许变更");
        }
        salServiceAcceptanceMapper.updateAllById(salServiceAcceptance);
        //服务销售验收单-明细对象
        List<SalServiceAcceptanceItem> salServiceAcceptanceItemList = salServiceAcceptance.getSalServiceAcceptanceItemList();
        if (CollectionUtils.isNotEmpty(salServiceAcceptanceItemList)) {
            salServiceAcceptanceItemList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addSalServiceAcceptanceItem(salServiceAcceptance, salServiceAcceptanceItemList);
        }
        //服务销售验收单-附件对象
        List<SalServiceAcceptanceAttachment> salServiceAcceptanceAttachmentList = salServiceAcceptance.getSalServiceAcceptanceAttachmentList();
        if (CollectionUtils.isNotEmpty(salServiceAcceptanceAttachmentList)) {
            salServiceAcceptanceAttachmentList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addSalServiceAcceptanceAttachment(salServiceAcceptance, salServiceAcceptanceAttachmentList);
        }
        //服务销售验收单-合作伙伴对象
        List<SalServiceAcceptancePartner> salServiceAcceptancePartneList = salServiceAcceptance.getSalServiceAcceptancePartnerList();
        if (CollectionUtils.isNotEmpty(salServiceAcceptancePartneList)) {
            salServiceAcceptancePartneList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addSalServiceAcceptancePartne(salServiceAcceptance, salServiceAcceptancePartneList);
        }
        return 1;
    }
}

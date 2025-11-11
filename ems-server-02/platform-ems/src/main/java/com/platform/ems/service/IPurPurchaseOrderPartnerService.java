package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurPurchaseOrderPartner;

/**
 * 采购订单-合作伙伴Service接口
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
public interface IPurPurchaseOrderPartnerService extends IService<PurPurchaseOrderPartner>{
    /**
     * 查询采购订单-合作伙伴
     * 
     * @param purchaseOrderPartnerSid 采购订单-合作伙伴ID
     * @return 采购订单-合作伙伴
     */
    public PurPurchaseOrderPartner selectPurPurchaseOrderPartnerById(Long purchaseOrderPartnerSid);

    /**
     * 查询采购订单-合作伙伴列表
     * 
     * @param purPurchaseOrderPartner 采购订单-合作伙伴
     * @return 采购订单-合作伙伴集合
     */
    public List<PurPurchaseOrderPartner> selectPurPurchaseOrderPartnerList(PurPurchaseOrderPartner purPurchaseOrderPartner);

    /**
     * 新增采购订单-合作伙伴
     * 
     * @param purPurchaseOrderPartner 采购订单-合作伙伴
     * @return 结果
     */
    public int insertPurPurchaseOrderPartner(PurPurchaseOrderPartner purPurchaseOrderPartner);

    /**
     * 修改采购订单-合作伙伴
     * 
     * @param purPurchaseOrderPartner 采购订单-合作伙伴
     * @return 结果
     */
    public int updatePurPurchaseOrderPartner(PurPurchaseOrderPartner purPurchaseOrderPartner);

    /**
     * 批量删除采购订单-合作伙伴
     * 
     * @param purchaseOrderPartnerSids 需要删除的采购订单-合作伙伴ID
     * @return 结果
     */
    public int deletePurPurchaseOrderPartnerByIds(List<Long> purchaseOrderPartnerSids);

}

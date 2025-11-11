package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PurPurchaseOrderPartnerMapper;
import com.platform.ems.domain.PurPurchaseOrderPartner;
import com.platform.ems.service.IPurPurchaseOrderPartnerService;

/**
 * 采购订单-合作伙伴Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
@Service
@SuppressWarnings("all")
public class PurPurchaseOrderPartnerServiceImpl extends ServiceImpl<PurPurchaseOrderPartnerMapper,PurPurchaseOrderPartner>  implements IPurPurchaseOrderPartnerService {
    @Autowired
    private PurPurchaseOrderPartnerMapper purPurchaseOrderPartnerMapper;

    /**
     * 查询采购订单-合作伙伴
     * 
     * @param purchaseOrderPartnerSid 采购订单-合作伙伴ID
     * @return 采购订单-合作伙伴
     */
    @Override
    public PurPurchaseOrderPartner selectPurPurchaseOrderPartnerById(Long purchaseOrderPartnerSid) {
        return purPurchaseOrderPartnerMapper.selectPurPurchaseOrderPartnerById(purchaseOrderPartnerSid);
    }

    /**
     * 查询采购订单-合作伙伴列表
     * 
     * @param purPurchaseOrderPartner 采购订单-合作伙伴
     * @return 采购订单-合作伙伴
     */
    @Override
    public List<PurPurchaseOrderPartner> selectPurPurchaseOrderPartnerList(PurPurchaseOrderPartner purPurchaseOrderPartner) {
        return purPurchaseOrderPartnerMapper.selectPurPurchaseOrderPartnerList(purPurchaseOrderPartner);
    }

    /**
     * 新增采购订单-合作伙伴
     * 需要注意编码重复校验
     * @param purPurchaseOrderPartner 采购订单-合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurPurchaseOrderPartner(PurPurchaseOrderPartner purPurchaseOrderPartner) {
        return purPurchaseOrderPartnerMapper.insert(purPurchaseOrderPartner);
    }

    /**
     * 修改采购订单-合作伙伴
     * 
     * @param purPurchaseOrderPartner 采购订单-合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurPurchaseOrderPartner(PurPurchaseOrderPartner purPurchaseOrderPartner) {
        return purPurchaseOrderPartnerMapper.updateById(purPurchaseOrderPartner);
    }

    /**
     * 批量删除采购订单-合作伙伴
     * 
     * @param purchaseOrderPartnerSids 需要删除的采购订单-合作伙伴ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurPurchaseOrderPartnerByIds(List<Long> purchaseOrderPartnerSids) {
        return purPurchaseOrderPartnerMapper.deleteBatchIds(purchaseOrderPartnerSids);
    }


}

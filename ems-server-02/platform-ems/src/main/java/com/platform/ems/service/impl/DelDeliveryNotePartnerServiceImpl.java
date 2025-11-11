package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.DelDeliveryNotePartnerMapper;
import com.platform.ems.domain.DelDeliveryNotePartner;
import com.platform.ems.service.IDelDeliveryNotePartnerService;

/**
 * 交货单-合作伙伴Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-21
 */
@Service
@SuppressWarnings("all")
public class DelDeliveryNotePartnerServiceImpl extends ServiceImpl<DelDeliveryNotePartnerMapper,DelDeliveryNotePartner>  implements IDelDeliveryNotePartnerService {
    @Autowired
    private DelDeliveryNotePartnerMapper delDeliveryNotePartnerMapper;

    /**
     * 查询交货单-合作伙伴
     * 
     * @param deliveryNotePartnerSid 交货单-合作伙伴ID
     * @return 交货单-合作伙伴
     */
    @Override
    public DelDeliveryNotePartner selectDelDeliveryNotePartnerById(Long deliveryNotePartnerSid) {
        return delDeliveryNotePartnerMapper.selectDelDeliveryNotePartnerById(deliveryNotePartnerSid);
    }

    /**
     * 查询交货单-合作伙伴列表
     * 
     * @param delDeliveryNotePartner 交货单-合作伙伴
     * @return 交货单-合作伙伴
     */
    @Override
    public List<DelDeliveryNotePartner> selectDelDeliveryNotePartnerList(DelDeliveryNotePartner delDeliveryNotePartner) {
        return delDeliveryNotePartnerMapper.selectDelDeliveryNotePartnerList(delDeliveryNotePartner);
    }

    /**
     * 新增交货单-合作伙伴
     * 需要注意编码重复校验
     * @param delDeliveryNotePartner 交货单-合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDelDeliveryNotePartner(DelDeliveryNotePartner delDeliveryNotePartner) {
        return delDeliveryNotePartnerMapper.insert(delDeliveryNotePartner);
    }

    /**
     * 修改交货单-合作伙伴
     * 
     * @param delDeliveryNotePartner 交货单-合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDelDeliveryNotePartner(DelDeliveryNotePartner delDeliveryNotePartner) {
        return delDeliveryNotePartnerMapper.updateById(delDeliveryNotePartner);
    }

    /**
     * 批量删除交货单-合作伙伴
     * 
     * @param deliveryNotePartnerSids 需要删除的交货单-合作伙伴ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDelDeliveryNotePartnerByIds(List<Long> deliveryNotePartnerSids) {
        return delDeliveryNotePartnerMapper.deleteBatchIds(deliveryNotePartnerSids);
    }


}

package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DelDeliveryNotePartner;

/**
 * 交货单-合作伙伴Service接口
 * 
 * @author linhongwei
 * @date 2021-04-21
 */
public interface IDelDeliveryNotePartnerService extends IService<DelDeliveryNotePartner>{
    /**
     * 查询交货单-合作伙伴
     * 
     * @param deliveryNotePartnerSid 交货单-合作伙伴ID
     * @return 交货单-合作伙伴
     */
    public DelDeliveryNotePartner selectDelDeliveryNotePartnerById(Long deliveryNotePartnerSid);

    /**
     * 查询交货单-合作伙伴列表
     * 
     * @param delDeliveryNotePartner 交货单-合作伙伴
     * @return 交货单-合作伙伴集合
     */
    public List<DelDeliveryNotePartner> selectDelDeliveryNotePartnerList(DelDeliveryNotePartner delDeliveryNotePartner);

    /**
     * 新增交货单-合作伙伴
     * 
     * @param delDeliveryNotePartner 交货单-合作伙伴
     * @return 结果
     */
    public int insertDelDeliveryNotePartner(DelDeliveryNotePartner delDeliveryNotePartner);

    /**
     * 修改交货单-合作伙伴
     * 
     * @param delDeliveryNotePartner 交货单-合作伙伴
     * @return 结果
     */
    public int updateDelDeliveryNotePartner(DelDeliveryNotePartner delDeliveryNotePartner);

    /**
     * 批量删除交货单-合作伙伴
     * 
     * @param deliveryNotePartnerSids 需要删除的交货单-合作伙伴ID
     * @return 结果
     */
    public int deleteDelDeliveryNotePartnerByIds(List<Long> deliveryNotePartnerSids);

}

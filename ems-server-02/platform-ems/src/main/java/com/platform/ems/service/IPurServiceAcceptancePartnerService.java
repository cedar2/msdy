package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurServiceAcceptancePartner;

/**
 * 服务采购验收单-合作伙伴Service接口
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
public interface IPurServiceAcceptancePartnerService extends IService<PurServiceAcceptancePartner>{
    /**
     * 查询服务采购验收单-合作伙伴
     * 
     * @param serviceAcceptancePartnerSid 服务采购验收单-合作伙伴ID
     * @return 服务采购验收单-合作伙伴
     */
    public PurServiceAcceptancePartner selectPurServiceAcceptancePartnerById(Long serviceAcceptancePartnerSid);

    /**
     * 查询服务采购验收单-合作伙伴列表
     * 
     * @param purServiceAcceptancePartner 服务采购验收单-合作伙伴
     * @return 服务采购验收单-合作伙伴集合
     */
    public List<PurServiceAcceptancePartner> selectPurServiceAcceptancePartnerList(PurServiceAcceptancePartner purServiceAcceptancePartner);

    /**
     * 新增服务采购验收单-合作伙伴
     * 
     * @param purServiceAcceptancePartner 服务采购验收单-合作伙伴
     * @return 结果
     */
    public int insertPurServiceAcceptancePartner(PurServiceAcceptancePartner purServiceAcceptancePartner);

    /**
     * 修改服务采购验收单-合作伙伴
     * 
     * @param purServiceAcceptancePartner 服务采购验收单-合作伙伴
     * @return 结果
     */
    public int updatePurServiceAcceptancePartner(PurServiceAcceptancePartner purServiceAcceptancePartner);

    /**
     * 批量删除服务采购验收单-合作伙伴
     * 
     * @param serviceAcceptancePartnerSids 需要删除的服务采购验收单-合作伙伴ID
     * @return 结果
     */
    public int deletePurServiceAcceptancePartnerByIds(List<Long> serviceAcceptancePartnerSids);

}

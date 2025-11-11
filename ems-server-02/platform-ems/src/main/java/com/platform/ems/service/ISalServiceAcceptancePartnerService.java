package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SalServiceAcceptancePartner;

/**
 * 服务销售验收单-合作伙伴Service接口
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
public interface ISalServiceAcceptancePartnerService extends IService<SalServiceAcceptancePartner>{
    /**
     * 查询服务销售验收单-合作伙伴
     * 
     * @param serviceAcceptancePartnerSid 服务销售验收单-合作伙伴ID
     * @return 服务销售验收单-合作伙伴
     */
    public SalServiceAcceptancePartner selectSalServiceAcceptancePartnerById(Long serviceAcceptancePartnerSid);

    /**
     * 查询服务销售验收单-合作伙伴列表
     * 
     * @param salServiceAcceptancePartner 服务销售验收单-合作伙伴
     * @return 服务销售验收单-合作伙伴集合
     */
    public List<SalServiceAcceptancePartner> selectSalServiceAcceptancePartnerList(SalServiceAcceptancePartner salServiceAcceptancePartner);

    /**
     * 新增服务销售验收单-合作伙伴
     * 
     * @param salServiceAcceptancePartner 服务销售验收单-合作伙伴
     * @return 结果
     */
    public int insertSalServiceAcceptancePartner(SalServiceAcceptancePartner salServiceAcceptancePartner);

    /**
     * 修改服务销售验收单-合作伙伴
     * 
     * @param salServiceAcceptancePartner 服务销售验收单-合作伙伴
     * @return 结果
     */
    public int updateSalServiceAcceptancePartner(SalServiceAcceptancePartner salServiceAcceptancePartner);

    /**
     * 批量删除服务销售验收单-合作伙伴
     * 
     * @param serviceAcceptancePartnerSids 需要删除的服务销售验收单-合作伙伴ID
     * @return 结果
     */
    public int deleteSalServiceAcceptancePartnerByIds(List<Long> serviceAcceptancePartnerSids);

}

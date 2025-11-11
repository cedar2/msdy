package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.SalServiceAcceptancePartnerMapper;
import com.platform.ems.domain.SalServiceAcceptancePartner;
import com.platform.ems.service.ISalServiceAcceptancePartnerService;

/**
 * 服务销售验收单-合作伙伴Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
@Service
@SuppressWarnings("all")
public class SalServiceAcceptancePartnerServiceImpl extends ServiceImpl<SalServiceAcceptancePartnerMapper,SalServiceAcceptancePartner>  implements ISalServiceAcceptancePartnerService {
    @Autowired
    private SalServiceAcceptancePartnerMapper salServiceAcceptancePartnerMapper;

    /**
     * 查询服务销售验收单-合作伙伴
     * 
     * @param serviceAcceptancePartnerSid 服务销售验收单-合作伙伴ID
     * @return 服务销售验收单-合作伙伴
     */
    @Override
    public SalServiceAcceptancePartner selectSalServiceAcceptancePartnerById(Long serviceAcceptancePartnerSid) {
        return salServiceAcceptancePartnerMapper.selectSalServiceAcceptancePartnerById(serviceAcceptancePartnerSid);
    }

    /**
     * 查询服务销售验收单-合作伙伴列表
     * 
     * @param salServiceAcceptancePartner 服务销售验收单-合作伙伴
     * @return 服务销售验收单-合作伙伴
     */
    @Override
    public List<SalServiceAcceptancePartner> selectSalServiceAcceptancePartnerList(SalServiceAcceptancePartner salServiceAcceptancePartner) {
        return salServiceAcceptancePartnerMapper.selectSalServiceAcceptancePartnerList(salServiceAcceptancePartner);
    }

    /**
     * 新增服务销售验收单-合作伙伴
     * 需要注意编码重复校验
     * @param salServiceAcceptancePartner 服务销售验收单-合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalServiceAcceptancePartner(SalServiceAcceptancePartner salServiceAcceptancePartner) {
        return salServiceAcceptancePartnerMapper.insert(salServiceAcceptancePartner);
    }

    /**
     * 修改服务销售验收单-合作伙伴
     * 
     * @param salServiceAcceptancePartner 服务销售验收单-合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalServiceAcceptancePartner(SalServiceAcceptancePartner salServiceAcceptancePartner) {
        return salServiceAcceptancePartnerMapper.updateById(salServiceAcceptancePartner);
    }

    /**
     * 批量删除服务销售验收单-合作伙伴
     * 
     * @param serviceAcceptancePartnerSids 需要删除的服务销售验收单-合作伙伴ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalServiceAcceptancePartnerByIds(List<Long> serviceAcceptancePartnerSids) {
        return salServiceAcceptancePartnerMapper.deleteBatchIds(serviceAcceptancePartnerSids);
    }


}

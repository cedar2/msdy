package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PurServiceAcceptancePartnerMapper;
import com.platform.ems.domain.PurServiceAcceptancePartner;
import com.platform.ems.service.IPurServiceAcceptancePartnerService;

/**
 * 服务采购验收单-合作伙伴Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
@Service
@SuppressWarnings("all")
public class PurServiceAcceptancePartnerServiceImpl extends ServiceImpl<PurServiceAcceptancePartnerMapper,PurServiceAcceptancePartner>  implements IPurServiceAcceptancePartnerService {
    @Autowired
    private PurServiceAcceptancePartnerMapper purServiceAcceptancePartnerMapper;

    /**
     * 查询服务采购验收单-合作伙伴
     * 
     * @param serviceAcceptancePartnerSid 服务采购验收单-合作伙伴ID
     * @return 服务采购验收单-合作伙伴
     */
    @Override
    public PurServiceAcceptancePartner selectPurServiceAcceptancePartnerById(Long serviceAcceptancePartnerSid) {
        return purServiceAcceptancePartnerMapper.selectPurServiceAcceptancePartnerById(serviceAcceptancePartnerSid);
    }

    /**
     * 查询服务采购验收单-合作伙伴列表
     * 
     * @param purServiceAcceptancePartner 服务采购验收单-合作伙伴
     * @return 服务采购验收单-合作伙伴
     */
    @Override
    public List<PurServiceAcceptancePartner> selectPurServiceAcceptancePartnerList(PurServiceAcceptancePartner purServiceAcceptancePartner) {
        return purServiceAcceptancePartnerMapper.selectPurServiceAcceptancePartnerList(purServiceAcceptancePartner);
    }

    /**
     * 新增服务采购验收单-合作伙伴
     * 需要注意编码重复校验
     * @param purServiceAcceptancePartner 服务采购验收单-合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurServiceAcceptancePartner(PurServiceAcceptancePartner purServiceAcceptancePartner) {
        return purServiceAcceptancePartnerMapper.insert(purServiceAcceptancePartner);
    }

    /**
     * 修改服务采购验收单-合作伙伴
     * 
     * @param purServiceAcceptancePartner 服务采购验收单-合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurServiceAcceptancePartner(PurServiceAcceptancePartner purServiceAcceptancePartner) {
        return purServiceAcceptancePartnerMapper.updateById(purServiceAcceptancePartner);
    }

    /**
     * 批量删除服务采购验收单-合作伙伴
     * 
     * @param serviceAcceptancePartnerSids 需要删除的服务采购验收单-合作伙伴ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurServiceAcceptancePartnerByIds(List<Long> serviceAcceptancePartnerSids) {
        return purServiceAcceptancePartnerMapper.deleteBatchIds(serviceAcceptancePartnerSids);
    }


}

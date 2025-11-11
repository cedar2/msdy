package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.SalSalesOrderPartnerMapper;
import com.platform.ems.domain.SalSalesOrderPartner;
import com.platform.ems.service.ISalSalesOrderPartnerService;

/**
 * 销售订单-合作伙伴Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
@Service
@SuppressWarnings("all")
public class SalSalesOrderPartnerServiceImpl extends ServiceImpl<SalSalesOrderPartnerMapper,SalSalesOrderPartner>  implements ISalSalesOrderPartnerService {
    @Autowired
    private SalSalesOrderPartnerMapper salSalesOrderPartnerMapper;

    /**
     * 查询销售订单-合作伙伴
     * 
     * @param salesOrderPartnerSid 销售订单-合作伙伴ID
     * @return 销售订单-合作伙伴
     */
    @Override
    public SalSalesOrderPartner selectSalSalesOrderPartnerById(Long salesOrderPartnerSid) {
        return salSalesOrderPartnerMapper.selectSalSalesOrderPartnerById(salesOrderPartnerSid);
    }

    /**
     * 查询销售订单-合作伙伴列表
     * 
     * @param salSalesOrderPartner 销售订单-合作伙伴
     * @return 销售订单-合作伙伴
     */
    @Override
    public List<SalSalesOrderPartner> selectSalSalesOrderPartnerList(SalSalesOrderPartner salSalesOrderPartner) {
        return salSalesOrderPartnerMapper.selectSalSalesOrderPartnerList(salSalesOrderPartner);
    }

    /**
     * 新增销售订单-合作伙伴
     * 需要注意编码重复校验
     * @param salSalesOrderPartner 销售订单-合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalSalesOrderPartner(SalSalesOrderPartner salSalesOrderPartner) {
        return salSalesOrderPartnerMapper.insert(salSalesOrderPartner);
    }

    /**
     * 修改销售订单-合作伙伴
     * 
     * @param salSalesOrderPartner 销售订单-合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalSalesOrderPartner(SalSalesOrderPartner salSalesOrderPartner) {
        return salSalesOrderPartnerMapper.updateById(salSalesOrderPartner);
    }

    /**
     * 批量删除销售订单-合作伙伴
     * 
     * @param salesOrderPartnerSids 需要删除的销售订单-合作伙伴ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSalesOrderPartnerByIds(List<Long> salesOrderPartnerSids) {
        return salSalesOrderPartnerMapper.deleteBatchIds(salesOrderPartnerSids);
    }


}

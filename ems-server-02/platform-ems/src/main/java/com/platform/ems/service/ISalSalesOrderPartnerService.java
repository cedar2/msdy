package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SalSalesOrderPartner;

/**
 * 销售订单-合作伙伴Service接口
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
public interface ISalSalesOrderPartnerService extends IService<SalSalesOrderPartner>{
    /**
     * 查询销售订单-合作伙伴
     * 
     * @param salesOrderPartnerSid 销售订单-合作伙伴ID
     * @return 销售订单-合作伙伴
     */
    public SalSalesOrderPartner selectSalSalesOrderPartnerById(Long salesOrderPartnerSid);

    /**
     * 查询销售订单-合作伙伴列表
     * 
     * @param salSalesOrderPartner 销售订单-合作伙伴
     * @return 销售订单-合作伙伴集合
     */
    public List<SalSalesOrderPartner> selectSalSalesOrderPartnerList(SalSalesOrderPartner salSalesOrderPartner);

    /**
     * 新增销售订单-合作伙伴
     * 
     * @param salSalesOrderPartner 销售订单-合作伙伴
     * @return 结果
     */
    public int insertSalSalesOrderPartner(SalSalesOrderPartner salSalesOrderPartner);

    /**
     * 修改销售订单-合作伙伴
     * 
     * @param salSalesOrderPartner 销售订单-合作伙伴
     * @return 结果
     */
    public int updateSalSalesOrderPartner(SalSalesOrderPartner salSalesOrderPartner);

    /**
     * 批量删除销售订单-合作伙伴
     * 
     * @param salesOrderPartnerSids 需要删除的销售订单-合作伙伴ID
     * @return 结果
     */
    public int deleteSalSalesOrderPartnerByIds(List<Long> salesOrderPartnerSids);

}

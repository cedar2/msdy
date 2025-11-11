package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalSalesOrderPartner;

/**
 * 销售订单-合作伙伴Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
public interface SalSalesOrderPartnerMapper  extends BaseMapper<SalSalesOrderPartner> {


    SalSalesOrderPartner selectSalSalesOrderPartnerById(Long salesOrderPartnerSid);

    List<SalSalesOrderPartner> selectSalSalesOrderPartnerList(SalSalesOrderPartner salSalesOrderPartner);

    /**
     * 添加多个
     * @param list List SalSalesOrderPartner
     * @return int
     */
    int inserts(@Param("list") List<SalSalesOrderPartner> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SalSalesOrderPartner
    * @return int
    */
    int updateAllById(SalSalesOrderPartner entity);

    /**
     * 更新多个
     * @param list List SalSalesOrderPartner
     * @return int
     */
    int updatesAllById(@Param("list") List<SalSalesOrderPartner> list);


    void deleteSalSalesOrderPartnerByIds(@Param("array")Long[] salesOrderSids);
}

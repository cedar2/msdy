package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalServiceAcceptancePartner;

/**
 * 服务销售验收单-合作伙伴Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
public interface SalServiceAcceptancePartnerMapper  extends BaseMapper<SalServiceAcceptancePartner> {


    SalServiceAcceptancePartner selectSalServiceAcceptancePartnerById(Long serviceAcceptancePartnerSid);

    List<SalServiceAcceptancePartner> selectSalServiceAcceptancePartnerList(SalServiceAcceptancePartner salServiceAcceptancePartner);

    /**
     * 添加多个
     * @param list List SalServiceAcceptancePartner
     * @return int
     */
    int inserts(@Param("list") List<SalServiceAcceptancePartner> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SalServiceAcceptancePartner
    * @return int
    */
    int updateAllById(SalServiceAcceptancePartner entity);

    /**
     * 更新多个
     * @param list List SalServiceAcceptancePartner
     * @return int
     */
    int updatesAllById(@Param("list") List<SalServiceAcceptancePartner> list);


    void deleteServiceAcceptancePartnerByIds(@Param("array") Long[] serviceAcceptanceSids);
}

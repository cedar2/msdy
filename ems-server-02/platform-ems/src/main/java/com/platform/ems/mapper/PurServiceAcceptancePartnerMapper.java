package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurServiceAcceptancePartner;

/**
 * 服务采购验收单-合作伙伴Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
public interface PurServiceAcceptancePartnerMapper  extends BaseMapper<PurServiceAcceptancePartner> {


    PurServiceAcceptancePartner selectPurServiceAcceptancePartnerById(Long serviceAcceptancePartnerSid);

    List<PurServiceAcceptancePartner> selectPurServiceAcceptancePartnerList(PurServiceAcceptancePartner purServiceAcceptancePartner);

    /**
     * 添加多个
     * @param list List PurServiceAcceptancePartner
     * @return int
     */
    int inserts(@Param("list") List<PurServiceAcceptancePartner> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurServiceAcceptancePartner
    * @return int
    */
    int updateAllById(PurServiceAcceptancePartner entity);

    /**
     * 更新多个
     * @param list List PurServiceAcceptancePartner
     * @return int
     */
    int updatesAllById(@Param("list") List<PurServiceAcceptancePartner> list);


    void deletePurServiceAcceptancePartnerByIds(@Param("array") Long[] serviceAcceptanceSids);
}

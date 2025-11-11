package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurPurchaseOrderPartner;

/**
 * 采购订单-合作伙伴Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
public interface PurPurchaseOrderPartnerMapper  extends BaseMapper<PurPurchaseOrderPartner> {


    PurPurchaseOrderPartner selectPurPurchaseOrderPartnerById(Long purchaseOrderPartnerSid);

    List<PurPurchaseOrderPartner> selectPurPurchaseOrderPartnerList(PurPurchaseOrderPartner purPurchaseOrderPartner);

    /**
     * 添加多个
     * @param list List PurPurchaseOrderPartner
     * @return int
     */
    int inserts(@Param("list") List<PurPurchaseOrderPartner> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurPurchaseOrderPartner
    * @return int
    */
    int updateAllById(PurPurchaseOrderPartner entity);

    /**
     * 更新多个
     * @param list List PurPurchaseOrderPartner
     * @return int
     */
    int updatesAllById(@Param("list") List<PurPurchaseOrderPartner> list);


    void deletePurPurchaseOrderPartnerByIds(@Param("array")Long[] purchaseOrderSids);
}

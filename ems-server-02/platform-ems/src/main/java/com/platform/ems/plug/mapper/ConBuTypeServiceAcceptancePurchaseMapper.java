package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeServiceAcceptancePurchase;

/**
 * 业务类型_服务采购验收单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeServiceAcceptancePurchaseMapper  extends BaseMapper<ConBuTypeServiceAcceptancePurchase> {


    ConBuTypeServiceAcceptancePurchase selectConBuTypeServiceAcceptancePurchaseById(Long sid);

    List<ConBuTypeServiceAcceptancePurchase> selectConBuTypeServiceAcceptancePurchaseList(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase);

    /**
     * 添加多个
     * @param list List ConBuTypeServiceAcceptancePurchase
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeServiceAcceptancePurchase> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeServiceAcceptancePurchase
    * @return int
    */
    int updateAllById(ConBuTypeServiceAcceptancePurchase entity);

    /**
     * 更新多个
     * @param list List ConBuTypeServiceAcceptancePurchase
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeServiceAcceptancePurchase> list);


}

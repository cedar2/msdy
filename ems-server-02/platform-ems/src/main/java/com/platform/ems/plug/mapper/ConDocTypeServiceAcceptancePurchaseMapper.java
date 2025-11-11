package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeServiceAcceptancePurchase;

/**
 * 单据类型_服务采购验收单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeServiceAcceptancePurchaseMapper  extends BaseMapper<ConDocTypeServiceAcceptancePurchase> {


    ConDocTypeServiceAcceptancePurchase selectConDocTypeServiceAcceptancePurchaseById(Long sid);

    List<ConDocTypeServiceAcceptancePurchase> selectConDocTypeServiceAcceptancePurchaseList(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase);

    /**
     * 添加多个
     * @param list List ConDocTypeServiceAcceptancePurchase
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeServiceAcceptancePurchase> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeServiceAcceptancePurchase
    * @return int
    */
    int updateAllById(ConDocTypeServiceAcceptancePurchase entity);

    /**
     * 更新多个
     * @param list List ConDocTypeServiceAcceptancePurchase
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeServiceAcceptancePurchase> list);


}

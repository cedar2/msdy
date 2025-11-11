package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvOwnerMaterialSettle;

/**
 * 甲供料结算单Mapper接口
 * 
 * @author c
 * @date 2021-09-13
 */
public interface InvOwnerMaterialSettleMapper  extends BaseMapper<InvOwnerMaterialSettle> {


    InvOwnerMaterialSettle selectInvOwnerMaterialSettleById(Long settleSid);

    List<InvOwnerMaterialSettle> selectInvOwnerMaterialSettleList(InvOwnerMaterialSettle invOwnerMaterialSettle);

    /**
     * 添加多个
     * @param list List InvOwnerMaterialSettle
     * @return int
     */
    int inserts(@Param("list") List<InvOwnerMaterialSettle> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvOwnerMaterialSettle
    * @return int
    */
    int updateAllById(InvOwnerMaterialSettle entity);

    /**
     * 更新多个
     * @param list List InvOwnerMaterialSettle
     * @return int
     */
    int updatesAllById(@Param("list") List<InvOwnerMaterialSettle> list);


}

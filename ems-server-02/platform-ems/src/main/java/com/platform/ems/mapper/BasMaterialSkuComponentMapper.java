package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.response.external.BasMaterialSkuComponentExternal;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasMaterialSkuComponent;

/**
 * 商品SKU实测成分Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-20
 */
public interface BasMaterialSkuComponentMapper  extends BaseMapper<BasMaterialSkuComponent> {


    BasMaterialSkuComponent selectBasMaterialSkuComponentById(String clientId);

    List<BasMaterialSkuComponentExternal> selectForExternalList(Long materialSid);

    List<BasMaterialSkuComponent> selectBasMaterialSkuComponentList(BasMaterialSkuComponent basMaterialSkuComponent);

    /**
     * 添加多个
     * @param list List BasMaterialSkuComponent
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialSkuComponent> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasMaterialSkuComponent
    * @return int
    */
    int updateAllById(BasMaterialSkuComponent entity);

    /**
     * 更新多个
     * @param list List BasMaterialSkuComponent
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialSkuComponent> list);


    void deleteMaterialSkuComponentById(Long materialSid);

    void deleteBasMaterialSkuComponentByIds(@Param("list")List<Long> materialSids);

}

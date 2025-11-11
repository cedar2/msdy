package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.response.external.BasMaterialSkuDownExternal;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasMaterialSkuDown;

/**
 * 商品SKU羽绒充绒量Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-20
 */
public interface BasMaterialSkuDownMapper  extends BaseMapper<BasMaterialSkuDown> {


    BasMaterialSkuDown selectBasMaterialSkuDownById(String clientId);

    List<BasMaterialSkuDownExternal> selectForExternalList(Long materialSid);

    List<BasMaterialSkuDown> selectBasMaterialSkuDownList(BasMaterialSkuDown basMaterialSkuDown);

    /**
     * 添加多个
     * @param list List BasMaterialSkuDown
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialSkuDown> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasMaterialSkuDown
    * @return int
    */
    int updateAllById(BasMaterialSkuDown entity);

    /**
     * 更新多个
     * @param list List BasMaterialSkuDown
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialSkuDown> list);


    void deleteMaterialSkuDownById(Long materialSid);

    void deleteBasMaterialSkuDownByIds(@Param("list")List<Long> materialSids);
}

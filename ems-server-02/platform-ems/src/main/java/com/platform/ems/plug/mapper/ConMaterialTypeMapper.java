package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConMovementType;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConMaterialType;

/**
 * 物料类型Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConMaterialTypeMapper  extends BaseMapper<ConMaterialType> {


    ConMaterialType selectConMaterialTypeById(Long sid);

    List<ConMaterialType> selectConMaterialTypeList(ConMaterialType conMaterialType);

    /**
     * 添加多个
     * @param list List ConMaterialType
     * @return int
     */
    int inserts(@Param("list") List<ConMaterialType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConMaterialType
    * @return int
    */
    int updateAllById(ConMaterialType entity);

    /**
     * 更新多个
     * @param list List ConMaterialType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConMaterialType> list);

    /** 获取下拉列表 */
    List<ConMaterialType> getConMaterialTypeList();

    /** 获取下拉列表 */
    List<ConMaterialType> getList(ConMaterialType materialType);
}

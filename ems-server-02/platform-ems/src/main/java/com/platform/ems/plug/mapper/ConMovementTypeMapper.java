package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConMovementType;

/**
 * 作业类型(移动类型)Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-20
 */
public interface ConMovementTypeMapper extends BaseMapper<ConMovementType> {


    ConMovementType selectConMovementTypeById(Long sid);

    List<ConMovementType> selectConMovementTypeList(ConMovementType conMovementType);

    public List<ConMovementType> getList(ConMovementType movementType);

    /**
     * 添加多个
     *
     * @param list List ConMovementType
     * @return int
     */
    int inserts(@Param("list") List<ConMovementType> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConMovementType
     * @return int
     */
    int updateAllById(ConMovementType entity);

    /**
     * 更新多个
     *
     * @param list List ConMovementType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConMovementType> list);

    List<ConMovementType> conMovementTypeList(ConMovementType conMovementType);

    ConMovementType conMovementTypeById(Long sid);

    List<ConMovementType> getConMovementTypeList();

    List<ConMovementType> getMovementList(ConMovementType conMovementType);
}

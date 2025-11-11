package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasGoodsShelfMaterialClass;
import com.platform.common.core.domain.entity.ConMaterialClass;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 货架档案-物料分类明细Mapper接口
 *
 * @author linhongwei
 * @date 2023-02-02
 */
public interface BasGoodsShelfMaterialClassMapper extends BaseMapper<BasGoodsShelfMaterialClass> {


    BasGoodsShelfMaterialClass selectBasGoodsShelfMaterialClassById(Long goodsShelfMaterialClassSid);

    List<BasGoodsShelfMaterialClass> selectBasGoodsShelfMaterialClassList(BasGoodsShelfMaterialClass basGoodsShelfMaterialClass);

    /**
     * 添加多个
     *
     * @param list List BasGoodsShelfMaterialClass
     * @return int
     */
    int inserts(@Param("list") List<BasGoodsShelfMaterialClass> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasGoodsShelfMaterialClass
     * @return int
     */
    int updateAllById(BasGoodsShelfMaterialClass entity);

    /**
     * 更新多个
     *
     * @param list List BasGoodsShelfMaterialClass
     * @return int
     */
    int updatesAllById(@Param("list") List<BasGoodsShelfMaterialClass> list);


    default List<Long> selectClassSidList(Long goodsShelfSid) {
        List<BasGoodsShelfMaterialClass> list = this.selectList(
                new LambdaQueryWrapper<BasGoodsShelfMaterialClass>()
                        .eq(BasGoodsShelfMaterialClass::getGoodsShelfSid,
                            goodsShelfSid)
                        .select(BasGoodsShelfMaterialClass::getMaterialClassSid)
        );
        return list.stream()
                   .map(BasGoodsShelfMaterialClass::getMaterialClassSid)
                   .collect(Collectors.toList());
    }

    List<ConMaterialClass> selectMaterialClassList(Long goodsShelfSid);
}

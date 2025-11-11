package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecBomSizeQuantity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物料清单（BOM）组件具体尺码用量Mapper接口
 *
 * @author qhq
 * @date 2021-03-15
 */
public interface TecBomSizeQuantityMapper  extends BaseMapper<TecBomSizeQuantity> {


    List<TecBomSizeQuantity>  selectTecBomSizeQuantityById(Long materialSid);

    List<TecBomSizeQuantity> selectTecBomSizeQuantityList(TecBomSizeQuantity tecBomSizeQuantity);

    /**
     * 添加多个
     * @param list List TecBomSizeQuantity
     * @return int
     */
    int inserts(@Param("list") List<TecBomSizeQuantity> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity TecBomSizeQuantity
    * @return int
    */
    int updateAllById(TecBomSizeQuantity entity);

    /**
     * 更新多个
     * @param list List TecBomSizeQuantity
     * @return int
     */
    int updatesAllById(@Param("list") List<TecBomSizeQuantity> list);


}

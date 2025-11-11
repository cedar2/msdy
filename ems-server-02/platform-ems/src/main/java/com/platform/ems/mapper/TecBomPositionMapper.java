package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.TecBomPosition;


/**
 * BOM部位档案Mapper接口
 * 
 * @author zhuangyz
 * @date 2022-07-07
 */
public interface TecBomPositionMapper  extends BaseMapper<TecBomPosition> {


    TecBomPosition selectTecBomPositionById(long sid);

    List<TecBomPosition> selectTecBomPositionList(TecBomPosition tecBomPosition);

    /**
     * 添加多个
     * @param list List TecBomPosition
     * @return int
     */
    int inserts(@Param("list") List<TecBomPosition> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity TecBomPosition
    * @return int
    */
    int updateAllById(TecBomPosition entity);

    /**
     * 更新多个
     * @param list List TecBomPosition
     * @return int
     */
    int updatesAllById(@Param("list") List<TecBomPosition> list);


}

package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasPlantProdLine;

/**
 * 工厂-生产线信息Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-27
 */
public interface BasPlantProdLineMapper  extends BaseMapper<BasPlantProdLine> {


    BasPlantProdLine selectBasPlantProdLineById(Long productLineSid);

    List<BasPlantProdLine> selectBasPlantProdLineList(BasPlantProdLine basPlantProdLine);

    /**
     * 添加多个
     * @param list List BasPlantProdLine
     * @return int
     */
    int inserts(@Param("list") List<BasPlantProdLine> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasPlantProdLine
    * @return int
    */
    int updateAllById(BasPlantProdLine entity);

    /**
     * 更新多个
     * @param list List BasPlantProdLine
     * @return int
     */
    int updatesAllById(@Param("list") List<BasPlantProdLine> list);


}

package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConPayDimension;

/**
 * 付款维度Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConPayDimensionMapper  extends BaseMapper<ConPayDimension> {


    ConPayDimension selectConPayDimensionById(Long sid);

    List<ConPayDimension> selectConPayDimensionList(ConPayDimension conPayDimension);

    /**
     * 添加多个
     * @param list List ConPayDimension
     * @return int
     */
    int inserts(@Param("list") List<ConPayDimension> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConPayDimension
    * @return int
    */
    int updateAllById(ConPayDimension entity);

    /**
     * 更新多个
     * @param list List ConPayDimension
     * @return int
     */
    int updatesAllById(@Param("list") List<ConPayDimension> list);


}

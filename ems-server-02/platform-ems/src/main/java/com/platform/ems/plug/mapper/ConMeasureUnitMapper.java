package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConMeasureUnit;

/**
 * 计量单位Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-20
 */
public interface ConMeasureUnitMapper  extends BaseMapper<ConMeasureUnit> {


    ConMeasureUnit selectConMeasureUnitById(Long sid);

    List<ConMeasureUnit> selectConMeasureUnitList(ConMeasureUnit conMeasureUnit);

    /**
     * 添加多个
     * @param list List ConMeasureUnit
     * @return int
     */
    int inserts(@Param("list") List<ConMeasureUnit> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConMeasureUnit
    * @return int
    */
    int updateAllById(ConMeasureUnit entity);

    /**
     * 更新多个
     * @param list List ConMeasureUnit
     * @return int
     */
    int updatesAllById(@Param("list") List<ConMeasureUnit> list);

    /** 获取下拉列表 */
    List<ConMeasureUnit> getConMeasureUnitList();
}

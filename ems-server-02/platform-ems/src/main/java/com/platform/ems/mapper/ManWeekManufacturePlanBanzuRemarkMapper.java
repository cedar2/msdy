package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManWeekManufacturePlanBanzuRemark;

/**
 * 生产周计划-班组总结Mapper接口
 * 
 * @author linhongwei
 * @date 2022-08-09
 */
public interface ManWeekManufacturePlanBanzuRemarkMapper  extends BaseMapper<ManWeekManufacturePlanBanzuRemark> {


    List<ManWeekManufacturePlanBanzuRemark> selectManWeekManufacturePlanBanzuRemarkById(Long weekManufacturePlanSid);

    List<ManWeekManufacturePlanBanzuRemark> selectManWeekManufacturePlanBanzuRemarkList(ManWeekManufacturePlanBanzuRemark manWeekManufacturePlanBanzuRemark);

    /**
     * 添加多个
     * @param list List ManWeekManufacturePlanBanzuRemark
     * @return int
     */
    int inserts(@Param("list") List<ManWeekManufacturePlanBanzuRemark> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManWeekManufacturePlanBanzuRemark
    * @return int
    */
    int updateAllById(ManWeekManufacturePlanBanzuRemark entity);

    /**
     * 更新多个
     * @param list List ManWeekManufacturePlanBanzuRemark
     * @return int
     */
    int updatesAllById(@Param("list") List<ManWeekManufacturePlanBanzuRemark> list);


}

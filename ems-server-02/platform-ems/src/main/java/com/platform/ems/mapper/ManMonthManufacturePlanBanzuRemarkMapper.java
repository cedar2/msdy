package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManMonthManufacturePlanBanzuRemark;

/**
 * 生产月计划-班组总结Mapper接口
 * 
 * @author linhongwei
 * @date 2022-08-08
 */
public interface ManMonthManufacturePlanBanzuRemarkMapper  extends BaseMapper<ManMonthManufacturePlanBanzuRemark> {


    List<ManMonthManufacturePlanBanzuRemark> selectManMonthManufacturePlanBanzuRemarkById(Long banzuRemarkSid);

    List<ManMonthManufacturePlanBanzuRemark> selectManMonthManufacturePlanBanzuRemarkList(ManMonthManufacturePlanBanzuRemark manMonthManufacturePlanBanzuRemark);

    /**
     * 添加多个
     * @param list List ManMonthManufacturePlanBanzuRemark
     * @return int
     */
    int inserts(@Param("list") List<ManMonthManufacturePlanBanzuRemark> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManMonthManufacturePlanBanzuRemark
    * @return int
    */
    int updateAllById(ManMonthManufacturePlanBanzuRemark entity);

    /**
     * 更新多个
     * @param list List ManMonthManufacturePlanBanzuRemark
     * @return int
     */
    int updatesAllById(@Param("list") List<ManMonthManufacturePlanBanzuRemark> list);


}

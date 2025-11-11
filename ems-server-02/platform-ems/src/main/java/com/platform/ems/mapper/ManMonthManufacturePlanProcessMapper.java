package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManMonthManufacturePlanProcess;

/**
 * 生产月计划-工序明细Mapper接口
 * 
 * @author linhongwei
 * @date 2022-08-08
 */
public interface ManMonthManufacturePlanProcessMapper  extends BaseMapper<ManMonthManufacturePlanProcess> {


    ManMonthManufacturePlanProcess selectManMonthManufacturePlanProcessById(Long manufacturePlanProcessSid);

    List<ManMonthManufacturePlanProcess> selectManMonthManufacturePlanProcessList(ManMonthManufacturePlanProcess manMonthManufacturePlanProcess);

    /**
     * 添加多个
     * @param list List ManMonthManufacturePlanProcess
     * @return int
     */
    int inserts(@Param("list") List<ManMonthManufacturePlanProcess> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManMonthManufacturePlanProcess
    * @return int
    */
    int updateAllById(ManMonthManufacturePlanProcess entity);

    /**
     * 更新多个
     * @param list List ManMonthManufacturePlanProcess
     * @return int
     */
    int updatesAllById(@Param("list") List<ManMonthManufacturePlanProcess> list);


}

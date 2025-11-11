package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DevProductPlan;

/**
 * 品类规划信息Mapper接口
 * 
 * @author qhq
 * @date 2021-11-08
 */
public interface DevProductPlanMapper  extends BaseMapper<DevProductPlan> {


    DevProductPlan selectDevProductPlanById (Long productPlanSid);

    List<DevProductPlan> selectDevProductPlanList (DevProductPlan devProductPlan);

    /**
     * 添加多个
     * @param list List DevProductPlan
     * @return int
     */
    int inserts (@Param("list") List<DevProductPlan> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DevProductPlan
    * @return int
    */
    int updateAllById (DevProductPlan entity);

    /**
     * 更新多个
     * @param list List DevProductPlan
     * @return int
     */
    int updatesAllById (@Param("list") List<DevProductPlan> list);


}

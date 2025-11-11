package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DevProductPlanAttach;

/**
 * 品类规划信息-附件Mapper接口
 * 
 * @author qhq
 * @date 2021-11-08
 */
public interface DevProductPlanAttachMapper  extends BaseMapper<DevProductPlanAttach> {


    DevProductPlanAttach selectDevProductPlanAttachById (Long attachmentSid);

    List<DevProductPlanAttach> selectDevProductPlanAttachList (DevProductPlanAttach devProductPlanAttach);

    /**
     * 添加多个
     * @param list List DevProductPlanAttach
     * @return int
     */
    int inserts (@Param("list") List<DevProductPlanAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DevProductPlanAttach
    * @return int
    */
    int updateAllById (DevProductPlanAttach entity);

    /**
     * 更新多个
     * @param list List DevProductPlanAttach
     * @return int
     */
    int updatesAllById (@Param("list") List<DevProductPlanAttach> list);


}

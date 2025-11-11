package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DevDevelopPlanAttach;

/**
 * 开发计划-附件Mapper接口
 * 
 * @author chenkw
 * @date 2022-12-08
 */
public interface DevDevelopPlanAttachMapper  extends BaseMapper<DevDevelopPlanAttach> {


    DevDevelopPlanAttach selectDevDevelopPlanAttachById(Long developPlanAttachSid);

    List<DevDevelopPlanAttach> selectDevDevelopPlanAttachList(DevDevelopPlanAttach devDevelopPlanAttach);

    /**
     * 添加多个
     * @param list List DevDevelopPlanAttach
     * @return int
     */
    int inserts(@Param("list") List<DevDevelopPlanAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DevDevelopPlanAttach
    * @return int
    */
    int updateAllById(DevDevelopPlanAttach entity);

    /**
     * 更新多个
     * @param list List DevDevelopPlanAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<DevDevelopPlanAttach> list);


}

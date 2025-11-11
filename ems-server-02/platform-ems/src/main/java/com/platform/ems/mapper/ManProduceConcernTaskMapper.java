package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManProduceConcernTask;

/**
 * 生产关注事项Mapper接口
 * 
 * @author zhuangyz
 * @date 2022-08-01
 */
public interface ManProduceConcernTaskMapper  extends BaseMapper<ManProduceConcernTask> {


    ManProduceConcernTask selectManProduceConcernTaskById(Long concernTaskSid);

    List<ManProduceConcernTask> selectManProduceConcernTaskList(ManProduceConcernTask manProduceConcernTask);

    /**
     * 添加多个
     * @param list List ManProduceConcernTask
     * @return int
     */
    int inserts(@Param("list") List<ManProduceConcernTask> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManProduceConcernTask
    * @return int
    */
    int updateAllById(ManProduceConcernTask entity);

    /**
     * 更新多个
     * @param list List ManProduceConcernTask
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProduceConcernTask> list);


}

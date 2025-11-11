package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.response.ManProduceConcernTaskResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManProduceConcernTaskGroup;

/**
 * 生产关注事项组Mapper接口
 * 
 * @author zhuangyz
 * @date 2022-08-02
 */
public interface ManProduceConcernTaskGroupMapper  extends BaseMapper<ManProduceConcernTaskGroup> {


    List<ManProduceConcernTaskResponse> addItem(@Param("concernTaskGroupSid") Long concernTaskGroupSid,@Param("manufactureOrderCode")Long manufactureOrderCode);
    ManProduceConcernTaskGroup selectManProduceConcernTaskGroupById(Long concernTaskGroupSid);

    List<ManProduceConcernTaskGroup> selectManProduceConcernTaskGroupList(ManProduceConcernTaskGroup manProduceConcernTaskGroup);

    /**
     * 添加多个
     * @param list List ManProduceConcernTaskGroup
     * @return int
     */
    int inserts(@Param("list") List<ManProduceConcernTaskGroup> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManProduceConcernTaskGroup
    * @return int
    */
    int updateAllById(ManProduceConcernTaskGroup entity);

    /**
     * 更新多个
     * @param list List ManProduceConcernTaskGroup
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProduceConcernTaskGroup> list);


}

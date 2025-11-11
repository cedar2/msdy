package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.ManProduceConcernTaskGroupRequest;
import com.platform.ems.domain.dto.response.ManProduceConcernTaskGroupResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManProduceConcernTaskGroupItem;

/**
 * 生产关注事项组-明细Mapper接口
 * 
 * @author zhuangyz
 * @date 2022-08-02
 */
public interface ManProduceConcernTaskGroupItemMapper  extends BaseMapper<ManProduceConcernTaskGroupItem> {


    ManProduceConcernTaskGroupItem selectManProduceConcernTaskGroupItemById(Long concernTaskGroupItemSid);
    List<ManProduceConcernTaskGroupResponse> getReport(ManProduceConcernTaskGroupRequest manProduceConcernTaskGroupRequest);
    List<ManProduceConcernTaskGroupItem> selectManProduceConcernTaskGroupItemList(ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem);

    /**
     * 添加多个
     * @param list List ManProduceConcernTaskGroupItem
     * @return int
     */
    int inserts(@Param("list") List<ManProduceConcernTaskGroupItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManProduceConcernTaskGroupItem
    * @return int
    */
    int updateAllById(ManProduceConcernTaskGroupItem entity);

    /**
     * 更新多个
     * @param list List ManProduceConcernTaskGroupItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProduceConcernTaskGroupItem> list);


}

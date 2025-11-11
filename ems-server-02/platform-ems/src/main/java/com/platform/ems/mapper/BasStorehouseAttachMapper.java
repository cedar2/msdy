package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasStorehouseAttach;

/**
 * 仓库档案-附件Mapper接口
 *
 * @author chenkw
 * @date 2021-09-15
 */
public interface BasStorehouseAttachMapper  extends BaseMapper<BasStorehouseAttach> {


    BasStorehouseAttach selectBasStorehouseAttachById(Long attachmentSid);

    List<BasStorehouseAttach> selectBasStorehouseAttachList(BasStorehouseAttach basStorehouseAttach);

    /**
     * 添加多个
     * @param list List BasStorehouseAttach
     * @return int
     */
    int inserts(@Param("list") List<BasStorehouseAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity BasStorehouseAttach
     * @return int
     */
    int updateAllById(BasStorehouseAttach entity);

    /**
     * 更新多个
     * @param list List BasStorehouseAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<BasStorehouseAttach> list);


}
package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.BasSkuGroup;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.TecModelPositionGroup;

/**
 * 版型部位组档案Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-02
 */
public interface TecModelPositionGroupMapper  extends BaseMapper<TecModelPositionGroup> {


    TecModelPositionGroup selectTecModelPositionGroupById(Long groupSid);

    List<TecModelPositionGroup> selectTecModelPositionGroupList(TecModelPositionGroup tecModelPositionGroup);

    List<TecModelPositionGroup> getList();

    /**
     * 添加多个
     * @param list List TecModelPositionGroup
     * @return int
     */
    int inserts(@Param("list") List<TecModelPositionGroup> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity TecModelPositionGroup
    * @return int
    */
    int updateAllById(TecModelPositionGroup entity);

    /**
     * 更新多个
     * @param list List TecModelPositionGroup
     * @return int
     */
    int updatesAllById(@Param("list") List<TecModelPositionGroup> list);


}

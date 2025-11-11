package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManProcessRouteItem;

/**
 * 工艺路线-工序Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-26
 */
public interface ManProcessRouteItemMapper  extends BaseMapper<ManProcessRouteItem> {


    List<ManProcessRouteItem> selectManProcessRouteItemById(Long processRouteProcessSid);

    List<ManProcessRouteItem> selectManProcessRouteItemList(ManProcessRouteItem manProcessRouteItem);

    /**
     * 添加多个
     * @param list List ManProcessRouteItem
     * @return int
     */
    int inserts(@Param("list") List<ManProcessRouteItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManProcessRouteItem
    * @return int
    */
    int updateAllById(ManProcessRouteItem entity);

    /**
     * 更新多个
     * @param list List ManProcessRouteItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProcessRouteItem> list);

    /**
     * 工艺路线明细报表
     */
    List<ManProcessRouteItem> getItemList(ManProcessRouteItem manProcessRouteItem);
}

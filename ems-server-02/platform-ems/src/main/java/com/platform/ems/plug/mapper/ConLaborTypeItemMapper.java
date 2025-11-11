package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConLaborTypeItem;
import org.apache.ibatis.annotations.Param;

/**
 * 工价类型/工价费用项对照Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-10
 */
public interface ConLaborTypeItemMapper  extends BaseMapper<ConLaborTypeItem> {


    ConLaborTypeItem selectConLaborTypeItemById(Long laborTypeItemSid);

    /*
     *  (主表详情的明细页面按序号+名称排序)
     */
    List<ConLaborTypeItem> selectConLaborTypeItemList(ConLaborTypeItem conLaborTypeItem);

    /**
     * 查询工价类型/工价费用项对照列表  (查询页面按工价类型+编码排序)
     * @author chenkw
     * @param conLaborTypeItem 工价类型/工价费用项对照
     * @return 工价类型/工价费用项对照
     */
    List<ConLaborTypeItem> selectTypeItemList(ConLaborTypeItem conLaborTypeItem);

    /**
     * 添加多个
     * @param list List ConLaborTypeItem
     * @return int
     */
    int inserts(@Param("list") List<ConLaborTypeItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConLaborTypeItem
    * @return int
    */
    int updateAllById(ConLaborTypeItem entity);

    /**
     * 更新多个
     * @param list List ConLaborTypeItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ConLaborTypeItem> list);

    /**
     * 下拉框列表
     */
    List<ConLaborTypeItem> getConLaborTypeItemList();
}

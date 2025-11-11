package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SamOsbSampleReimburseItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 外采样报销单-明细Mapper接口
 *
 * @author qhq
 * @date 2021-12-28
 */
public interface SamOsbSampleReimburseItemMapper  extends BaseMapper<SamOsbSampleReimburseItem> {


    SamOsbSampleReimburseItem selectSamOsbSampleReimburseItemById (Long reimburseItemSid);

    List<SamOsbSampleReimburseItem> selectSamOsbSampleReimburseItemList (SamOsbSampleReimburseItem samOsbSampleReimburseItem);

    /**
     * 添加多个
     * @param list List SamOsbSampleReimburseItem
     * @return int
     */
    int inserts (@Param("list") List<SamOsbSampleReimburseItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SamOsbSampleReimburseItem
    * @return int
    */
    int updateAllById (SamOsbSampleReimburseItem entity);

    /**
     * 更新多个
     * @param list List SamOsbSampleReimburseItem
     * @return int
     */
    int updatesAllById (@Param("list") List<SamOsbSampleReimburseItem> list);


}

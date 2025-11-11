package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.StaffCompleteSummary;
import com.platform.ems.domain.StepFinishDetail;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManProcessStepCompleteRecordItem;

/**
 * 商品道序完成量台账-明细Mapper接口
 *
 * @author chenkw
 * @date 2022-10-20
 */
public interface ManProcessStepCompleteRecordItemMapper extends BaseMapper<ManProcessStepCompleteRecordItem> {


    ManProcessStepCompleteRecordItem selectManProcessStepCompleteRecordItemById(Long stepCompleteRecordItemSid);

    List<ManProcessStepCompleteRecordItem> selectManProcessStepCompleteRecordItemList(ManProcessStepCompleteRecordItem manProcessStepCompleteRecordItem);

    /**
     * 添加多个
     *
     * @param list List ManProcessStepCompleteRecordItem
     * @return int
     */
    int inserts(@Param("list") List<ManProcessStepCompleteRecordItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManProcessStepCompleteRecordItem
     * @return int
     */
    int updateAllById(ManProcessStepCompleteRecordItem entity);

    /**
     * 更新多个
     *
     * @param list List ManProcessStepCompleteRecordItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProcessStepCompleteRecordItem> list);

    /**
     * 道序完成台账明细报表数据查询 报表的总数 手动分页处理
     * @param stepFinishDetail
     * @return
     */
    long selectManProcessStepFinishDetailListCount(StepFinishDetail stepFinishDetail);

    /**
     * 道序完成台账明细报表数据查询
     * @param stepFinishDetail
     * @return
     */
    @SqlParser(filter=true)
    List<StepFinishDetail> selectManProcessStepFinishDetailList(StepFinishDetail stepFinishDetail);

    /**
     * 员工完成量汇总数据 报表的总数 手动分页处理
     * @param staffCompleteSummary
     * @return
     */
    long selectStaffCompleteSummaryCount(StaffCompleteSummary staffCompleteSummary);

    /**
     * 员工完成量汇总数据
     * @param staffCompleteSummary
     * @return
     */
    @SqlParser(filter=true)
    List<StaffCompleteSummary> selectStaffCompleteSummary(StaffCompleteSummary staffCompleteSummary);
}

package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.PurInquiryItem;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourceInquiryItem;

/**
 * 加工询价单明细Mapper接口
 * 
 * @author chenkw
 * @date 2022-01-11
 */
public interface PurOutsourceInquiryItemMapper  extends BaseMapper<PurOutsourceInquiryItem> {


    PurOutsourceInquiryItem selectPurOutsourceInquiryItemById(Long outsourceInquiryItemSid);

    List<PurOutsourceInquiryItem> selectPurOutsourceInquiryItemList(PurOutsourceInquiryItem purOutsourceInquiryItem);

    /**
     * 添加多个
     * @param list List PurOutsourceInquiryItem
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourceInquiryItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurOutsourceInquiryItem
    * @return int
    */
    int updateAllById(PurOutsourceInquiryItem entity);

    /**
     * 更新多个
     * @param list List PurOutsourceInquiryItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourceInquiryItem> list);

    /**
     * 通过主表sid查主表下明细
     * @param outsourceInquirySid Long outsourceInquirySid
     * @return int
     */
    List<PurOutsourceInquiryItem> selectPurOutsourceInquiryItemListById(Long outsourceInquirySid);

    /**
     * 通过批量主表sid批量删除明细
     * @param outsourceInquirySid Long outsourceInquirySid
     * @return int
     */
    int deletePurOutsourceInquiryItemByInquirySids(@Param("outsourceInquirySid") List<Long> outsourceInquirySid);

    /**
     * 通过批量主表sid查询所有明细表SID
     * @param outsourceInquirySid Long outsourceInquirySid
     * @return int
     */
    List<Long> selectPurOutsourceInquiryItemSidListById(@Param("outsourceInquirySid") Long[] outsourceInquirySid);

    /**
     * 明细报表查询
     * @param purOutsourceInquiryItem getReportForm purOutsourceInquiryItem
     * @return List
     */
    List<PurOutsourceInquiryItem> getReportForm(PurOutsourceInquiryItem purOutsourceInquiryItem);
}

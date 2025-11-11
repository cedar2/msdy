package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurInquiryItem;

/**
 * 物料询价单明细Mapper接口
 * 
 * @author chenkw
 * @date 2022-01-11
 */
public interface PurInquiryItemMapper  extends BaseMapper<PurInquiryItem> {


    PurInquiryItem selectPurInquiryItemById(Long inquiryItemSid);

    List<PurInquiryItem> selectPurInquiryItemList(PurInquiryItem purInquiryItem);

    /**
     * 添加多个
     * @param list List PurInquiryItem
     * @return int
     */
    int inserts(@Param("list") List<PurInquiryItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurInquiryItem
    * @return int
    */
    int updateAllById(PurInquiryItem entity);

    /**
     * 更新多个
     * @param list List PurInquiryItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PurInquiryItem> list);

    /**
     * 通过主表sid查主表下明细
     * @param inquirySid Long inquirySid
     * @return int
     */
    List<PurInquiryItem> selectPurInquiryItemListById(Long inquirySid);

    /**
     * 通过批量主表sid批量删除明细
     * @param inquirySid Long inquirySid
     * @return int
     */
    int deletePurInquiryItemByInquirySids(@Param("inquirySid") List<Long> inquirySid);

    /**
     * 通过批量主表sid查询所有明细表SID
     * @param inquirySid Long inquirySid
     * @return int
     */
    List<Long> selectPurInquiryItemSidListById(@Param("inquirySid") Long[] inquirySid);

    /**
     * 明细报表
     * @param purInquiryItem PurInquiryItem purInquiryItem
     * @return List
     */
    List<PurInquiryItem> getReportForm(PurInquiryItem purInquiryItem);
}

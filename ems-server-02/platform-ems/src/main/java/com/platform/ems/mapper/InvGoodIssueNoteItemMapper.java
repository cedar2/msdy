package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.InvIssueNoteReportRequest;
import com.platform.ems.domain.dto.response.InvIssueNoteReportResponse;
import com.platform.ems.domain.dto.response.InvReceiptNoteReportResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvGoodIssueNoteItem;

/**
 * 发货单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface InvGoodIssueNoteItemMapper  extends BaseMapper<InvGoodIssueNoteItem> {


    List<InvGoodIssueNoteItem> selectInvGoodIssueNoteItemById(@Param("noteSid")Long noteSid);

    List<InvGoodIssueNoteItem> selectInvGoodIssueNoteItemList(InvGoodIssueNoteItem invGoodIssueNoteItem);
    /**
     * 获取收货单明细报表
     */
    List<InvIssueNoteReportResponse> reportInvGoodIssueNote(InvIssueNoteReportRequest invIssueNoteReportRequest);
    /**
     * 添加多个
     * @param list List InvGoodIssueNoteItem
     * @return int
     */
    int inserts(@Param("list") List<InvGoodIssueNoteItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvGoodIssueNoteItem
    * @return int
    */
    int updateAllById(InvGoodIssueNoteItem entity);

    /**
     * 更新多个
     * @param list List InvGoodIssueNoteItem
     * @return int
     */
    int updatesAllById(@Param("list") List<InvGoodIssueNoteItem> list);


}

package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.InvGoodIssueNoteItem;
import com.platform.ems.domain.dto.request.InvReceiptNoteReportRequest;
import com.platform.ems.domain.dto.response.InvReceiptNoteReportResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvGoodReceiptNoteItem;

/**
 * 收货单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface InvGoodReceiptNoteItemMapper  extends BaseMapper<InvGoodReceiptNoteItem> {


    List<InvGoodReceiptNoteItem>  selectInvGoodReceiptNoteItemById(Long goodReceiptNoteItemSid);

    List<InvGoodReceiptNoteItem> selectInvGoodReceiptNoteItemList(InvGoodReceiptNoteItem invGoodReceiptNoteItem);

    /**
     * 获取收货单明细报表
     */
    List<InvReceiptNoteReportResponse> reportInvGoodReceiptNote(InvReceiptNoteReportRequest invReceiptNoteReportRequest);

    /**
     * 添加多个
     * @param list List InvGoodReceiptNoteItem
     * @return int
     */
    int inserts(@Param("list") List<InvGoodReceiptNoteItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvGoodReceiptNoteItem
    * @return int
    */
    int updateAllById(InvGoodReceiptNoteItem entity);

    /**
     * 更新多个
     * @param list List InvGoodReceiptNoteItem
     * @return int
     */
    int updatesAllById(@Param("list") List<InvGoodReceiptNoteItem> list);


}

package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.InvGoodReceiptNote;
import com.platform.ems.domain.InvGoodReceiptNoteItem;
import com.platform.ems.domain.TecBomItemReport;
import com.platform.ems.domain.dto.request.InvReceiptNoteReportRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvReceiptNoteReportResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 收货单Service接口
 *
 * @author linhongwei
 * @date 2021-06-01
 */
public interface IInvGoodReceiptNoteService extends IService<InvGoodReceiptNote>{
    /**
     * 查询收货单
     *
     * @param goodReceiptNoteSid 收货单ID
     * @return 收货单
     */
    public InvGoodReceiptNote selectInvGoodReceiptNoteById(Long goodReceiptNoteSid);
    public List<InvGoodReceiptNoteItem> sort(List<InvGoodReceiptNoteItem> items, String type);
    /**
     * 查询收货单列表
     *
     * @param invGoodReceiptNote 收货单
     * @return 收货单集合
     */
    public List<InvGoodReceiptNote> selectInvGoodReceiptNoteList(InvGoodReceiptNote invGoodReceiptNote);

    /**
     * 物料需求测算-创建收货单
     */
    public InvGoodReceiptNote getGoodReceiptNote(List<TecBomItemReport> order);
    /**
     * 查询收货单报表
     *
     * @param request 收货单
     * @return 收货单集合
     */
    List<InvReceiptNoteReportResponse> reportInvReceiptNote(InvReceiptNoteReportRequest request);

    /**
     * 新增收货单
     *
     * @param invGoodReceiptNote 收货单
     * @return 结果
     */
    public int insertInvGoodReceiptNote(InvGoodReceiptNote invGoodReceiptNote);

    /**
     * 修改收货单
     *
     * @param invGoodReceiptNote 收货单
     * @return 结果
     */
    public int updateInvGoodReceiptNote(InvGoodReceiptNote invGoodReceiptNote);

    /**
     * 变更收货单
     *
     * @param invGoodReceiptNote 收货单
     * @return 结果
     */
    public int changeInvGoodReceiptNote(InvGoodReceiptNote invGoodReceiptNote);

    /**
     * 批量删除收货单
     *
     * @param goodReceiptNoteSids 需要删除的收货单ID
     * @return 结果
     */
    public int deleteInvGoodReceiptNoteByIds(List<Long> goodReceiptNoteSids);
    /**
     * 批量删除收货单
     *
     * @param goodReceiptNoteSids 需要删除的收货单ID
     * @return 结果
     */
    public int close(List<Long> goodReceiptNoteSids);
    /**
    * 启用/停用
    * @param invGoodReceiptNote
    * @return
    */
    int changeStatus(InvGoodReceiptNote invGoodReceiptNote);

    /**
     * 更改确认状态
     * @param invGoodReceiptNote
     * @return
     */
    int check(InvGoodReceiptNote invGoodReceiptNote);
    /**
     * 提交时校验
     */
    public OrderErrRequest processCheck(OrderErrRequest request);
    /**
     * 导入收货单
     */
    AjaxResult importData(MultipartFile file);
    /**
     * 复制
     */
    public InvGoodReceiptNote getCopy(Long sid);

}

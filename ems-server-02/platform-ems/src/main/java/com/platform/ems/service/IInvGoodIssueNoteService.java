package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.InvGoodIssueNote;
import com.platform.ems.domain.InvGoodIssueNoteItem;
import com.platform.ems.domain.TecBomItemReport;
import com.platform.ems.domain.dto.request.InvIssueNoteReportRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvIssueNoteReportResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 发货单Service接口
 *
 * @author linhongwei
 * @date 2021-06-01
 */
public interface IInvGoodIssueNoteService extends IService<InvGoodIssueNote>{
    /**
     * 查询发货单
     *
     * @param goodIssueNoteSid 发货单ID
     * @return 发货单
     */
    public InvGoodIssueNote selectInvGoodIssueNoteById(Long goodIssueNoteSid);
    public List<InvGoodIssueNoteItem> sort(List<InvGoodIssueNoteItem> items, String type);
    /**
     * 查询发货单报表明细
     *
     * @param request 发货单
     * @return 发货单
     */
    public List<InvIssueNoteReportResponse> report(InvIssueNoteReportRequest request);
    /**
     * 物料需求测算-创建发货单
     */
    public InvGoodIssueNote getGoodIssueNote(List<TecBomItemReport> order);

    /**
     * 查询发货单列表
     *
     * @param invGoodIssueNote 发货单
     * @return 发货单集合
     */
    public List<InvGoodIssueNote> selectInvGoodIssueNoteList(InvGoodIssueNote invGoodIssueNote);

    /**
     * 新增发货单
     *
     * @param invGoodIssueNote 发货单
     * @return 结果
     */
    public int insertInvGoodIssueNote(InvGoodIssueNote invGoodIssueNote);
    //明细报表生成库存预留
    public int create(List<Long> sids);
    //明细报表释放预留库存
    public int reportFreeInv(List<Long> sids);

    /**
     * 修改发货单
     *
     * @param invGoodIssueNote 发货单
     * @return 结果
     */
    public int updateInvGoodIssueNote(InvGoodIssueNote invGoodIssueNote);

    /**
     * 关闭发货单
     */
    public int close(List<Long> sidList);

    /**
     * 变更发货单
     *
     * @param invGoodIssueNote 发货单
     * @return 结果
     */
    public int changeInvGoodIssueNote(InvGoodIssueNote invGoodIssueNote);

    /**
     * 批量删除发货单
     *
     * @param goodIssueNoteSids 需要删除的发货单ID
     * @return 结果
     */
    public int deleteInvGoodIssueNoteByIds(List<Long> goodIssueNoteSids);

    /**
    * 启用/停用
    * @param invGoodIssueNote
    * @return
    */
    int changeStatus(InvGoodIssueNote invGoodIssueNote);

    /**
     * 更改确认状态
     * @param invGoodIssueNote
     * @return
     */
    int check(InvGoodIssueNote invGoodIssueNote);
    /**
     * 提交时校验
     */
    public OrderErrRequest processCheck(OrderErrRequest request);
    /**
     * 导入发货单
     */
    AjaxResult importData(MultipartFile file);
    /**
     * 复制
     */
    public InvGoodIssueNote getCopy(Long sid);
}

package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.InvInventoryTransfer;
import com.platform.ems.domain.InvInventoryTransferItem;
import com.platform.ems.domain.TecBomItemReport;
import com.platform.ems.domain.dto.request.InvInventoryTransferRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvInventoryTransferResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 调拨单Service接口
 * 
 * @author linhongwei
 * @date 2021-06-04
 */
public interface IInvInventoryTransferService extends IService<InvInventoryTransfer>{
    /**
     * 查询调拨单
     * 
     * @param inventoryTransferSid 调拨单ID
     * @return 调拨单
     */
    public InvInventoryTransfer selectInvInventoryTransferById(Long inventoryTransferSid);
    public List<InvInventoryTransferItem> sort(List<InvInventoryTransferItem> items, String type);
    /**
     * 查询调拨单明细报表
     *
     * @param request 调拨单请求实体
     * @return 调拨单响应实体
     */
    public List<InvInventoryTransferResponse> report(InvInventoryTransferRequest request);
    /**
     * 物料需求测算-创建调拨单
     */
    public InvInventoryTransfer getGoodIssueNote(List<TecBomItemReport> list);
    /**
     * 查询调拨单列表
     * 
     * @param invInventoryTransfer 调拨单
     * @return 调拨单集合
     */
    public List<InvInventoryTransfer> selectInvInventoryTransferList(InvInventoryTransfer invInventoryTransfer);

    /**
     * 新增调拨单
     * 
     * @param invInventoryTransfer 调拨单
     * @return 结果
     */
    public int insertInvInventoryTransfer(InvInventoryTransfer invInventoryTransfer);

    /**
     * 修改调拨单
     * 
     * @param invInventoryTransfer 调拨单
     * @return 结果
     */
    public int updateInvInventoryTransfer(InvInventoryTransfer invInventoryTransfer);

    /**
     * 变更调拨单
     *
     * @param invInventoryTransfer 调拨单
     * @return 结果
     */
    public int changeInvInventoryTransfer(InvInventoryTransfer invInventoryTransfer);

    /**
     * 批量删除调拨单
     * 
     * @param inventoryTransferSids 需要删除的调拨单ID
     * @return 结果
     */
    public int deleteInvInventoryTransferByIds(List<Long> inventoryTransferSids);
    /**
     * 关闭调拨单
     */
    public int close(List<Long> inventoryTransferSids);
    /**
    * 启用/停用
    * @param invInventoryTransfer
    * @return
    */
    int changeStatus(InvInventoryTransfer invInventoryTransfer);

    /**
     * 更改确认状态
     * @param invInventoryTransfer
     * @return
     */
    int check(InvInventoryTransfer invInventoryTransfer);

    /**
     * 导入调拨单
     */
    AjaxResult importData(MultipartFile file);

    /**
     * 复制
     */
    public InvInventoryTransfer getCopy(Long sid);
    //明细报表生成库存预留
    public int create(Long[] inventoryTransferItemSidList);
    //明细报表释放预留库存
    public int reportFreeInv(List<Long> sids);
    /**
     * 提交时校验
     */
    public OrderErrRequest processCheck(OrderErrRequest request);
}

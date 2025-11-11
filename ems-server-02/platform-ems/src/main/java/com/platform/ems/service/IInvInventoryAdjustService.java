package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.InvInventoryAdjust;
import com.platform.ems.domain.InvInventoryAdjustItem;
import com.platform.ems.domain.dto.request.InvCrossColorReportRequest;
import com.platform.ems.domain.dto.request.InvInventoryAdjustReportRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvCrossColorReportResponse;
import com.platform.ems.domain.dto.response.InvInventoryAdjustReportResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 库存调整单Service接口
 *
 * @author linhongwei
 * @date 2021-04-19
 */
public interface IInvInventoryAdjustService extends IService<InvInventoryAdjust>{
    /**
     * 查询库存调整单
     *
     * @param inventoryAdjustSid 库存调整单ID
     * @return 库存调整单
     */
    public InvInventoryAdjust selectInvInventoryAdjustById(Long inventoryAdjustSid);
    public List<InvInventoryAdjustItem> sort(List<InvInventoryAdjustItem> items, String type);
    /**
     * 查询库存调整单列表
     *
     * @param invInventoryAdjust 库存调整单
     * @return 库存调整单集合
     */
    public List<InvInventoryAdjust> selectInvInventoryAdjustList(InvInventoryAdjust invInventoryAdjust);
    /**
     * 查询库存调整单明细报表
     *
     * @param request 库存调整单请求实体
     * @return 库存调整单响应集合
     */
    List<InvInventoryAdjustReportResponse> reportInvInventoryAdjust(InvInventoryAdjustReportRequest request);

    /**
     * 查询串色串码明细报表
     *
     * @param request 串色串码请求实体
     * @return 串色串码响应集合
     */
    public List<InvCrossColorReportResponse> reportCrossColor(InvCrossColorReportRequest request);

    /**
     * 新增库存调整单
     *
     * @param invInventoryAdjust 库存调整单
     * @return 结果
     */
    public int insertInvInventoryAdjust(InvInventoryAdjust invInventoryAdjust);

    /**
     * 修改库存调整单
     *
     * @param invInventoryAdjust 库存调整单
     * @return 结果
     */
    public int updateInvInventoryAdjust(InvInventoryAdjust invInventoryAdjust);

    /**
     * 批量删除库存调整单
     *
     * @param inventoryAdjustSids 需要删除的库存调整单ID
     * @return 结果
     */
    public int deleteInvInventoryAdjustByIds(Long[] inventoryAdjustSids);

    /**
     * 库存调整单确认
     */
    int confirm(InvInventoryAdjust invInventoryAdjust);

    /**
     * 库存调整单变更
     */
    int change(InvInventoryAdjust invInventoryAdjust);

    /**
     * 复制
     */
    public InvInventoryAdjust getCopy(Long sid);
    public OrderErrRequest processCheck(OrderErrRequest request);
    /**
     * 库存调整导入
     */
    public AjaxResult importDataInv(MultipartFile file);
}

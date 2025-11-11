package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.InvInventorySheet;
import com.platform.ems.domain.InvInventorySheetItem;
import com.platform.ems.domain.dto.request.InvInventorySheetReportRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvInventorySheetReportResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 盘点单Service接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface IInvInventorySheetService extends IService<InvInventorySheet>{
    /**
     * 查询盘点单
     * 
     * @param inventorySheetSid 盘点单ID
     * @return 盘点单
     */
    public InvInventorySheet selectInvInventorySheetById(Long inventorySheetSid);
    public InvInventorySheet getInvInventorySheet(InvInventorySheet invInventorySheet);
    /**
     * 查询盘点单列表
     * 
     * @param invInventorySheet 盘点单
     * @return 盘点单集合
     */
    public List<InvInventorySheet> selectInvInventorySheetList(InvInventorySheet invInventorySheet);

    /**
     * 查询盘点单明细报表
     *
     * @param invInventorySheetReportRequest 盘点单
     * @return 盘点单集合
     */
    List<InvInventorySheetReportResponse> reportInvInventorySheet(InvInventorySheetReportRequest invInventorySheetReportRequest);

    /**
     * 新增盘点单
     * 
     * @param invInventorySheet 盘点单
     * @return 结果
     */
    public int insertInvInventorySheet(InvInventorySheet invInventorySheet);

    /**
     * 修改盘点单
     * 
     * @param invInventorySheet 盘点单
     * @return 结果
     */
    public int updateInvInventorySheet(InvInventorySheet invInventorySheet);

    public List<InvInventorySheetItem> sort(List<InvInventorySheetItem> items, String type);
    /**
     * 批量删除盘点单
     * 
     * @param inventorySheetSids 需要删除的盘点单ID
     * @return 结果
     */
    public int deleteInvInventorySheetByIds(Long[] inventorySheetSids);

    /**
     * 盘点单批量修改处理状态
     */
    int handle(InvInventorySheet invInventorySheet);

    /**
     * 盘点单确认
     */
    int confirm(InvInventorySheet invInventorySheet);

    /**
     * 盘点单变更
     */
    int change(InvInventorySheet invInventorySheet);

    /**
     * 盘点单过账
     */
    public int post(InvInventorySheet invInventorySheet);
    /**
     * 复制
     */
    public InvInventorySheet getCopy(Long sid);

    /**
     * 提交时校验
     */
    public OrderErrRequest processCheck(OrderErrRequest request);

    /**
     * 盘点导入
     */
    public AjaxResult importDataInv(MultipartFile file);

    //盘点明细导出
    public void exportGood(HttpServletResponse response, Long sid);

    /**
     * 实盘量导入
     */
    public AjaxResult importData(MultipartFile file);
}

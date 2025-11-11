package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.PurPurchaseOrder;
import com.platform.ems.domain.PurVendorMonthAccountBill;
import com.platform.ems.domain.SalCustomerMonthAccountBill;
import com.platform.ems.domain.SalSalePrice;
import com.platform.ems.domain.dto.response.InvInventoryDocumentReportResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 供应商月对账单Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IPurVendorMonthAccountBillService extends IService<PurVendorMonthAccountBill> {
    /**
     * 查询供应商月对账单
     *
     * @param vendorMonthAccountBillSid 供应商月对账单ID
     * @return 供应商月对账单
     */
    public PurVendorMonthAccountBill selectPurVendorMonthAccountBillById(Long vendorMonthAccountBillSid);

    /**
     * 查询供应商月对账单列表
     *
     * @param purVendorMonthAccountBill 供应商月对账单
     * @return 供应商月对账单集合
     */
    public List<PurVendorMonthAccountBill> selectPurVendorMonthAccountBillList(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 新增供应商月对账单
     *
     * @param purVendorMonthAccountBill 供应商月对账单
     * @return 结果
     */
    public int insertPurVendorMonthAccountBill(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 修改供应商月对账单
     *
     * @param purVendorMonthAccountBill 供应商月对账单
     * @return 结果
     */
    public int updatePurVendorMonthAccountBill(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 新增/编辑直接提交供应商月对账单
     *
     * @param purVendorMonthAccountBill 供应商月对账单
     * @return 结果
     */
    public AjaxResult submit(PurVendorMonthAccountBill purVendorMonthAccountBill, String jump);

    /**
     * 供应商月对账单确认
     */
    int confirm(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 变更供应商月对账单
     *
     * @param purVendorMonthAccountBill 供应商月对账单
     * @return 结果
     */
    public int changePurVendorMonthAccountBill(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 批量删除供应商月对账单
     *
     * @param vendorMonthAccountBillSids 需要删除的供应商月对账单ID
     * @return 结果
     */
    public int deletePurVendorMonthAccountBillByIds(List<Long> vendorMonthAccountBillSids);

    /**
     * 更改确认状态
     *
     * @param purVendorMonthAccountBill
     * @return
     */
    int check(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 提交校验
     *
     * @param purVendorMonthAccountBill
     * @return
     */
    int processCheck(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 供应商月对账单新建入口
     *
     * @param purVendorMonthAccountBill 供应商，公司，月份
     * @return 供应商月对账单
     */
    PurVendorMonthAccountBill entrance(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 查询供应商月对账单明细信息
     *
     * @param purVendorMonthAccountBill 供应商，公司，月份
     * @return 供应商月对账单
     */
    PurVendorMonthAccountBill selectItemList(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 计算账单总览金额
     *
     * @param purVendorMonthAccountBill 各明细
     * @return 供应商月对账单
     */
    PurVendorMonthAccountBill calculationAmount(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 变更所属账期
     *
     * @param list
     * @return
     */
    int changeYearMonth(PurVendorMonthAccountBill list);

    /**
     * 查询供应商台账报表
     *
     * @param purVendorMonthAccountBill 供应商，公司
     * @return 供应商月对账单
     */
    TableDataInfo selectReportList(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 导入
     *
     * @param file
     * @return
     */
    AjaxResult importData(MultipartFile file);

    /**
     * 记账
     *
     * @param list
     * @return
     */
    int addForm(List<PurVendorMonthAccountBill> list);


    public void exportPur(HttpServletResponse response, PurVendorMonthAccountBill purVendorMonthAccountBill);

}

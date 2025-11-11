package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.FinVendorMonthAccountBill;
import com.platform.ems.domain.FinVendorMonthAccountBillInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 供应商月对账单Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IFinVendorMonthAccountBillService extends IService<FinVendorMonthAccountBill> {
    /**
     * 查询供应商月对账单
     *
     * @param vendorMonthAccountBillSid 供应商月对账单ID
     * @return 供应商月对账单
     */
    public FinVendorMonthAccountBill selectFinVendorMonthAccountBillById(Long vendorMonthAccountBillSid);

    /**
     * 查询供应商月对账单列表
     *
     * @param finVendorMonthAccountBill 供应商月对账单
     * @return 供应商月对账单集合
     */
    public List<FinVendorMonthAccountBill> selectFinVendorMonthAccountBillList(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 新增供应商月对账单
     *
     * @param finVendorMonthAccountBill 供应商月对账单
     * @return 结果
     */
    public int insertFinVendorMonthAccountBill(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 修改供应商月对账单
     *
     * @param finVendorMonthAccountBill 供应商月对账单
     * @return 结果
     */
    public int updateFinVendorMonthAccountBill(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 变更供应商月对账单
     *
     * @param finVendorMonthAccountBill 供应商月对账单
     * @return 结果
     */
    public int changeFinVendorMonthAccountBill(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 批量删除供应商月对账单
     *
     * @param vendorMonthAccountBillSids 需要删除的供应商月对账单ID
     * @return 结果
     */
    public int deleteFinVendorMonthAccountBillByIds(List<Long> vendorMonthAccountBillSids);

    /**
     * 更改确认状态
     *
     * @param finVendorMonthAccountBill
     * @return
     */
    int check(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 供应商月对账单新建入口
     *
     * @param finVendorMonthAccountBill 供应商，公司，月份
     * @return 供应商月对账单
     */
    FinVendorMonthAccountBill entrance(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 查询供应商月对账单明细信息
     *
     * @param finVendorMonthAccountBill 供应商，公司，月份
     * @return 供应商月对账单
     */
    FinVendorMonthAccountBill selectItemList(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 计算账单总览金额
     *
     * @param finVendorMonthAccountBill 各明细
     * @return 供应商月对账单
     */
    FinVendorMonthAccountBill calculationAmount(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 变更所属账期
     *
     * @param list
     * @return
     */
    int changeYearMonth(FinVendorMonthAccountBill list);

    /**
     * 查询供应商台账报表
     *
     * @param finVendorMonthAccountBill 供应商，公司
     * @return 供应商月对账单
     */
    TableDataInfo selectReportList(FinVendorMonthAccountBill finVendorMonthAccountBill);

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
    int addForm(List<FinVendorMonthAccountBill> list);

}

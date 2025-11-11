package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.FinCustomerMonthAccountBill;
import com.platform.ems.domain.FinCustomerMonthAccountBillInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 客户月对账单Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IFinCustomerMonthAccountBillService extends IService<FinCustomerMonthAccountBill> {
    /**
     * 查询客户月对账单
     *
     * @param customerMonthAccountBillSid 客户月对账单ID
     * @return 客户月对账单
     */
    public FinCustomerMonthAccountBill selectFinCustomerMonthAccountBillById(Long customerMonthAccountBillSid);

    /**
     * 查询客户月对账单列表
     *
     * @param finCustomerMonthAccountBill 客户月对账单
     * @return 客户月对账单集合
     */
    public List<FinCustomerMonthAccountBill> selectFinCustomerMonthAccountBillList(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 新增客户月对账单
     *
     * @param finCustomerMonthAccountBill 客户月对账单
     * @return 结果
     */
    public int insertFinCustomerMonthAccountBill(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 修改客户月对账单
     *
     * @param finCustomerMonthAccountBill 客户月对账单
     * @return 结果
     */
    public int updateFinCustomerMonthAccountBill(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 变更客户月对账单
     *
     * @param finCustomerMonthAccountBill 客户月对账单
     * @return 结果
     */
    public int changeFinCustomerMonthAccountBill(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 批量删除客户月对账单
     *
     * @param customerMonthAccountBillSids 需要删除的客户月对账单ID
     * @return 结果
     */
    public int deleteFinCustomerMonthAccountBillByIds(List<Long> customerMonthAccountBillSids);

    /**
     * 更改确认状态
     *
     * @param finCustomerMonthAccountBill
     * @return
     */
    int check(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 客户月对账单新建入口
     *
     * @param finCustomerMonthAccountBill 客户，公司，月份
     * @return 供应商月对账单
     */
    FinCustomerMonthAccountBill entrance(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 查询客户月对账单明细信息
     *
     * @param finCustomerMonthAccountBill 客户，公司，月份
     * @return 供应商月对账单
     */
    FinCustomerMonthAccountBill selectItemList(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 计算账单总览金额
     *
     * @param finCustomerMonthAccountBill 各明细
     * @return 供应商月对账单
     */
    FinCustomerMonthAccountBill calculationAmount(FinCustomerMonthAccountBill finCustomerMonthAccountBill);


    /**
     * 变更所属账期
     *
     * @param list
     * @return
     */
    int changeYearMonth(FinCustomerMonthAccountBill list);

    /**
     * 查询客户台账报表
     *
     * @param finCustomerMonthAccountBill 客户，公司
     * @return 供应商月对账单
     */
    TableDataInfo selectReportList(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

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
    int addForm(List<FinCustomerMonthAccountBill> list);
}

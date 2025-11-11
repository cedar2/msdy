package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.SalCustomerMonthAccountBill;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 客户对账单Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface ISalCustomerMonthAccountBillService extends IService<SalCustomerMonthAccountBill> {
    /**
     * 查询客户对账单
     *
     * @param customerMonthAccountBillSid 客户对账单ID
     * @return 客户对账单
     */
    public SalCustomerMonthAccountBill selectSalCustomerMonthAccountBillById(Long customerMonthAccountBillSid);

    /**
     * 查询客户对账单列表
     *
     * @param purVendorMonthAccountBill 客户对账单
     * @return 客户对账单集合
     */
    public List<SalCustomerMonthAccountBill> selectSalCustomerMonthAccountBillList(SalCustomerMonthAccountBill purVendorMonthAccountBill);

    /**
     * 新增客户对账单
     *
     * @param purVendorMonthAccountBill 客户对账单
     * @return 结果
     */
    public int insertSalCustomerMonthAccountBill(SalCustomerMonthAccountBill purVendorMonthAccountBill);

    /**
     * 修改客户对账单
     *
     * @param purVendorMonthAccountBill 客户对账单
     * @return 结果
     */
    public int updateSalCustomerMonthAccountBill(SalCustomerMonthAccountBill purVendorMonthAccountBill);

    /**
     * 新增/编辑直接提交客户对账单
     *
     * @param purVendorMonthAccountBill 客户对账单
     * @return 结果
     */
    public AjaxResult submit(SalCustomerMonthAccountBill purVendorMonthAccountBill, String jump);

    /**
     * 客户对账单确认
     */
    int confirm(SalCustomerMonthAccountBill purVendorMonthAccountBill);

    /**
     * 变更客户对账单
     *
     * @param purVendorMonthAccountBill 客户对账单
     * @return 结果
     */
    public int changeSalCustomerMonthAccountBill(SalCustomerMonthAccountBill purVendorMonthAccountBill);

    /**
     * 批量删除客户对账单
     *
     * @param customerMonthAccountBillSids 需要删除的客户对账单ID
     * @return 结果
     */
    public int deleteSalCustomerMonthAccountBillByIds(List<Long> customerMonthAccountBillSids);

    /**
     * 更改确认状态
     *
     * @param purVendorMonthAccountBill
     * @return
     */
    int check(SalCustomerMonthAccountBill purVendorMonthAccountBill);

    /**
     * 提交校验
     *
     * @param salCustomerMonthAccountBill
     * @return
     */
    int processCheck(SalCustomerMonthAccountBill salCustomerMonthAccountBill);

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
    int addForm(List<SalCustomerMonthAccountBill> list);


    public void exportPur(HttpServletResponse response, SalCustomerMonthAccountBill purVendorMonthAccountBill);

}

package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayProcessStepCompleteItem;
import com.platform.ems.domain.PaySalaryBill;
import com.platform.ems.domain.PaySalaryBillItem;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 工资单-主Service接口
 *
 * @author linhongwei
 * @date 2021-09-14
 */
public interface IPaySalaryBillService extends IService<PaySalaryBill> {
    /**
     * 查询工资单-主
     *
     * @param salaryBillSid 工资单-主ID
     * @return 工资单-主
     */
    public PaySalaryBill selectPaySalaryBillById(Long salaryBillSid);

    /**
     * 查询工资单-主列表
     *
     * @param paySalaryBill 工资单-主
     * @return 工资单-主集合
     */
    public List<PaySalaryBill> selectPaySalaryBillList(PaySalaryBill paySalaryBill);

    /**
     * 新增工资单-主
     *
     * @param paySalaryBill 工资单-主
     * @return 结果
     */
    public int insertPaySalaryBill(PaySalaryBill paySalaryBill);

    /**
     * 修改工资单-主
     *
     * @param paySalaryBill 工资单-主
     * @return 结果
     */
    public int updatePaySalaryBill(PaySalaryBill paySalaryBill);

    /**
     * 变更工资单-主
     *
     * @param paySalaryBill 工资单-主
     * @return 结果
     */
    public int changePaySalaryBill(PaySalaryBill paySalaryBill);

    /**
     * 批量删除工资单-主
     *
     * @param salaryBillSids 需要删除的工资单-主ID
     * @return 结果
     */
    public int deletePaySalaryBillByIds(List<Long> salaryBillSids);

    /**
     * 更改确认状态
     *
     * @param paySalaryBill
     * @return
     */
    int check(PaySalaryBill paySalaryBill);

    /**
     * 计件工资(自动)
     */
    PaySalaryBillItem getPieceworkSalary(PayProcessStepCompleteItem payProcessStepCompleteItem);

    /**
     * 工资单明细校验
     */
    PaySalaryBill verifyItem(PaySalaryBill paySalaryBill);

    /**
     * 单据提交校验
     */
    int verify(PaySalaryBill paySalaryBill);

    /**
     * 选择某一笔主表导出它的明细
     *
     * @param
     * @return
     */
    void exportItemBySalary(HttpServletResponse response, PaySalaryBill paySalaryBill);

    /**
     * 导入工资单
     *
     * @param file
     * @return
     */
    EmsResultEntity importData(MultipartFile file);

    /**
     * 选择某一笔主表导入它的明细
     *
     * @param file
     * @return
     */
    Object importItemData(MultipartFile file, String salaryBillCode);
}

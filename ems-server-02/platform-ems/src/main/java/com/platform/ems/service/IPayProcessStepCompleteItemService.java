package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayProcessStepComplete;
import com.platform.ems.domain.PayProcessStepCompleteItem;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.PayProcessStepCompleteTableRequest;
import com.platform.ems.domain.dto.response.PayProcessStepCompleteTableResponse;
import com.platform.ems.domain.dto.response.form.PaySalaryWageFormResponse;
import com.platform.ems.domain.dto.response.form.ProductProcessCompleteStatisticsSalary;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * 完工量申报-明细Service接口
 *
 * @author linhongwei
 * @date 2021-09-08
 */
public interface IPayProcessStepCompleteItemService extends IService<PayProcessStepCompleteItem> {
    /**
     * 查询完工量申报-明细
     *
     * @param stepCompleteItemSid 完工量申报-明细ID
     * @return 完工量申报-明细
     */
    PayProcessStepCompleteItem selectPayProcessStepCompleteItemById(Long stepCompleteItemSid);

    /**
     * 查询完工量申报-明细列表
     *
     * @param payProcessStepCompleteItem 完工量申报-明细
     * @return 计薪量申报-明细集合
     */
    List<PayProcessStepCompleteItem> selectPayProcessStepCompleteItemList(PayProcessStepCompleteItem payProcessStepCompleteItem);

    /**
     * 新增计薪量申报-明细
     *
     * @param payProcessStepCompleteItem 计薪量申报-明细
     * @return 结果
     */
    int insertPayProcessStepCompleteItem(PayProcessStepCompleteItem payProcessStepCompleteItem);

    /**
     * 修改计薪量申报-明细
     *
     * @param payProcessStepCompleteItem 计薪量申报-明细
     * @return 结果
     */
    int updatePayProcessStepCompleteItem(PayProcessStepCompleteItem payProcessStepCompleteItem);

    /**
     * 变更计薪量申报-明细
     *
     * @param payProcessStepCompleteItem 计薪量申报-明细
     * @return 结果
     */
    int changePayProcessStepCompleteItem(PayProcessStepCompleteItem payProcessStepCompleteItem);

    /**
     * 批量删除计薪量申报-明细
     *
     * @param stepCompleteItemSids 需要删除的计薪量申报-明细ID
     * @return 结果
     */
    int deletePayProcessStepCompleteItemByIds(List<Long> stepCompleteItemSids);

    /**
     * “工资清单”页签下方，在“考勤信息：页签旁边，新增一页签”计件量明细“
     */
    List<PayProcessStepCompleteItem> getProcessStepCompleteWageItem(PayProcessStepCompleteItem payProcessStepCompleteItem);

    /**
     * 刷新排序
     */
    List<PayProcessStepCompleteItem> sort(List<PayProcessStepCompleteItem> payProcessStepCompleteItemList);

    /**
     * 更新道序工价 / 工价倍率
     */
    List<PayProcessStepCompleteItem> updatePrice(PayProcessStepComplete payProcessStepComplete);

    /**
     * 计薪量明细报表更新道序工价 / 工价倍率
     */
    int updateItemPrice(List<PayProcessStepCompleteItem> itemList);

    /**
     * 计薪明细预警 报错 可选择性
     * list 要包含 在 payProcessStepComplete.getItemList 里面
     */
    EmsResultEntity checkPayProductJijianSettleInforBySelect(List<PayProcessStepCompleteItem> list, PayProcessStepComplete payProcessStepComplete);

    /**
     * 计薪明细预警 可选择性
     * 点击此按钮，进行结算量校验，如超量，则预警列显示红灯，否则预警列默认为：空
     */
    List<PayProcessStepCompleteItem> itemWarningBySelect(List<PayProcessStepCompleteItem> list, PayProcessStepComplete payProcessStepComplete);

    /**
     * 计薪明细按款显示
     */
    PayProcessStepCompleteTableResponse itemTable(PayProcessStepCompleteTableRequest request);

    /**
     * 计薪明细按款录入的显示
     */
    PayProcessStepCompleteTableResponse itemAddToTable(PayProcessStepComplete request);

    /**
     * 计薪明细按款录入点确认
     */
    EmsResultEntity itemAddByTable(PayProcessStepCompleteTableResponse request);

    /**
     * 计薪量申报明细报表 查询
     */
    List<PaySalaryWageFormResponse> getProcessStepCompleteWage(PaySalaryWageFormResponse paySalaryBillItem);

    /**
     * 计薪量申报明细报表 打印汇总 数据源
     */
    List<PaySalaryWageFormResponse> printProcessStepCompleteWageVo(PaySalaryWageFormResponse paySalaryBillItem);

    /**
     * 计薪量申报明细报表 打印汇总 数据源 按结算班组
     */
    List<PaySalaryWageFormResponse> printProcessStepCompleteWageProcessVo(PaySalaryWageFormResponse paySalaryBillItem);

    /**
     * 计薪量申报明细报表 打印汇总 按员工加班组维度
     */
    HashMap<String, Object> printProcessStepCompleteWage(List<PaySalaryWageFormResponse> itemList);

    /**
     * 计薪量申报明细报表 打印汇总 按员工维度
     */
    HashMap<String, Object> printProcessStepCompleteWageByStaff(List<PaySalaryWageFormResponse> itemList);

    /**
     * 计薪量申报明细报表 打印汇总 按隶属班组维度
     */
    HashMap<String, Object> printProcessStepCompleteWageByWorkCenter(List<PaySalaryWageFormResponse> itemList);

    /**
     * 计薪量申报明细报表 打印汇总 按结算班组维度
     */
    HashMap<String, Object> printProcessStepCompleteWageByWorkCenterProcess(List<PaySalaryWageFormResponse> itemList);

    /**
     * 计薪量申报明细报表 打印汇总导出
     */
    void printExcelProcessStepCompleteWage(HttpServletResponse response, PaySalaryWageFormResponse paySalaryBillItem);

    /**
     * 道序计薪量统计报表
     * 根据查询字段，从“计薪量申报头表及明细表中”获取满足条件的计薪量信息，
     * 并按“工厂、商品编码(款号)、道序序号、道序工价、工价倍率、调价率、操作部门、商品工价类型、计薪完工类型”对“计薪量”进行小计。
     */
    List<PayProcessStepCompleteItem> selectProcessStepCompleteItemForm(PayProcessStepComplete payProcessStepComplete);

    /**
     * 商品计件工资统计报表
     *
     * @param payProcessStepCompleteItem 请求
     * @return 商品计件工资统计报表
     */
    List<ProductProcessCompleteStatisticsSalary> selectCompleteStatisticsSalaryList(ProductProcessCompleteStatisticsSalary payProcessStepCompleteItem);
}

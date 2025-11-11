package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PayProcessStepComplete;
import com.platform.ems.domain.PayProcessStepCompleteItem;
import com.platform.ems.domain.dto.response.form.PaySalaryWageFormResponse;
import com.platform.ems.domain.dto.response.form.ProductProcessCompleteStatisticsSalary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 计薪量申报-明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-08
 */
public interface PayProcessStepCompleteItemMapper extends BaseMapper<PayProcessStepCompleteItem> {


    PayProcessStepCompleteItem selectPayProcessStepCompleteItemById(Long stepCompleteItemSid);

    List<PayProcessStepCompleteItem> selectPayProcessStepCompleteItemList(PayProcessStepCompleteItem payProcessStepCompleteItem);

    /**
     * 有关联出确认状态的已计薪量小计
     * 即：根据计薪明细中的“工厂、商品工价类型、计薪完工类型、操作部门、款号、排产批次号、道序序号”，
     * 获取“计薪量申报明细报表”中的道序计薪量，
     * 并对获取的计薪量进行小计（获取所有符合条件的“已确认“状态的计薪量申报单（当前比是保存状态就加上自己的计薪量），
     * @return list List PayProcessStepCompleteItem
     */
    List<PayProcessStepCompleteItem> selectPayProcessStepCompleteItemJoinList(PayProcessStepCompleteItem payProcessStepCompleteItem);

    /**
     * 添加多个
     *
     * @param list List PayProcessStepCompleteItem
     * @return int
     */
    int inserts(@Param("list") List<PayProcessStepCompleteItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayProcessStepCompleteItem
     * @return int
     */
    int updateAllById(PayProcessStepCompleteItem entity);

    /**
     * 更新多个
     *
     * @param list List PayProcessStepCompleteItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PayProcessStepCompleteItem> list);

    /**
     *  更新”更新工价/倍率“多个
     *
     * @param list List PayProcessStepCompleteItem
     * @return int
     */
    int updatesPriceById(@Param("list") List<PayProcessStepCompleteItem> list);

    /**
     * “工资清单”页签下方，在“考勤信息：页签旁边，新增一页签”计件量明细“
     *
     * @param payProcessStepCompleteItem PayProcessStepCompleteItem
     * @return int
     */
    List<PayProcessStepCompleteItem> getProcessStepCompleteWageItem(PayProcessStepCompleteItem payProcessStepCompleteItem);

    /**
     * 计件工资明细报表
     */
    @SqlParser(filter=true)
    List<PaySalaryWageFormResponse> getProcessStepCompleteWageForm(PaySalaryWageFormResponse paySalaryBillItem);

    /**
     * 计薪量申报明细报表，点击“打印预览”、“汇总导出”，按照“工厂+班组+员工+商品编码+道序编码+序号+道序工价+工价倍率+调价倍率”对“数量”、“金额”进行汇总
     */
    @SqlParser(filter=true)
    List<PaySalaryWageFormResponse> getCollectProcessStepCompleteWageForm(PaySalaryWageFormResponse paySalaryBillItem);

    /**
     * 计薪量申报明细报表，点击“打印预览”、“汇总导出”，按照“工厂+班组+员工+商品编码+道序编码+序号+道序工价+工价倍率+调价倍率”对“数量”、“金额”进行汇总
     */
    @SqlParser(filter=true)
    List<PaySalaryWageFormResponse> getCollectProcessStepCompleteWageProcessForm(PaySalaryWageFormResponse paySalaryBillItem);

    /**
     * 即：根据计薪明细中的“工厂、商品工价类型、计薪完工类型、操作部门、款号、排产批次号、道序序号”，
     * 获取“计薪量申报明细报表”中的道序计薪量，
     * 并对获取的计薪量进行小计（获取所有符合条件的“已确认“状态的计薪量申报单（当前勾选的这笔不需要），
     */
    Double countCompleteQuantity(PayProcessStepCompleteItem payProcessStepCompleteItem);

    /**
     * 道序计薪量统计报表 求total
     * 根据查询字段，从“计薪量申报头表及明细表中”获取满足条件的计薪量信息，
     * 并按“工厂、商品编码(款号)、道序序号、道序工价、工价倍率、调价率、操作部门、商品工价类型、计薪完工类型”对“计薪量”进行小计。
     */
    Integer countProcessStepCompleteItemForm(PayProcessStepComplete payProcessStepComplete);

    /**
     * 道序计薪量统计报表
     * 根据查询字段，从“计薪量申报头表及明细表中”获取满足条件的计薪量信息，
     * 并按“工厂、商品编码(款号)、道序序号、道序工价、工价倍率、调价率、操作部门、商品工价类型、计薪完工类型”对“计薪量”进行小计。
     */
    @SqlParser(filter=true)
    List<PayProcessStepCompleteItem> selectProcessStepCompleteItemForm(PayProcessStepComplete payProcessStepComplete);

    /**
     * 商品计件工资统计报表
     *
     * @param payProcessStepCompleteItem 请求
     * @return 商品计件工资统计报表
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ProductProcessCompleteStatisticsSalary> selectCompleteStatisticsSalaryList(ProductProcessCompleteStatisticsSalary payProcessStepCompleteItem);
}

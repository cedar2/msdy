package com.platform.ems.mapper;
import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.platform.ems.domain.dto.response.form.SaleManufactureOrderProcessFormResponse;
import org.springframework.data.repository.query.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManManufactureOrder;

/**
 * 生产订单Mapper接口
 *
 * @author qhq
 * @date 2021-04-10
 */
public interface ManManufactureOrderMapper extends BaseMapper<ManManufactureOrder> {
    /**
     * 查询生产订单
     *
     * @param manufactureOrderSid 生产订单ID
     * @return 生产订单
     */
	ManManufactureOrder selectManManufactureOrderById(Long manufactureOrderSid);

    /**
     * 查询生产订单列表
     *
     * @param manManufactureOrder 生产订单
     * @return 生产订单集合
     */
	List<ManManufactureOrder> selectManManufactureOrderList(ManManufactureOrder manManufactureOrder);

    int inserts(List<ManManufactureOrder> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity ManManufactureOrder
     * @return int
     */
     int updateAllById(ManManufactureOrder entity);

     /**
      * 更新多个
      * @param list List ManManufactureOrder
      * @return int
      */
     int updatesAllById(@Param("list") List<ManManufactureOrder> list);

    int updatesPlanById(@Param("list") List<ManManufactureOrder> list);

    /**
     * 生产订单下拉框列表
     */
    List<ManManufactureOrder> getManufactureOrderList();

    /**
     * 1、获取未完工的生产订单的“工厂、商品编码”
     * 2、根据“工厂、商品编码、商品工价类型（大货）”获取是否存在“处理状态”是“已确认”的商品道序工价，
     *  如不存在，则增加待办通知：商品XXX在工厂XXX的道序工价还未创建或未确认
     */
    List<ManManufactureOrder> getNotPayProductProcessStepList(ManManufactureOrder entity);

    /**
     * 销售订单进度报表的生产进度报表明细
     */
    List<SaleManufactureOrderProcessFormResponse> getProcessItem(SaleManufactureOrderProcessFormResponse entity);

    /**
     * 生产进度报表
     */
    List<SaleManufactureOrderProcessFormResponse> getProcessForm(SaleManufactureOrderProcessFormResponse entity);

    /**
     * 获取即将逾期生产订单
     * 即将逾期天数取值顺序 1、主表， 2、租户配置表， 3、系统配置表， 4、默认7天
     * 每天定时获取【生产订单为”已确认“状态  & 生产订单主表的“计划完工日期” >= 当前日期 & 生产订单主表的“计划完工日期” <= 当前日期 + 即将预警天数 】的所有生产订单，预警信息：
     * 款号XXX的生产订单XXX即将到期，计划完工日期YYYY/MM/DD
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManufactureOrder> selectToexpireList(ManManufactureOrder manManufactureOrder);

    /**
     * 获取已逾期生产订单
     * 每天定时获取【生产订单为”已确认“状态  &  生产订单的”完工状态“不是”已完工“  &  当前日期 > 生产订单主表的“计划完工日期” 】的所有生产订单，预警信息：
     * 款号XXX的生产订单XXX已逾期，计划完工日期YYYY/MM/DD
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManufactureOrder> selectOverdueList(ManManufactureOrder manManufactureOrder);


    /**
     * 生产进度报表
     *
     * @param manManufactureOrder 生产订单
     * @return 生产订单集合
     */
    List<ManManufactureOrder> selectManManufactureOrderProgressForm(ManManufactureOrder manManufactureOrder);

    /**
     * 生产进度状态报表
     */
    List<ManManufactureOrder> selectStatusReport(ManManufactureOrder manManufactureOrder);

    /**
     * 设置初始计划结束日期
     */
    int setInitialPlanEndDate(ManManufactureOrder manManufactureOrder);
}

package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.ManManufactureOrder;
import com.platform.ems.domain.dto.response.form.ManManuOrderProductTracking;
import com.platform.ems.domain.dto.response.form.ManManufactureOrderProductStatistics;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManManufactureOrderProduct;

/**
 * 生产订单-产品明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-09
 */
public interface ManManufactureOrderProductMapper extends BaseMapper<ManManufactureOrderProduct> {


    ManManufactureOrderProduct selectManManufactureOrderProductById(Long manufactureOrderProductSid);

    List<ManManufactureOrderProduct> selectManManufactureOrderProductList(ManManufactureOrderProduct manManufactureOrderProduct);

    /**
     * 找未排产用的
     */
    List<ManManufactureOrderProduct> selectManManufactureList(ManManufactureOrderProduct manManufactureOrderProduct);

    /**
     * 添加多个
     *
     * @param list List ManManufactureOrderProduct
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureOrderProduct> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManManufactureOrderProduct
     * @return int
     */
    int updateAllById(ManManufactureOrderProduct entity);

    /**
     * 更新多个
     *
     * @param list List ManManufactureOrderProduct
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureOrderProduct> list);

    /**
     * 销售订单明细报表获取 已排产量和待排产量
     1）已排产量：根据“销售订单明细行sid"，从”生产订单产品明细表“中，获取所有此销售订单明细行sid的”计划产量/本次排产量“，进行累加得出（获取除”已作废“状态外的其它状态的所有生产订单）
     2）待排产量：订单量-已排产量
     *
     * @param product ManManufactureOrderProduct
     * @return int
     */
    List<ManManufactureOrderProduct> getPaichanQuantity(ManManufactureOrderProduct product);

    /**
     *  生产订单的计划完工日期晚于合同交期预警
     *  （仅获取”已确认“状态 & ”完工状态“不是”已完工“的生产订单）
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManufactureOrder> selectOverdueContractList();

    /**
     * 获取即将逾期生产订单产品明细
     * 即将逾期天数取值顺序 1、主表， 2、租户配置表， 3、系统配置表， 4、默认7天
     * 每天定时获取【生产订单为”已确认“状态  & 生产订单产品明细表的“计划完工日期” >= 当前日期 & 生产订单产品明细表的“计划完工日期” <= 当前日期 + 即将预警天数 】的所有生产订单产品明细，预警信息：
     * 款号XXX的生产订单XXX即将到期，计划完工日期YYYY/MM/DD
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManufactureOrderProduct> selectToexpireList(ManManufactureOrderProduct manManufactureOrderProduct);

    /**
     * 获取已逾期生产订单产品明细
     * 每天定时获取【生产订单为”已确认“状态  &  生产订单的”完工状态“不是”已完工“  &  当前日期 > 生产订单产品明细表的“计划完工日期” 】的所有生产订单产品明细，预警信息：
     * 款号XXX的生产订单XXX已逾期，计划完工日期YYYY/MM/DD
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManufactureOrderProduct> selectOverdueList(ManManufactureOrderProduct manManufactureOrderProduct);

    /**
     * 生产进度跟踪报表（商品） 按单生产
     *
     * @param request
     * @return 生产进度跟踪报表（商品）集合
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManuOrderProductTracking> selectManufactureOrderProductTrackingListByDan(ManManuOrderProductTracking request);

    /**
     * 生产进度跟踪报表（商品） 按库生产
     *
     * @param request
     * @return 生产进度跟踪报表（商品）集合
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManuOrderProductTracking> selectManufactureOrderProductTrackingListByKu(ManManuOrderProductTracking request);

    /**
     * 查询商品生产统计报表
     *
     * @param manManufactureOrderProduct 请求
     * @return 商品生产统计报表
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManufactureOrderProductStatistics> selectManManufactureOrderProductStatistics(ManManufactureOrderProductStatistics manManufactureOrderProduct);
}

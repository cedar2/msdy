package com.platform.ems.service;

import java.util.HashMap;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasSku;
import com.platform.ems.domain.ManManufactureOrderProduct;
import com.platform.ems.domain.dto.response.form.ManManuOrderProductTracking;
import com.platform.ems.domain.dto.response.form.ManManufactureOrderProductStatistics;

/**
 * 生产订单-产品明细Service接口
 * 
 * @author linhongwei
 * @date 2021-06-09
 */
public interface IManManufactureOrderProductService extends IService<ManManufactureOrderProduct>{
    /**
     * 查询生产订单-产品明细
     * 
     * @param manufactureOrderProductSid 生产订单-产品明细ID
     * @return 生产订单-产品明细
     */
    ManManufactureOrderProduct selectManManufactureOrderProductById(Long manufactureOrderProductSid);

    /**
     * 查询生产订单-产品明细列表
     * 
     * @param manManufactureOrderProduct 生产订单-产品明细
     * @return 生产订单-产品明细集合
     */
    List<ManManufactureOrderProduct> selectManManufactureOrderProductList(ManManufactureOrderProduct manManufactureOrderProduct);

    /**
     * 查询生产订单-产品明细列表
     *
     * @param manufactureOrderSidList 生产订单
     * @return 生产订单-产品明细集合
     */
    List<ManManufactureOrderProduct> selectManManufactureOrderProductListByOrderSid(List<Long> manufactureOrderSidList);

    /**
     * 修改
     *
     * @param manManufactureOrderProduct 生产订单-产品明细集合
     * @return 结果
     */
    int updateManManufactureOrderProduct(ManManufactureOrderProduct manManufactureOrderProduct);

    /**
     * 获取SKU颜色尺码双下拉框
     *
     * @param manManufactureOrderProduct 生产订单-产品明细
     * @return 获取SKU颜色尺码双下拉框
     */
    HashMap<String, List<BasSku>> getSkuList(ManManufactureOrderProduct manManufactureOrderProduct);

    /**
     * 设置计划投产日期
     */
    int setPlanStart(ManManufactureOrderProduct manManufactureOrderProduct);

    /**
     * 设置计划完工日期
     */
    int setPlanEnd(ManManufactureOrderProduct manManufactureOrderProduct);

    /**
     * 设置即将到期提醒天数
     * @param request
     * @return
     */
    int setToexpireDays(ManManufactureOrderProduct request);

    /**
     * 生产进度跟踪报表（商品）
     *
     * @param request
     * @return 生产进度跟踪报表（商品）集合
     */
    List<ManManuOrderProductTracking> selectManufactureOrderProductTrackingList(ManManuOrderProductTracking request);

    /**
     * 查询商品生产统计报表
     *
     * @param manManufactureOrderProduct 请求
     * @return 商品生产统计报表
     */
    List<ManManufactureOrderProductStatistics> selectManManufactureOrderProductStatistics(ManManufactureOrderProductStatistics manManufactureOrderProduct);
}

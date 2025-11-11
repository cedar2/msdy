package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.ManWorkOrderProgressFormData;
import com.platform.ems.domain.dto.request.ManManufactureOrderSetRequest;
import com.platform.ems.domain.dto.response.form.SaleManufactureOrderProcessFormResponse;

/**
 * 生产订单Service接口
 *
 * @author qhq
 * @date 2021-04-10
 */
public interface IManManufactureOrderService extends IService<ManManufactureOrder>{


    /**
     * 按订单：获取即将到期生产订单
     * @param manManufactureOrder
     * @return
     */
    List<ManManufactureOrder> selectExpiringOrderForm(ManManufactureOrder manManufactureOrder);

    /**
     * 按订单：获取已逾期生产订单
     * @param manManufactureOrder
     * @return
     */
    List<ManManufactureOrder> selectOverdueOrderForm(ManManufactureOrder manManufactureOrder);

    /**
     * 查询生产订单
     *
     * @param manufactureOrderSid 生产订单ID
     * @return 生产订单
     */
    ManManufactureOrder selectManManufactureOrderById(Long manufactureOrderSid);

    /**
     * 得到标签信息
     *
     * @param manufactureOrderSid 生产订单ID
     * @return 得到标签信息
     */
    ManManufactureOrder getLabelInfo(Long manufactureOrderSid);

    /**
     * 复制生产订单
     *
     * @param manufactureOrderSid 生产订单ID
     * @return 生产订单
     */
    ManManufactureOrder copyManManufactureOrderById(Long manufactureOrderSid);

    /**
     * 查询生产订单列表
     *
     * @param manManufactureOrder 生产订单
     * @return 生产订单集合
     */
    List<ManManufactureOrder> selectManManufactureOrderList(ManManufactureOrder manManufactureOrder);

    /**
     * 生产进度状态报表
     */
    List<ManManufactureOrder> selectStatusReport(ManManufactureOrder manManufactureOrder);

    /**
     * 新增生产订单
     *
     * @param manManufactureOrder 生产订单
     * @return 结果
     */
    int insertManManufactureOrder(ManManufactureOrder manManufactureOrder);

    /**
     * 修改生产订单
     *
     * @param manManufactureOrder 生产订单
     * @return 结果
     */
    int updateManManufactureOrder(ManManufactureOrder manManufactureOrder);

    /**
     * 批量删除生产订单
     *
     * @param manufactureOrderSids 需要删除的生产订单ID
     * @return 结果
     */
    int deleteManManufactureOrderByIds(List<Long>  manufactureOrderSids);

    /**
     * 获取租户默认配置
     */
    public SysDefaultSettingClient getClientSetting();

    /**
     * 确认前校验-生产订单
     */
    EmsResultEntity verifyCheck(ManManufactureOrder manManufactureOrder);

    /**
     * 确认前校验-生产订单
     */
    EmsResultEntity verifyCheckForm(ManManufactureOrder manManufactureOrder);

    /**
     * 批量确认
     */
    int handleStatus(ManManufactureOrder manManufactureOrder);

    /**
     * 获取BOM明细
     */
    List<TecBomItem> getMaterialInfo(ManManufactureOrder manManufactureOrder);

    /**
     * 生产订单下拉框列表
     */
    List<ManManufactureOrder> getManufactureOrderList();

    /**
     * 变更生产订单
     *
     * @param manManufactureOrder 生产订单
     * @return 结果
     */
    int changeManManufactureOrder(ManManufactureOrder manManufactureOrder);

    /**
     * 作废生产订单
     */
    int cancellationManufactureOrderById(Long manufactureOrderSid);

    /**
     * 完工生产订单
     */
    int completionManufactureOrderById(Long manufactureOrderSid);

    /**
     * 提交前校验-生产订单
     */
    EmsResultEntity verify(Long manufactureOrderSid, String handleStatus);

    /**
     * 工序计划产量校验
     */
    ManManufactureOrderProcess processQuantityVerify(ManManufactureOrder manManufactureOrder);

    /**
     * 审批操作 提交/确认 操作逾期天数
     */
    void confirm(ManManufactureOrder order);

    /**
     * 设置即将到期提醒天数
     * @param manManufactureOrder
     * @return
     */
    int setToexpireDays(ManManufactureOrder manManufactureOrder);

    /**
     * 设置基本信息/头缸信息/首批信息
     * @param manManufactureOrder
     * @return
     */
    int setDateStatus(ManManufactureOrderSetRequest manManufactureOrder);

    /**
     * 设置完工状态
     */
    int setComplateStatus(ManManufactureOrder manManufactureOrder);

    /**
     * 销售订单进度报表的生产进度报表明细
     */
    List<SaleManufactureOrderProcessFormResponse> getProcessItem(SaleManufactureOrderProcessFormResponse entity);

    /**
     * 查询生产进度报表
     *
     * @param
     * @return 生产进度报表
     */
    List<SaleManufactureOrderProcessFormResponse> getProcessForm(SaleManufactureOrderProcessFormResponse entity);

    /**
     * 查询班组生产进度报表
     *
     * @param
     * @return 生产进度报表
     */
    ManWorkOrderProgressFormData selectManManufactureOrderWorkProgress(ManManufactureOrder manManufactureOrder);

    /**
     * 查询生产进度报表
     *
     * @param
     * @return 生产进度报表
     */
    ManWorkOrderProgressFormData selectManManufactureOrderProgress(ManManufactureOrder manManufactureOrder);
}

package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureOrderConcernTaskAttach;

import java.util.List;

/**
 * 生产订单关注事项-附件Service接口
 *
 * @author linhongwei
 * @date 2021-10-20
 */
public interface IManManufactureOrderConcernTaskAttachService extends IService<ManManufactureOrderConcernTaskAttach> {
    /**
     * 查询生产订单关注事项-附件
     *
     * @param attachmentSid 生产订单关注事项-附件ID
     * @return 生产订单关注事项-附件
     */
    public ManManufactureOrderConcernTaskAttach selectManManufactureOrderConcernTaskAttachById(Long attachmentSid);

    /**
     * 查询生产订单关注事项-附件列表
     *
     * @param ManManufactureOrderConcernTaskAttach 生产订单关注事项-附件
     * @return 生产订单关注事项-附件集合
     */
    public List<ManManufactureOrderConcernTaskAttach> selectManManufactureOrderConcernTaskAttachList(ManManufactureOrderConcernTaskAttach ManManufactureOrderConcernTaskAttach);

    /**
     * 新增生产订单关注事项-附件
     *
     * @param ManManufactureOrderConcernTaskAttach 生产订单关注事项-附件
     * @return 结果
     */
    public int insertManManufactureOrderConcernTaskAttach(ManManufactureOrderConcernTaskAttach ManManufactureOrderConcernTaskAttach);

    /**
     * 修改生产订单关注事项-附件
     *
     * @param ManManufactureOrderConcernTaskAttach 生产订单关注事项-附件
     * @return 结果
     */
    public int updateManManufactureOrderConcernTaskAttach(ManManufactureOrderConcernTaskAttach ManManufactureOrderConcernTaskAttach);

    /**
     * 变更生产订单关注事项-附件
     *
     * @param ManManufactureOrderConcernTaskAttach 生产订单关注事项-附件
     * @return 结果
     */
    public int changeManManufactureOrderConcernTaskAttach(ManManufactureOrderConcernTaskAttach ManManufactureOrderConcernTaskAttach);

    /**
     * 批量删除生产订单关注事项-附件
     *
     * @param attachmentSids 需要删除的生产订单关注事项-附件ID
     * @return 结果
     */
    public int deleteManManufactureOrderConcernTaskAttachByIds(List<Long> attachmentSids);

}

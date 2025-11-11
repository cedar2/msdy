package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureOrderAttach;

import java.util.List;

/**
 * 生产订单-附件Service接口
 *
 * @author linhongwei
 * @date 2021-10-20
 */
public interface IManManufactureOrderAttachService extends IService<ManManufactureOrderAttach> {
    /**
     * 查询生产订单-附件
     *
     * @param attachmentSid 生产订单-附件ID
     * @return 生产订单-附件
     */
    public ManManufactureOrderAttach selectManManufactureOrderAttachById(Long attachmentSid);

    /**
     * 查询生产订单-附件列表
     *
     * @param manManufactureOrderAttach 生产订单-附件
     * @return 生产订单-附件集合
     */
    public List<ManManufactureOrderAttach> selectManManufactureOrderAttachList(ManManufactureOrderAttach manManufactureOrderAttach);

    /**
     * 新增生产订单-附件
     *
     * @param manManufactureOrderAttach 生产订单-附件
     * @return 结果
     */
    public int insertManManufactureOrderAttach(ManManufactureOrderAttach manManufactureOrderAttach);

    /**
     * 修改生产订单-附件
     *
     * @param manManufactureOrderAttach 生产订单-附件
     * @return 结果
     */
    public int updateManManufactureOrderAttach(ManManufactureOrderAttach manManufactureOrderAttach);

    /**
     * 变更生产订单-附件
     *
     * @param manManufactureOrderAttach 生产订单-附件
     * @return 结果
     */
    public int changeManManufactureOrderAttach(ManManufactureOrderAttach manManufactureOrderAttach);

    /**
     * 批量删除生产订单-附件
     *
     * @param attachmentSids 需要删除的生产订单-附件ID
     * @return 结果
     */
    public int deleteManManufactureOrderAttachByIds(List<Long> attachmentSids);

}

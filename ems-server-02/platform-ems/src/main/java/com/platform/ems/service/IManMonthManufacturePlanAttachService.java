package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManMonthManufacturePlanAttach;

import java.util.List;

/**
 * 生产月计划-附件Service接口
 *
 * @author linhongwei
 * @date 2021-09-10
 */
public interface IManMonthManufacturePlanAttachService extends IService<ManMonthManufacturePlanAttach> {
    /**
     * 查询生产月计划-附件
     *
     * @param manufacturePlanAttachSid 生产月计划-附件ID
     * @return 生产月计划-附件
     */
    public ManMonthManufacturePlanAttach selectManMonthManufacturePlanAttachById(Long manufacturePlanAttachSid);

    /**
     * 查询生产月计划-附件列表
     *
     * @param manMonthManufacturePlanAttach 生产月计划-附件
     * @return 生产月计划-附件集合
     */
    public List<ManMonthManufacturePlanAttach> selectManMonthManufacturePlanAttachList(ManMonthManufacturePlanAttach manMonthManufacturePlanAttach);

    /**
     * 新增生产月计划-附件
     *
     * @param manMonthManufacturePlanAttach 生产月计划-附件
     * @return 结果
     */
    public int insertManMonthManufacturePlanAttach(ManMonthManufacturePlanAttach manMonthManufacturePlanAttach);

    /**
     * 修改生产月计划-附件
     *
     * @param manMonthManufacturePlanAttach 生产月计划-附件
     * @return 结果
     */
    public int updateManMonthManufacturePlanAttach(ManMonthManufacturePlanAttach manMonthManufacturePlanAttach);

    /**
     * 变更生产月计划-附件
     *
     * @param manMonthManufacturePlanAttach 生产月计划-附件
     * @return 结果
     */
    public int changeManMonthManufacturePlanAttach(ManMonthManufacturePlanAttach manMonthManufacturePlanAttach);

    /**
     * 批量删除生产月计划-附件
     *
     * @param manufacturePlanAttachSids 需要删除的生产月计划-附件ID
     * @return 结果
     */
    public int deleteManMonthManufacturePlanAttachByIds(List<Long> manufacturePlanAttachSids);

}

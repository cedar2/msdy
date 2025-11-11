package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManWeekManufacturePlanAttach;

import java.util.List;

/**
 * 生产周计划-附件Service接口
 *
 * @author linhongwei
 * @date 2021-09-10
 */
public interface IManWeekManufacturePlanAttachService extends IService<ManWeekManufacturePlanAttach> {
    /**
     * 查询生产周计划-附件
     *
     * @param manufacturePlanAttachSid 生产周计划-附件ID
     * @return 生产周计划-附件
     */
    public ManWeekManufacturePlanAttach selectManWeekManufacturePlanAttachById(Long manufacturePlanAttachSid);

    /**
     * 查询生产周计划-附件列表
     *
     * @param manWeekManufacturePlanAttach 生产周计划-附件
     * @return 生产周计划-附件集合
     */
    public List<ManWeekManufacturePlanAttach> selectManWeekManufacturePlanAttachList(ManWeekManufacturePlanAttach manWeekManufacturePlanAttach);

    /**
     * 新增生产周计划-附件
     *
     * @param manWeekManufacturePlanAttach 生产周计划-附件
     * @return 结果
     */
    public int insertManWeekManufacturePlanAttach(ManWeekManufacturePlanAttach manWeekManufacturePlanAttach);

    /**
     * 修改生产周计划-附件
     *
     * @param manWeekManufacturePlanAttach 生产周计划-附件
     * @return 结果
     */
    public int updateManWeekManufacturePlanAttach(ManWeekManufacturePlanAttach manWeekManufacturePlanAttach);

    /**
     * 变更生产周计划-附件
     *
     * @param manWeekManufacturePlanAttach 生产周计划-附件
     * @return 结果
     */
    public int changeManWeekManufacturePlanAttach(ManWeekManufacturePlanAttach manWeekManufacturePlanAttach);

    /**
     * 批量删除生产周计划-附件
     *
     * @param manufacturePlanAttachSids 需要删除的生产周计划-附件ID
     * @return 结果
     */
    public int deleteManWeekManufacturePlanAttachByIds(List<Long> manufacturePlanAttachSids);

}

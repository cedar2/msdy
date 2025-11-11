package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepManufactureStatusCipin;

import java.util.List;

/**
 * 生产状况-次品Service接口
 *
 * @author c
 * @date 2022-03-17
 */
public interface IRepManufactureStatusCipinService extends IService<RepManufactureStatusCipin> {
    /**
     * 查询生产状况-次品
     *
     * @param dataRecordSid 生产状况-次品ID
     * @return 生产状况-次品
     */
    public RepManufactureStatusCipin selectRepManufactureStatusCipinById(Long dataRecordSid);

    /**
     * 查询生产状况-次品列表
     *
     * @param repManufactureStatusCipin 生产状况-次品
     * @return 生产状况-次品集合
     */
    public List<RepManufactureStatusCipin> selectRepManufactureStatusCipinList(RepManufactureStatusCipin repManufactureStatusCipin);

    /**
     * 新增生产状况-次品
     *
     * @param repManufactureStatusCipin 生产状况-次品
     * @return 结果
     */
    public int insertRepManufactureStatusCipin(RepManufactureStatusCipin repManufactureStatusCipin);

    /**
     * 批量删除生产状况-次品
     *
     * @param dataRecordSids 需要删除的生产状况-次品ID
     * @return 结果
     */
    public int deleteRepManufactureStatusCipinByIds(List<Long> dataRecordSids);

}

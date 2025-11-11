package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepManufactureStatusDaipai;

import java.util.List;

/**
 * 生产状况-待排产Service接口
 *
 * @author c
 * @date 2022-03-17
 */
public interface IRepManufactureStatusDaipaiService extends IService<RepManufactureStatusDaipai> {
    /**
     * 查询生产状况-待排产
     *
     * @param dataRecordSid 生产状况-待排产ID
     * @return 生产状况-待排产
     */
    public RepManufactureStatusDaipai selectRepManufactureStatusDaipaiById(Long dataRecordSid);

    /**
     * 查询生产状况-待排产列表
     *
     * @param repManufactureStatusDaipai 生产状况-待排产
     * @return 生产状况-待排产集合
     */
    public List<RepManufactureStatusDaipai> selectRepManufactureStatusDaipaiList(RepManufactureStatusDaipai repManufactureStatusDaipai);

    /**
     * 新增生产状况-待排产
     *
     * @param repManufactureStatusDaipai 生产状况-待排产
     * @return 结果
     */
    public int insertRepManufactureStatusDaipai(RepManufactureStatusDaipai repManufactureStatusDaipai);

    /**
     * 批量删除生产状况-待排产
     *
     * @param dataRecordSids 需要删除的生产状况-待排产ID
     * @return 结果
     */
    public int deleteRepManufactureStatusDaipaiByIds(List<Long> dataRecordSids);

}

package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepManufactureStatusZaicha;

import java.util.List;

/**
 * 生产状况-在产Service接口
 *
 * @author c
 * @date 2022-03-17
 */
public interface IRepManufactureStatusZaichaService extends IService<RepManufactureStatusZaicha> {
    /**
     * 查询生产状况-在产
     *
     * @param dataRecordSid 生产状况-在产ID
     * @return 生产状况-在产
     */
    public RepManufactureStatusZaicha selectRepManufactureStatusZaichaById(Long dataRecordSid);

    /**
     * 查询生产状况-在产列表
     *
     * @param repManufactureStatusZaicha 生产状况-在产
     * @return 生产状况-在产集合
     */
    public List<RepManufactureStatusZaicha> selectRepManufactureStatusZaichaList(RepManufactureStatusZaicha repManufactureStatusZaicha);

    /**
     * 新增生产状况-在产
     *
     * @param repManufactureStatusZaicha 生产状况-在产
     * @return 结果
     */
    public int insertRepManufactureStatusZaicha(RepManufactureStatusZaicha repManufactureStatusZaicha);

    /**
     * 批量删除生产状况-在产
     *
     * @param dataRecordSids 需要删除的生产状况-在产ID
     * @return 结果
     */
    public int deleteRepManufactureStatusZaichaByIds(List<Long> dataRecordSids);

}

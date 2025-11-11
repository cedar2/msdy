package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManDayManufactureKuanProgress;
import com.platform.ems.domain.ManDayManufactureProgress;

/**
 * 生产进度日报-款生产进度Service接口
 *
 * @author chenkw
 * @date 2022-08-03
 */
public interface IManDayManufactureKuanProgressService extends IService<ManDayManufactureKuanProgress> {
    /**
     * 查询生产进度日报-款生产进度
     *
     * @param dayManufactureKuanProgressSid 生产进度日报-款生产进度ID
     * @return 生产进度日报-款生产进度
     */
    ManDayManufactureKuanProgress selectManDayManufactureKuanProgressById(Long dayManufactureKuanProgressSid);

    /**
     * 查询生产进度日报-款生产进度列表
     *
     * @param manDayManufactureKuanProgress 生产进度日报-款生产进度
     * @return 生产进度日报-款生产进度集合
     */
    List<ManDayManufactureKuanProgress> selectManDayManufactureKuanProgressList(ManDayManufactureKuanProgress manDayManufactureKuanProgress);

    /**
     * 新增生产进度日报-款生产进度
     *
     * @param manDayManufactureKuanProgress 生产进度日报-款生产进度
     * @return 结果
     */
    int insertManDayManufactureKuanProgress(ManDayManufactureKuanProgress manDayManufactureKuanProgress);

    /**
     * 修改生产进度日报-款生产进度
     *
     * @param manDayManufactureKuanProgress 生产进度日报-款生产进度
     * @return 结果
     */
    int updateManDayManufactureKuanProgress(ManDayManufactureKuanProgress manDayManufactureKuanProgress);

    /**
     * 变更生产进度日报-款生产进度
     *
     * @param manDayManufactureKuanProgress 生产进度日报-款生产进度
     * @return 结果
     */
    int changeManDayManufactureKuanProgress(ManDayManufactureKuanProgress manDayManufactureKuanProgress);

    /**
     * 批量删除生产进度日报-款生产进度
     *
     * @param dayManufactureKuanProgressSids 需要删除的生产进度日报-款生产进度ID
     * @return 结果
     */
    int deleteManDayManufactureKuanProgressByIds(List<Long> dayManufactureKuanProgressSids);

    /**
     * 根据生产进度日报明细查询生产进度日报-款生产进度列表
     *
     * @param manDayManufactureProgress 生产进度日报
     * @return 生产进度日报-款生产进度集合
     */
    List<ManDayManufactureKuanProgress> selectManDayManufactureProgressKuanList(ManDayManufactureProgress manDayManufactureProgress);

}

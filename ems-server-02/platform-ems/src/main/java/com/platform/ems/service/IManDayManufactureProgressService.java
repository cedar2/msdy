package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.ManDayManufactureProgress;
import com.platform.ems.domain.base.EmsResultEntity;

/**
 * 生产进度日报Service接口
 *
 * @author linhongwei
 * @date 2021-06-09
 */
public interface IManDayManufactureProgressService extends IService<ManDayManufactureProgress>{
    /**
     * 查询生产进度日报
     *
     * @param dayManufactureProgressSid 生产进度日报ID
     * @return 生产进度日报
     */
    public ManDayManufactureProgress selectManDayManufactureProgressById(Long dayManufactureProgressSid);

    /**
     * 查询生产进度日报列表
     *
     * @param manDayManufactureProgress 生产进度日报
     * @return 生产进度日报集合
     */
    public List<ManDayManufactureProgress> selectManDayManufactureProgressList(ManDayManufactureProgress manDayManufactureProgress);

    /**
     * 新增生产进度日报
     *
     * @param manDayManufactureProgress 生产进度日报
     * @return 结果
     */
    public EmsResultEntity insertManDayManufactureProgress(ManDayManufactureProgress manDayManufactureProgress);

    /**
     * 新增/编辑直接提交生产进度日报
     *
     * @param manDayManufactureProgress 生产进度日报
     * @return 结果
     */
    public AjaxResult submit(ManDayManufactureProgress manDayManufactureProgress);

    /**
     * 修改生产进度日报
     *
     * @param manDayManufactureProgress 生产进度日报
     * @return 结果
     */
    public int updateManDayManufactureProgress(ManDayManufactureProgress manDayManufactureProgress);

    /**
     * 变更生产进度日报
     *
     * @param manDayManufactureProgress 生产进度日报
     * @return 结果
     */
    public int changeManDayManufactureProgress(ManDayManufactureProgress manDayManufactureProgress);

    /**
     * 批量删除生产进度日报
     *
     * @param dayManufactureProgressSids 需要删除的生产进度日报ID
     * @return 结果
     */
    public int deleteManDayManufactureProgressByIds(List<Long> dayManufactureProgressSids);

    /**
     * 更改确认状态
     * @param manDayManufactureProgress
     * @return
     */
    int check(ManDayManufactureProgress manDayManufactureProgress);

    int verify(Long dayManufactureProgressSid, String handleStatus);

}

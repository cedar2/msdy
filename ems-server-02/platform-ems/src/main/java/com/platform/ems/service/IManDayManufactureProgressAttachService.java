package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManDayManufactureProgressAttach;

/**
 * 生产进度日报-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-06-09
 */
public interface IManDayManufactureProgressAttachService extends IService<ManDayManufactureProgressAttach>{
    /**
     * 查询生产进度日报-附件
     * 
     * @param dayManufactureProgressAttachSid 生产进度日报-附件ID
     * @return 生产进度日报-附件
     */
    public ManDayManufactureProgressAttach selectManDayManufactureProgressAttachById(Long dayManufactureProgressAttachSid);

    /**
     * 查询生产进度日报-附件列表
     * 
     * @param manDayManufactureProgressAttach 生产进度日报-附件
     * @return 生产进度日报-附件集合
     */
    public List<ManDayManufactureProgressAttach> selectManDayManufactureProgressAttachList(ManDayManufactureProgressAttach manDayManufactureProgressAttach);

    /**
     * 新增生产进度日报-附件
     * 
     * @param manDayManufactureProgressAttach 生产进度日报-附件
     * @return 结果
     */
    public int insertManDayManufactureProgressAttach(ManDayManufactureProgressAttach manDayManufactureProgressAttach);

    /**
     * 修改生产进度日报-附件
     * 
     * @param manDayManufactureProgressAttach 生产进度日报-附件
     * @return 结果
     */
    public int updateManDayManufactureProgressAttach(ManDayManufactureProgressAttach manDayManufactureProgressAttach);

    /**
     * 变更生产进度日报-附件
     *
     * @param manDayManufactureProgressAttach 生产进度日报-附件
     * @return 结果
     */
    public int changeManDayManufactureProgressAttach(ManDayManufactureProgressAttach manDayManufactureProgressAttach);

    /**
     * 批量删除生产进度日报-附件
     * 
     * @param dayManufactureProgressAttachSids 需要删除的生产进度日报-附件ID
     * @return 结果
     */
    public int deleteManDayManufactureProgressAttachByIds(List<Long> dayManufactureProgressAttachSids);

    /**
     * 更改确认状态
     * @param manDayManufactureProgressAttach
     * @return
     */
    int check(ManDayManufactureProgressAttach manDayManufactureProgressAttach);

}

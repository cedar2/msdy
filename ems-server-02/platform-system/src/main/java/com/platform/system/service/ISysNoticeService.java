package com.platform.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.system.domain.SysNotice;

/**
 * 公告 服务层
 *
 * @author platform
 */
public interface ISysNoticeService extends IService<SysNotice> {
    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    public SysNotice selectNoticeById(Long noticeId);

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    public List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    public int insertNotice(SysNotice notice);

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    public int updateNotice(SysNotice notice);

    /**
     * 删除公告信息
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    public int deleteNoticeById(Long noticeId);

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    public int deleteNoticeByIds(Long[] noticeIds);

    /**
     * 查询通知公告
     *
     * @param noticeSid 通知公告ID
     * @return 通知公告
     */
    public SysNotice selectSysNoticeById(Long noticeSid);

    /**
     * 查询通知公告列表
     *
     * @param sysNotice 通知公告
     * @return 通知公告集合
     */
    public List<SysNotice> selectSysNoticeList(SysNotice sysNotice);

    /**
     * 新增通知公告
     *
     * @param sysNotice 通知公告
     * @return 结果
     */
    public int insertSysNotice(SysNotice sysNotice);

    /**
     * 修改通知公告
     *
     * @param sysNotice 通知公告
     * @return 结果
     */
    public int updateSysNotice(SysNotice sysNotice);

    /**
     * 变更通知公告
     *
     * @param sysNotice 通知公告
     * @return 结果
     */
    public int changeSysNotice(SysNotice sysNotice);

    /**
     * 批量删除通知公告
     *
     * @param noticeSids 需要删除的通知公告ID
     * @return 结果
     */
    public int deleteSysNoticeByIds(List<Long> noticeSids);

    /**
     * 启用/停用
     *
     * @param sysNotice
     * @return
     */
    int changeStatus(SysNotice sysNotice);

    /**
     * 更改确认状态
     *
     * @param sysNotice
     * @return
     */
    int check(SysNotice sysNotice);

    /**
     * 待办、预警消息条数
     */
    SysNotice countMessage(SysNotice sysNotice);
}

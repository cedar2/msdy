package com.platform.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.system.domain.SysNotice;
import org.apache.ibatis.annotations.Param;

/**
 * 通知公告表 数据层
 *
 * @author platform
 */
public interface SysNoticeMapper extends BaseMapper<SysNotice>
{
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
     * 批量删除公告
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

    SysNotice selectSysNoticeById(Long noticeSid);

    List<SysNotice> selectSysNoticeList(SysNotice sysNotice);

    /**
     * 添加多个
     * @param list List SysNotice
     * @return int
     */
    int inserts(@Param("list") List<SysNotice> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity SysNotice
     * @return int
     */
    int updateAllById(SysNotice entity);

    /**
     * 更新多个
     * @param list List SysNotice
     * @return int
     */
    int updatesAllById(@Param("list") List<SysNotice> list);

}

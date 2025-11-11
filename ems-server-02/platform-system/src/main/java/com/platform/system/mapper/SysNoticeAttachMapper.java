package com.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.system.domain.SysNoticeAttach;

/**
 * 通知公告-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-30
 */
public interface SysNoticeAttachMapper  extends BaseMapper<SysNoticeAttach> {

    SysNoticeAttach selectSysNoticeAttachById(Long noticeAttachSid);

    List<SysNoticeAttach> selectSysNoticeAttachList(SysNoticeAttach sysNoticeAttach);

    /**
     * 添加多个
     * @param list List SysNoticeAttach
     * @return int
     */
    int inserts(@Param("list") List<SysNoticeAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SysNoticeAttach
    * @return int
    */
    int updateAllById(SysNoticeAttach entity);

    /**
     * 更新多个
     * @param list List SysNoticeAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<SysNoticeAttach> list);


}

package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmArrivalNoticeAttach;

/**
 * 到货通知单-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-12-13
 */
public interface FrmArrivalNoticeAttachMapper extends BaseMapper<FrmArrivalNoticeAttach> {

    FrmArrivalNoticeAttach selectFrmArrivalNoticeAttachById(Long arrivalNoticeAttachSid);

    List<FrmArrivalNoticeAttach> selectFrmArrivalNoticeAttachList(FrmArrivalNoticeAttach frmArrivalNoticeAttach);

    /**
     * 添加多个
     *
     * @param list List FrmArrivalNoticeAttach
     * @return int
     */
    int inserts(@Param("list") List<FrmArrivalNoticeAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmArrivalNoticeAttach
     * @return int
     */
    int updateAllById(FrmArrivalNoticeAttach entity);

    /**
     * 更新多个
     *
     * @param list List FrmArrivalNoticeAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmArrivalNoticeAttach> list);

}

package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmArrivalNotice;

/**
 * 到货通知单Mapper接口
 *
 * @author chenkw
 * @date 2022-12-13
 */
public interface FrmArrivalNoticeMapper extends BaseMapper<FrmArrivalNotice> {

    FrmArrivalNotice selectFrmArrivalNoticeById(Long arrivalNoticeSid);

    List<FrmArrivalNotice> selectFrmArrivalNoticeList(FrmArrivalNotice frmArrivalNotice);

    /**
     * 添加多个
     *
     * @param list List FrmArrivalNotice
     * @return int
     */
    int inserts(@Param("list") List<FrmArrivalNotice> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmArrivalNotice
     * @return int
     */
    int updateAllById(FrmArrivalNotice entity);

    /**
     * 更新多个
     *
     * @param list List FrmArrivalNotice
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmArrivalNotice> list);

}

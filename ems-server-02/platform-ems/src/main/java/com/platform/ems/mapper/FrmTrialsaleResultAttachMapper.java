package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmTrialsaleResultAttach;

/**
 * 试销结果单-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-12-19
 */
public interface FrmTrialsaleResultAttachMapper extends BaseMapper<FrmTrialsaleResultAttach> {

    FrmTrialsaleResultAttach selectFrmTrialsaleResultAttachById(Long trialsaleResultAttachSid);

    List<FrmTrialsaleResultAttach> selectFrmTrialsaleResultAttachList(FrmTrialsaleResultAttach frmTrialsaleResultAttach);

    /**
     * 添加多个
     *
     * @param list List FrmTrialsaleResultAttach
     * @return int
     */
    int inserts(@Param("list") List<FrmTrialsaleResultAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmTrialsaleResultAttach
     * @return int
     */
    int updateAllById(FrmTrialsaleResultAttach entity);

    /**
     * 更新多个
     *
     * @param list List FrmTrialsaleResultAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmTrialsaleResultAttach> list);

}

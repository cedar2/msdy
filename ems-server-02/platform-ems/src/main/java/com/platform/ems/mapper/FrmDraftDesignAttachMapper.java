package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmDraftDesignAttach;

/**
 * 图稿绘制单-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-12-12
 */
public interface FrmDraftDesignAttachMapper extends BaseMapper<FrmDraftDesignAttach> {

    FrmDraftDesignAttach selectFrmDraftDesignAttachById(Long draftDesignAttachSid);

    List<FrmDraftDesignAttach> selectFrmDraftDesignAttachList(FrmDraftDesignAttach frmDraftDesignAttach);

    /**
     * 添加多个
     *
     * @param list List FrmDraftDesignAttach
     * @return int
     */
    int inserts(@Param("list") List<FrmDraftDesignAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmDraftDesignAttach
     * @return int
     */
    int updateAllById(FrmDraftDesignAttach entity);

    /**
     * 更新多个
     *
     * @param list List FrmDraftDesignAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmDraftDesignAttach> list);

}

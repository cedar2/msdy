package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmPhotoSampleGainAttach;

/**
 * 拍照样获取单-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-12-13
 */
public interface FrmPhotoSampleGainAttachMapper extends BaseMapper<FrmPhotoSampleGainAttach> {

    FrmPhotoSampleGainAttach selectFrmPhotoSampleGainAttachById(Long photoSampleGainAttachSid);

    List<FrmPhotoSampleGainAttach> selectFrmPhotoSampleGainAttachList(FrmPhotoSampleGainAttach frmPhotoSampleGainAttach);

    /**
     * 添加多个
     *
     * @param list List FrmPhotoSampleGainAttach
     * @return int
     */
    int inserts(@Param("list") List<FrmPhotoSampleGainAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmPhotoSampleGainAttach
     * @return int
     */
    int updateAllById(FrmPhotoSampleGainAttach entity);

    /**
     * 更新多个
     *
     * @param list List FrmPhotoSampleGainAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmPhotoSampleGainAttach> list);

}

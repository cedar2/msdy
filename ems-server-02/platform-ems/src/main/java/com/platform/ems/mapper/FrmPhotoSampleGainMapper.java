package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmPhotoSampleGain;

/**
 * 视觉设计单Mapper接口
 *
 * @author chenkw
 * @date 2022-12-13
 */
public interface FrmPhotoSampleGainMapper extends BaseMapper<FrmPhotoSampleGain> {

    FrmPhotoSampleGain selectFrmPhotoSampleGainById(Long photoSampleGainSid);

    List<FrmPhotoSampleGain> selectFrmPhotoSampleGainList(FrmPhotoSampleGain frmPhotoSampleGain);

    /**
     * 添加多个
     *
     * @param list List FrmPhotoSampleGain
     * @return int
     */
    int inserts(@Param("list") List<FrmPhotoSampleGain> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmPhotoSampleGain
     * @return int
     */
    int updateAllById(FrmPhotoSampleGain entity);

    /**
     * 更新多个
     *
     * @param list List FrmPhotoSampleGain
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmPhotoSampleGain> list);

}

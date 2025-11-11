package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmPhotoSampleGain;
import com.platform.ems.domain.base.EmsResultEntity;

/**
 * 视觉设计单Service接口
 *
 * @author chenkw
 * @date 2022-12-13
 */
public interface IFrmPhotoSampleGainService extends IService<FrmPhotoSampleGain> {
    /**
     * 查询视觉设计单
     *
     * @param photoSampleGainSid 视觉设计单ID
     * @return 视觉设计单
     */
    public FrmPhotoSampleGain selectFrmPhotoSampleGainById(Long photoSampleGainSid);

    /**
     * 查询视觉设计单列表
     *
     * @param frmPhotoSampleGain 视觉设计单
     * @return 视觉设计单集合
     */
    public List<FrmPhotoSampleGain> selectFrmPhotoSampleGainList(FrmPhotoSampleGain frmPhotoSampleGain);

    /**
     * 新增视觉设计单
     *
     * @param frmPhotoSampleGain 视觉设计单
     * @return 结果
     */
    public int insertFrmPhotoSampleGain(FrmPhotoSampleGain frmPhotoSampleGain);

    /**
     * 修改视觉设计单
     *
     * @param frmPhotoSampleGain 视觉设计单
     * @return 结果
     */
    public int updateFrmPhotoSampleGain(FrmPhotoSampleGain frmPhotoSampleGain);

    /**
     * 变更视觉设计单
     *
     * @param frmPhotoSampleGain 视觉设计单
     * @return 结果
     */
    public int changeFrmPhotoSampleGain(FrmPhotoSampleGain frmPhotoSampleGain);

    /**
     * 批量删除视觉设计单
     *
     * @param photoSampleGainSids 需要删除的视觉设计单ID
     * @return 结果
     */
    public int deleteFrmPhotoSampleGainByIds(List<Long> photoSampleGainSids);

    /**
     * 提交前校验
     *
     * @param frmPhotoSampleGain 视觉设计单
     * @return 结果
     */
    public EmsResultEntity submitVerify(FrmPhotoSampleGain frmPhotoSampleGain);

    /**
     * 更改确认状态
     *
     * @param frmPhotoSampleGain
     * @return
     */
    int check(FrmPhotoSampleGain frmPhotoSampleGain);

}

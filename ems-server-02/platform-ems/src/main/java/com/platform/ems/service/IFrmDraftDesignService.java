package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmDraftDesign;
import com.platform.ems.domain.base.EmsResultEntity;

import java.util.List;

/**
 * 图稿绘制单Service接口
 *
 * @author chenkw
 * @date 2022-12-12
 */
public interface IFrmDraftDesignService extends IService<FrmDraftDesign> {
    /**
     * 查询图稿绘制单
     *
     * @param draftDesignSid 图稿绘制单ID
     * @return 图稿绘制单
     */
    public FrmDraftDesign selectFrmDraftDesignById(Long draftDesignSid);

    /**
     * 查询图稿绘制单列表
     *
     * @param frmDraftDesign 图稿绘制单
     * @return 图稿绘制单集合
     */
    public List<FrmDraftDesign> selectFrmDraftDesignListOrderByDesc(FrmDraftDesign frmDraftDesign);

    /**
     * 新增图稿绘制单
     *
     * @param frmDraftDesign 图稿绘制单
     * @return 结果
     */
    public int insertFrmDraftDesign(FrmDraftDesign frmDraftDesign);

    /**
     * 提交前校验
     *
     * @param frmDraftDesign 图稿绘制
     * @return 结果
     */
    public EmsResultEntity submitVerify(FrmDraftDesign frmDraftDesign);

    /**
     * 修改图稿绘制单
     *
     * @param frmDraftDesign 图稿绘制单
     * @return 结果
     */
    public int updateFrmDraftDesign(FrmDraftDesign frmDraftDesign);

    /**
     * 变更图稿绘制单
     *
     * @param frmDraftDesign 图稿绘制单
     * @return 结果
     */
    public int changeFrmDraftDesign(FrmDraftDesign frmDraftDesign);

    /**
     * 批量删除图稿绘制单
     *
     * @param draftDesignSids 需要删除的图稿绘制单ID
     * @return 结果
     */
    public int deleteFrmDraftDesignByIds(List<Long> draftDesignSids);

    /**
     * 更改确认状态
     *
     * @param frmDraftDesign
     * @return
     */
    int check(FrmDraftDesign frmDraftDesign);

    /**
     * 更改确认状态
     *
     * @param frmDraftDesign
     * @return
     */
    int approval(FrmDraftDesign frmDraftDesign);

}

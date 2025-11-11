package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmDocumentVision;
import com.platform.ems.domain.base.EmsResultEntity;

/**
 * 文案脚本单Service接口
 * 
 * @author chenkw
 * @date 2022-12-13
 */
public interface IFrmDocumentVisionService extends IService<FrmDocumentVision>{
    /**
     * 查询文案脚本单
     * 
     * @param documentVisionSid 文案脚本单ID
     * @return 文案脚本单
     */
    public FrmDocumentVision selectFrmDocumentVisionById(Long documentVisionSid);

    /**
     * 查询文案脚本单列表
     * 
     * @param frmDocumentVision 文案脚本单
     * @return 文案脚本单集合
     */
    public List<FrmDocumentVision> selectFrmDocumentVisionList(FrmDocumentVision frmDocumentVision);

    /**
     * 新增文案脚本单
     * 
     * @param frmDocumentVision 文案脚本单
     * @return 结果
     */
    public int insertFrmDocumentVision(FrmDocumentVision frmDocumentVision);

    /**
     * 修改文案脚本单
     * 
     * @param frmDocumentVision 文案脚本单
     * @return 结果
     */
    public int updateFrmDocumentVision(FrmDocumentVision frmDocumentVision);

    /**
     * 变更文案脚本单
     *
     * @param frmDocumentVision 文案脚本单
     * @return 结果
     */
    public int changeFrmDocumentVision(FrmDocumentVision frmDocumentVision);

    /**
     * 批量删除文案脚本单
     * 
     * @param documentVisionSids 需要删除的文案脚本单ID
     * @return 结果
     */
    public int deleteFrmDocumentVisionByIds(List<Long>  documentVisionSids);

    /**
     * 提交前校验
     *
     * @param frmDocumentVision 新品试销计划单
     * @return 结果
     */
    public EmsResultEntity submitVerify(FrmDocumentVision frmDocumentVision);

    /**
     * 更改确认状态
     * @param frmDocumentVision
     * @return
     */
    int check(FrmDocumentVision frmDocumentVision);

}

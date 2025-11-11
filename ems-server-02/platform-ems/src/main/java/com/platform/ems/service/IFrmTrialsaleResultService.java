package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmTrialsaleResult;
import com.platform.ems.domain.base.EmsResultEntity;

/**
 * 试销结果单Service接口
 *
 * @author chenkw
 * @date 2022-12-19
 */
public interface IFrmTrialsaleResultService extends IService<FrmTrialsaleResult> {
    /**
     * 查询试销结果单
     *
     * @param trialsaleResultSid 试销结果单ID
     * @return 试销结果单
     */
    public FrmTrialsaleResult selectFrmTrialsaleResultById(Long trialsaleResultSid);

    /**
     * 查询试销结果单列表
     *
     * @param frmTrialsaleResult 试销结果单
     * @return 试销结果单集合
     */
    public List<FrmTrialsaleResult> selectFrmTrialsaleResultList(FrmTrialsaleResult frmTrialsaleResult);

    /**
     * 新增试销结果单
     *
     * @param frmTrialsaleResult 试销结果单
     * @return 结果
     */
    public int insertFrmTrialsaleResult(FrmTrialsaleResult frmTrialsaleResult);

    /**
     * 修改试销结果单
     *
     * @param frmTrialsaleResult 试销结果单
     * @return 结果
     */
    public int updateFrmTrialsaleResult(FrmTrialsaleResult frmTrialsaleResult);

    /**
     * 变更试销结果单
     *
     * @param frmTrialsaleResult 试销结果单
     * @return 结果
     */
    public int changeFrmTrialsaleResult(FrmTrialsaleResult frmTrialsaleResult);

    /**
     * 批量删除试销结果单
     *
     * @param trialsaleResultSids 需要删除的试销结果单ID
     * @return 结果
     */
    public int deleteFrmTrialsaleResultByIds(List<Long> trialsaleResultSids);

    /**
     * 提交前校验
     *
     * @param frmTrialsaleResult 试销结果单
     * @return 结果
     */
    public EmsResultEntity submitVerify(FrmTrialsaleResult frmTrialsaleResult);

    /**
     * 更改确认状态
     *
     * @param frmTrialsaleResult
     * @return
     */
    int check(FrmTrialsaleResult frmTrialsaleResult);

}

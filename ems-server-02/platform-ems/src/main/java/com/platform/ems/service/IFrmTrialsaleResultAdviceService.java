package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmTrialsaleResult;
import com.platform.ems.domain.FrmTrialsaleResultAdvice;

/**
 * 试销结果单-优化建议Service接口
 *
 * @author chenkw
 * @date 2022-12-19
 */
public interface IFrmTrialsaleResultAdviceService extends IService<FrmTrialsaleResultAdvice> {
    /**
     * 查询试销结果单-优化建议
     *
     * @param trialsaleResultAdviceSid 试销结果单-优化建议ID
     * @return 试销结果单-优化建议
     */
    public FrmTrialsaleResultAdvice selectFrmTrialsaleResultAdviceById(Long trialsaleResultAdviceSid);

    /**
     * 查询试销结果单-优化建议列表
     *
     * @param frmTrialsaleResultAdvice 试销结果单-优化建议
     * @return 试销结果单-优化建议集合
     */
    public List<FrmTrialsaleResultAdvice> selectFrmTrialsaleResultAdviceList(FrmTrialsaleResultAdvice frmTrialsaleResultAdvice);

    /**
     * 新增试销结果单-优化建议
     *
     * @param frmTrialsaleResultAdvice 试销结果单-优化建议
     * @return 结果
     */
    public int insertFrmTrialsaleResultAdvice(FrmTrialsaleResultAdvice frmTrialsaleResultAdvice);

    /**
     * 修改试销结果单-优化建议
     *
     * @param frmTrialsaleResultAdvice 试销结果单-优化建议
     * @return 结果
     */
    public int updateFrmTrialsaleResultAdvice(FrmTrialsaleResultAdvice frmTrialsaleResultAdvice);

    /**
     * 变更试销结果单-优化建议
     *
     * @param frmTrialsaleResultAdvice 试销结果单-优化建议
     * @return 结果
     */
    public int changeFrmTrialsaleResultAdvice(FrmTrialsaleResultAdvice frmTrialsaleResultAdvice);

    /**
     * 批量删除试销结果单-优化建议
     *
     * @param trialsaleResultAdviceSids 需要删除的试销结果单-优化建议ID
     * @return 结果
     */
    public int deleteFrmTrialsaleResultAdviceByIds(List<Long> trialsaleResultAdviceSids);

    /**
     * 查询新品试销结果单-优化建议
     *
     * @param trialsaleResultSid 新品试销结果单-主表ID
     * @return 新品试销结果单-优化建议
     */
    public List<FrmTrialsaleResultAdvice> selectFrmTrialsaleResultAdviceListById(Long trialsaleResultSid);

    /**
     * 批量新增新品试销结果单-优化建议
     *
     * @param result 新品试销结果单
     * @return 结果
     */
    public int insertFrmTrialsaleResultAdviceList(FrmTrialsaleResult result);

    /**
     * 批量修改新品试销结果单-优化建议
     *
     * @param result 新品试销结果单
     * @return 结果
     */
    public int updateFrmTrialsaleResultAdviceList(FrmTrialsaleResult result);

    /**
     * 批量删除新品试销结果单-优化建议
     *
     * @param adviceList 需要删除的项新品试销结果单-优化建议
     * @return 结果
     */
    public int deleteFrmTrialsaleResultAdviceByList(List<FrmTrialsaleResultAdvice> adviceList);

    /**
     * 批量删除新品试销结果单-优化建议 根据主表sids
     *
     * @param trialsaleResultSidList 需要删除的新品试销结果单sids
     * @return 结果
     */
    public int deleteFrmTrialsaleResultAdviceByPlan(List<Long> trialsaleResultSidList);

}

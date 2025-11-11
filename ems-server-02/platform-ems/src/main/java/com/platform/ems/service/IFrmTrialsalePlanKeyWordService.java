package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmNewproductTrialsalePlan;
import com.platform.ems.domain.FrmTrialsalePlanKeyWord;

/**
 * 新品试销计划单-关键词分析Service接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface IFrmTrialsalePlanKeyWordService extends IService<FrmTrialsalePlanKeyWord> {
    /**
     * 查询新品试销计划单-关键词分析
     *
     * @param trialsalePlanKeyWordSid 新品试销计划单-关键词分析ID
     * @return 新品试销计划单-关键词分析
     */
    public FrmTrialsalePlanKeyWord selectFrmTrialsalePlanKeyWordById(Long trialsalePlanKeyWordSid);

    /**
     * 查询新品试销计划单-关键词分析列表
     *
     * @param frmTrialsalePlanKeyWord 新品试销计划单-关键词分析
     * @return 新品试销计划单-关键词分析集合
     */
    public List<FrmTrialsalePlanKeyWord> selectFrmTrialsalePlanKeyWordList(FrmTrialsalePlanKeyWord frmTrialsalePlanKeyWord);

    /**
     * 新增新品试销计划单-关键词分析
     *
     * @param frmTrialsalePlanKeyWord 新品试销计划单-关键词分析
     * @return 结果
     */
    public int insertFrmTrialsalePlanKeyWord(FrmTrialsalePlanKeyWord frmTrialsalePlanKeyWord);

    /**
     * 修改新品试销计划单-关键词分析
     *
     * @param frmTrialsalePlanKeyWord 新品试销计划单-关键词分析
     * @return 结果
     */
    public int updateFrmTrialsalePlanKeyWord(FrmTrialsalePlanKeyWord frmTrialsalePlanKeyWord);

    /**
     * 变更新品试销计划单-关键词分析
     *
     * @param frmTrialsalePlanKeyWord 新品试销计划单-关键词分析
     * @return 结果
     */
    public int changeFrmTrialsalePlanKeyWord(FrmTrialsalePlanKeyWord frmTrialsalePlanKeyWord);

    /**
     * 批量删除新品试销计划单-关键词分析
     *
     * @param trialsalePlanKeyWordSids 需要删除的新品试销计划单-关键词分析ID
     * @return 结果
     */
    public int deleteFrmTrialsalePlanKeyWordByIds(List<Long> trialsalePlanKeyWordSids);

    /**
     * 查询新品试销计划单-关键词分析
     *
     * @param newproductTrialsalePlanSid 新品试销计划单-主表ID
     * @return 新品试销计划单-关键词分析
     */
    public List<FrmTrialsalePlanKeyWord> selectFrmTrialsalePlanKeyWordListById(Long newproductTrialsalePlanSid);

    /**
     * 批量新增新品试销计划单-关键词分析
     *
     * @param plan 新品试销计划单
     * @return 结果
     */
    public int insertFrmTrialsalePlanKeyWordList(FrmNewproductTrialsalePlan plan);

    /**
     * 批量修改新品试销计划单-关键词分析
     *
     * @param plan 新品试销计划单
     * @return 结果
     */
    public int updateFrmTrialsalePlanKeyWordList(FrmNewproductTrialsalePlan plan);

    /**
     * 批量删除新品试销计划单-关键词分析
     *
     * @param keyWordList 需要删除的项新品试销计划单-关键词分析
     * @return 结果
     */
    public int deleteFrmTrialsalePlanKeyWordByList(List<FrmTrialsalePlanKeyWord> keyWordList);

    /**
     * 批量删除新品试销计划单-关键词分析 根据主表sids
     *
     * @param newproductTrialsalePlanSidList 需要删除的新品试销计划单sids
     * @return 结果
     */
    public int deleteFrmTrialsalePlanKeyWordByPlan(List<Long> newproductTrialsalePlanSidList);

}

package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmTrialsaleResult;
import com.platform.ems.domain.FrmTrialsaleResultPriceScheme;

/**
 * 试销结果单-定价方案Service接口
 *
 * @author chenkw
 * @date 2022-12-19
 */
public interface IFrmTrialsaleResultPriceSchemeService extends IService<FrmTrialsaleResultPriceScheme> {
    /**
     * 查询试销结果单-定价方案
     *
     * @param trialsaleResultPriceSchemeSid 试销结果单-定价方案ID
     * @return 试销结果单-定价方案
     */
    public FrmTrialsaleResultPriceScheme selectFrmTrialsaleResultPriceSchemeById(Long trialsaleResultPriceSchemeSid);

    /**
     * 查询试销结果单-定价方案列表
     *
     * @param frmTrialsaleResultPriceScheme 试销结果单-定价方案
     * @return 试销结果单-定价方案集合
     */
    public List<FrmTrialsaleResultPriceScheme> selectFrmTrialsaleResultPriceSchemeList(FrmTrialsaleResultPriceScheme frmTrialsaleResultPriceScheme);

    /**
     * 新增试销结果单-定价方案
     *
     * @param frmTrialsaleResultPriceScheme 试销结果单-定价方案
     * @return 结果
     */
    public int insertFrmTrialsaleResultPriceScheme(FrmTrialsaleResultPriceScheme frmTrialsaleResultPriceScheme);

    /**
     * 修改试销结果单-定价方案
     *
     * @param frmTrialsaleResultPriceScheme 试销结果单-定价方案
     * @return 结果
     */
    public int updateFrmTrialsaleResultPriceScheme(FrmTrialsaleResultPriceScheme frmTrialsaleResultPriceScheme);

    /**
     * 变更试销结果单-定价方案
     *
     * @param frmTrialsaleResultPriceScheme 试销结果单-定价方案
     * @return 结果
     */
    public int changeFrmTrialsaleResultPriceScheme(FrmTrialsaleResultPriceScheme frmTrialsaleResultPriceScheme);

    /**
     * 批量删除试销结果单-定价方案
     *
     * @param trialsaleResultPriceSchemeSids 需要删除的试销结果单-定价方案ID
     * @return 结果
     */
    public int deleteFrmTrialsaleResultPriceSchemeByIds(List<Long> trialsaleResultPriceSchemeSids);

    /**
     * 查询新品试销结果单-定价方案
     *
     * @param trialsaleResultSid 新品试销结果单-主表ID
     * @return 新品试销结果单-定价方案
     */
    public List<FrmTrialsaleResultPriceScheme> selectFrmTrialsaleResultPriceSchemeListById(Long trialsaleResultSid);

    /**
     * 批量新增新品试销结果单-定价方案
     *
     * @param result 新品试销结果单
     * @return 结果
     */
    public int insertFrmTrialsaleResultPriceSchemeList(FrmTrialsaleResult result);

    /**
     * 批量修改新品试销结果单-定价方案
     *
     * @param result 新品试销结果单
     * @return 结果
     */
    public int updateFrmTrialsaleResultPriceSchemeList(FrmTrialsaleResult result);

    /**
     * 批量删除新品试销结果单-定价方案
     *
     * @param priceSchemeList 需要删除的项新品试销结果单-定价方案
     * @return 结果
     */
    public int deleteFrmTrialsaleResultPriceSchemeByList(List<FrmTrialsaleResultPriceScheme> priceSchemeList);

    /**
     * 批量删除新品试销结果单-定价方案 根据主表sids
     *
     * @param trialsaleResultSidList 需要删除的新品试销结果单sids
     * @return 结果
     */
    public int deleteFrmTrialsaleResultPriceSchemeByPlan(List<Long> trialsaleResultSidList);
}

package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmNewproductTrialsalePlan;
import com.platform.ems.domain.base.EmsResultEntity;

/**
 * 新品试销计划单Service接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface IFrmNewproductTrialsalePlanService extends IService<FrmNewproductTrialsalePlan> {
    /**
     * 查询新品试销计划单
     *
     * @param newproductTrialsalePlanSid 新品试销计划单ID
     * @return 新品试销计划单
     */
    public FrmNewproductTrialsalePlan selectFrmNewproductTrialsalePlanById(Long newproductTrialsalePlanSid);

    /**
     * 查询新品试销计划单列表
     *
     * @param frmNewproductTrialsalePlan 新品试销计划单
     * @return 新品试销计划单集合
     */
    public List<FrmNewproductTrialsalePlan> selectFrmNewproductTrialsalePlanList(FrmNewproductTrialsalePlan frmNewproductTrialsalePlan);

    /**
     * 新增新品试销计划单
     *
     * @param frmNewproductTrialsalePlan 新品试销计划单
     * @return 结果
     */
    public int insertFrmNewproductTrialsalePlan(FrmNewproductTrialsalePlan frmNewproductTrialsalePlan);

    /**
     * 修改新品试销计划单
     *
     * @param frmNewproductTrialsalePlan 新品试销计划单
     * @return 结果
     */
    public int updateFrmNewproductTrialsalePlan(FrmNewproductTrialsalePlan frmNewproductTrialsalePlan);

    /**
     * 变更新品试销计划单
     *
     * @param frmNewproductTrialsalePlan 新品试销计划单
     * @return 结果
     */
    public int changeFrmNewproductTrialsalePlan(FrmNewproductTrialsalePlan frmNewproductTrialsalePlan);

    /**
     * 批量删除新品试销计划单
     *
     * @param newproductTrialsalePlanSids 需要删除的新品试销计划单ID
     * @return 结果
     */
    public int deleteFrmNewproductTrialsalePlanByIds(List<Long> newproductTrialsalePlanSids);

    /**
     * 提交前校验
     *
     * @param frmNewproductTrialsalePlan 新品试销计划单
     * @return 结果
     */
    public EmsResultEntity submitVerify(FrmNewproductTrialsalePlan frmNewproductTrialsalePlan);

    /**
     * 更改确认状态
     *
     * @param frmNewproductTrialsalePlan
     * @return
     */
    int check(FrmNewproductTrialsalePlan frmNewproductTrialsalePlan);

}

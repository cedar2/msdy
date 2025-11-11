package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmNewproductTrialsalePlanAttach;

/**
 * 新品试销计划单-附件Service接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface IFrmNewproductTrialsalePlanAttachService extends IService<FrmNewproductTrialsalePlanAttach> {
    /**
     * 查询新品试销计划单-附件
     *
     * @param newproductTrialsalePlanAttachSid 新品试销计划单-附件ID
     * @return 新品试销计划单-附件
     */
    public FrmNewproductTrialsalePlanAttach selectFrmNewproductTrialsalePlanAttachById(Long newproductTrialsalePlanAttachSid);

    /**
     * 查询新品试销计划单-附件列表
     *
     * @param frmNewproductTrialsalePlanAttach 新品试销计划单-附件
     * @return 新品试销计划单-附件集合
     */
    public List<FrmNewproductTrialsalePlanAttach> selectFrmNewproductTrialsalePlanAttachList(FrmNewproductTrialsalePlanAttach frmNewproductTrialsalePlanAttach);

    /**
     * 新增新品试销计划单-附件
     *
     * @param frmNewproductTrialsalePlanAttach 新品试销计划单-附件
     * @return 结果
     */
    public int insertFrmNewproductTrialsalePlanAttach(FrmNewproductTrialsalePlanAttach frmNewproductTrialsalePlanAttach);

    /**
     * 修改新品试销计划单-附件
     *
     * @param frmNewproductTrialsalePlanAttach 新品试销计划单-附件
     * @return 结果
     */
    public int updateFrmNewproductTrialsalePlanAttach(FrmNewproductTrialsalePlanAttach frmNewproductTrialsalePlanAttach);

    /**
     * 变更新品试销计划单-附件
     *
     * @param frmNewproductTrialsalePlanAttach 新品试销计划单-附件
     * @return 结果
     */
    public int changeFrmNewproductTrialsalePlanAttach(FrmNewproductTrialsalePlanAttach frmNewproductTrialsalePlanAttach);

    /**
     * 批量删除新品试销计划单-附件
     *
     * @param newproductTrialsalePlanAttachSids 需要删除的新品试销计划单-附件ID
     * @return 结果
     */
    public int deleteFrmNewproductTrialsalePlanAttachByIds(List<Long> newproductTrialsalePlanAttachSids);

}

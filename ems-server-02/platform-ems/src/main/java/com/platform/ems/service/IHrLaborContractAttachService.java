package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.HrLaborContractAttach;

import java.util.List;

/**
 * 劳动合同-附件Service接口
 *
 * @author xfzz
 * @date 2024/5/9
 */
public interface IHrLaborContractAttachService extends IService<HrLaborContractAttach> {
    /**
     * 查询劳动合同-附件
     *
     * @param laborContractAttachSid 劳动合同-附件ID
     * @return 劳动合同-附件
     */
    public HrLaborContractAttach selectHrLaborContractAttachById(Long laborContractAttachSid);

    /**
     * 查询劳动合同-附件列表
     *
     * @param hrLaborContractAttach 劳动合同-附件
     * @return 劳动合同-附件集合
     */
    public List<HrLaborContractAttach> selectHrLaborContractAttachList(HrLaborContractAttach hrLaborContractAttach);

    /**
     * 新增劳动合同-附件
     *
     * @param hrLaborContractAttach 劳动合同-附件
     * @return 结果
     */
    public int insertHrLaborContractAttach(HrLaborContractAttach hrLaborContractAttach);

    /**
     * 修改劳动合同-附件
     *
     * @param hrLaborContractAttach 劳动合同-附件
     * @return 结果
     */
    public int updateHrLaborContractAttach(HrLaborContractAttach hrLaborContractAttach);

    /**
     * 变更劳动合同-附件
     *
     * @param hrLaborContractAttach 劳动合同-附件
     * @return 结果
     */
    public int changeHrLaborContractAttach(HrLaborContractAttach hrLaborContractAttach);

    /**
     * 批量删除劳动合同-附件
     *
     * @param laborContractAttachSids 需要删除的劳动合同-附件ID
     * @return 结果
     */
    public int deleteHrLaborContractAttachByIds(List<Long> laborContractAttachSids);

    /**
     * 劳动合同查询页面上传附件前的校验
     * @param hrLaborContractAttach
     * @return
     */
    AjaxResult check(HrLaborContractAttach hrLaborContractAttach);

}

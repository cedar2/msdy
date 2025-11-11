package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.HrDimissionCertificateAttach;

import java.util.List;

/**
 * 离职证明-附件Service接口
 *
 * @author xfzz
 * @date 2024/5/9
 */
public interface IHrDimissionCertificateAttachService extends IService<HrDimissionCertificateAttach> {
    /**
     * 查询离职证明-附件
     *
     * @param dimissionCertificateAttachSid 离职证明-附件ID
     * @return 离职证明-附件
     */
    public HrDimissionCertificateAttach selectHrDimissionCertificateAttachById(Long dimissionCertificateAttachSid);

    /**
     * 查询离职证明-附件列表
     *
     * @param hrDimissionCertificateAttach 离职证明-附件
     * @return 离职证明-附件集合
     */
    public List<HrDimissionCertificateAttach> selectHrDimissionCertificateAttachList(HrDimissionCertificateAttach hrDimissionCertificateAttach);

    /**
     * 新增离职证明-附件
     *
     * @param hrDimissionCertificateAttach 离职证明-附件
     * @return 结果
     */
    public int insertHrDimissionCertificateAttach(HrDimissionCertificateAttach hrDimissionCertificateAttach);

    /**
     * 修改离职证明-附件
     *
     * @param hrDimissionCertificateAttach 离职证明-附件
     * @return 结果
     */
    public int updateHrDimissionCertificateAttach(HrDimissionCertificateAttach hrDimissionCertificateAttach);

    /**
     * 变更离职证明-附件
     *
     * @param hrDimissionCertificateAttach 离职证明-附件
     * @return 结果
     */
    public int changeHrDimissionCertificateAttach(HrDimissionCertificateAttach hrDimissionCertificateAttach);

    /**
     * 批量删除离职证明-附件
     *
     * @param dimissionCertificateAttachSids 需要删除的离职证明-附件ID
     * @return 结果
     */
    public int deleteHrDimissionCertificateAttachByIds(List<Long> dimissionCertificateAttachSids);

    /**
     * 离职证明查询页面上传附件前的校验
     * @param hrDimissionCertificateAttach
     * @return
     */
    AjaxResult check(HrDimissionCertificateAttach hrDimissionCertificateAttach);

}

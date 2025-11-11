package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.HrOtherPersonnelCertificateAttach;

import java.util.List;

/**
 * 其它人事证明-附件Service接口
 *
 * @author xfzz
 * @date 2024/5/9
 */
public interface IHrOtherPersonnelCertificateAttachService extends IService<HrOtherPersonnelCertificateAttach> {
    /**
     * 查询其它人事证明-附件
     *
     * @param otherPersonnelCertificateAttachSid 其它人事证明-附件ID
     * @return 其它人事证明-附件
     */
    public HrOtherPersonnelCertificateAttach selectHrOtherPersonnelCertificateAttachById(Long otherPersonnelCertificateAttachSid);

    /**
     * 查询其它人事证明-附件列表
     *
     * @param hrOtherPersonnelCertificateAttach 其它人事证明-附件
     * @return 其它人事证明-附件集合
     */
    public List<HrOtherPersonnelCertificateAttach> selectHrOtherPersonnelCertificateAttachList(HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificateAttach);

    /**
     * 新增其它人事证明-附件
     *
     * @param hrOtherPersonnelCertificateAttach 其它人事证明-附件
     * @return 结果
     */
    public int insertHrOtherPersonnelCertificateAttach(HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificateAttach);

    /**
     * 修改其它人事证明-附件
     *
     * @param hrOtherPersonnelCertificateAttach 其它人事证明-附件
     * @return 结果
     */
    public int updateHrOtherPersonnelCertificateAttach(HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificateAttach);

    /**
     * 变更其它人事证明-附件
     *
     * @param hrOtherPersonnelCertificateAttach 其它人事证明-附件
     * @return 结果
     */
    public int changeHrOtherPersonnelCertificateAttach(HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificateAttach);

    /**
     * 批量删除其它人事证明-附件
     *
     * @param otherPersonnelCertificateAttachSids 需要删除的其它人事证明-附件ID
     * @return 结果
     */
    public int deleteHrOtherPersonnelCertificateAttachByIds(List<Long> otherPersonnelCertificateAttachSids);

    /**
     * 其它人事证明查询页面上传附件前的校验
     * @param hrOtherPersonnelCertificateAttach
     * @return
     */
    AjaxResult check(HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificateAttach);

}

package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.HrIncomeCertificateAttach;

import java.util.List;

/**
 * 收入证明-附件Service接口
 *
 * @author xfzz
 * @date 2024/5/9
 */
public interface IHrIncomeCertificateAttachService extends IService<HrIncomeCertificateAttach> {
    /**
     * 查询收入证明-附件
     *
     * @param IncomeCertificateAttachSid 收入证明-附件ID
     * @return 收入证明-附件
     */
    public HrIncomeCertificateAttach selectHrIncomeCertificateAttachById(Long IncomeCertificateAttachSid);

    /**
     * 查询收入证明-附件列表
     *
     * @param hrIncomeCertificateAttach 收入证明-附件
     * @return 收入证明-附件集合
     */
    public List<HrIncomeCertificateAttach> selectHrIncomeCertificateAttachList(HrIncomeCertificateAttach hrIncomeCertificateAttach);

    /**
     * 新增收入证明-附件
     *
     * @param hrIncomeCertificateAttach 收入证明-附件
     * @return 结果
     */
    public int insertHrIncomeCertificateAttach(HrIncomeCertificateAttach hrIncomeCertificateAttach);

    /**
     * 修改收入证明-附件
     *
     * @param hrIncomeCertificateAttach 收入证明-附件
     * @return 结果
     */
    public int updateHrIncomeCertificateAttach(HrIncomeCertificateAttach hrIncomeCertificateAttach);

    /**
     * 变更收入证明-附件
     *
     * @param hrIncomeCertificateAttach 收入证明-附件
     * @return 结果
     */
    public int changeHrIncomeCertificateAttach(HrIncomeCertificateAttach hrIncomeCertificateAttach);

    /**
     * 批量删除收入证明-附件
     *
     * @param IncomeCertificateAttachSids 需要删除的收入证明-附件ID
     * @return 结果
     */
    public int deleteHrIncomeCertificateAttachByIds(List<Long> IncomeCertificateAttachSids);

    /**
     * 收入证明查询页面上传附件前的校验
     * @param hrIncomeCertificateAttach
     * @return
     */
    AjaxResult check(HrIncomeCertificateAttach hrIncomeCertificateAttach);

}

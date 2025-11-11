package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.HrOtherPersonnelCertificate;

import java.util.List;

/**
 * 其它人事证明Service接口
 *
 * @author xfzz
 * @date 2024/5/8
 */
public interface IHrOtherPersonnelCertificateService extends IService<HrOtherPersonnelCertificate> {
    /**
     * 查询其它人事证明
     *
     * @param otherPersonnelCertificateSid 其它人事证明ID
     * @return 其它人事证明
     */
    public HrOtherPersonnelCertificate selectHrOtherPersonnelCertificateById(Long otherPersonnelCertificateSid);

    /**
     * 查询其它人事证明列表
     *
     * @param hrOtherPersonnelCertificate 其它人事证明
     * @return 其它人事证明集合
     */
    public List<HrOtherPersonnelCertificate> selectHrOtherPersonnelCertificateList(HrOtherPersonnelCertificate hrOtherPersonnelCertificate);

    /**
     * 新增其它人事证明
     *
     * @param hrOtherPersonnelCertificate 其它人事证明
     * @return 结果
     */
    public int insertHrOtherPersonnelCertificate(HrOtherPersonnelCertificate hrOtherPersonnelCertificate);

    /**
     * 变更其它人事证明
     *
     * @param hrOtherPersonnelCertificate 其它人事证明
     * @return 结果
     */
    public int changeHrOtherPersonnelCertificate(HrOtherPersonnelCertificate hrOtherPersonnelCertificate);

    /**
     * 批量删除其它人事证明
     *
     * @param otherPersonnelCertificateSids 需要删除的其它人事证明ID
     * @return 结果
     */
    public int deleteHrOtherPersonnelCertificateByIds(List<Long> otherPersonnelCertificateSids);

    /**
     * 更改确认状态
     *
     * @param hrOtherPersonnelCertificate
     * @return
     */
    int check(HrOtherPersonnelCertificate hrOtherPersonnelCertificate);

    /**
     * 纸质合同签收
     */
    int signHrOtherPersonnelCertificateById(HrOtherPersonnelCertificate hrOtherPersonnelCertificate);
}

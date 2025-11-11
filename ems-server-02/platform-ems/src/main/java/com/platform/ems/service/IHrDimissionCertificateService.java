package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.HrDimissionCertificate;

import java.util.List;

/**
 * 离职证明Service接口
 *
 * @author xfzz
 * @date 2024/5/8
 */
public interface IHrDimissionCertificateService extends IService<HrDimissionCertificate> {
    /**
     * 查询离职证明
     *
     * @param dimissionCertificateSid 离职证明ID
     * @return 离职证明
     */
    public HrDimissionCertificate selectHrDimissionCertificateById(Long dimissionCertificateSid);

    /**
     * 查询离职证明列表
     *
     * @param hrDimissionCertificate 离职证明
     * @return 离职证明集合
     */
    public List<HrDimissionCertificate> selectHrDimissionCertificateList(HrDimissionCertificate hrDimissionCertificate);

    /**
     * 新增离职证明
     *
     * @param hrDimissionCertificate 离职证明
     * @return 结果
     */
    public int insertHrDimissionCertificate(HrDimissionCertificate hrDimissionCertificate);

    /**
     * 变更离职证明
     *
     * @param hrDimissionCertificate 离职证明
     * @return 结果
     */
    public int changeHrDimissionCertificate(HrDimissionCertificate hrDimissionCertificate);

    /**
     * 批量删除离职证明
     *
     * @param dimissionCertificateSids 需要删除的离职证明ID
     * @return 结果
     */
    public int deleteHrDimissionCertificateByIds(List<Long> dimissionCertificateSids);

    /**
     * 更改确认状态
     *
     * @param hrDimissionCertificate
     * @return
     */
    int check(HrDimissionCertificate hrDimissionCertificate);

    /**
     * 纸质合同签收
     */
    int signHrDimissionCertificateById(HrDimissionCertificate hrDimissionCertificate);
}

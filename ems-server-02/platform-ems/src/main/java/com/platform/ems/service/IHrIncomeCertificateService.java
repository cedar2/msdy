package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.HrIncomeCertificate;

import java.util.List;

/**
 * 收入证明Service接口
 *
 * @author xfzz
 * @date 2024/5/8
 */
public interface IHrIncomeCertificateService extends IService<HrIncomeCertificate> {
    /**
     * 查询收入证明
     *
     * @param incomeCertificateSid 收入证明ID
     * @return 收入证明
     */
    public HrIncomeCertificate selectHrIncomeCertificateById(Long incomeCertificateSid);

    /**
     * 查询收入证明列表
     *
     * @param hrIncomeCertificate 收入证明
     * @return 收入证明集合
     */
    public List<HrIncomeCertificate> selectHrIncomeCertificateList(HrIncomeCertificate hrIncomeCertificate);

    /**
     * 新增收入证明
     *
     * @param hrIncomeCertificate 收入证明
     * @return 结果
     */
    public int insertHrIncomeCertificate(HrIncomeCertificate hrIncomeCertificate);

    /**
     * 变更收入证明
     *
     * @param hrIncomeCertificate 收入证明
     * @return 结果
     */
    public int changeHrIncomeCertificate(HrIncomeCertificate hrIncomeCertificate);

    /**
     * 批量删除收入证明
     *
     * @param incomeCertificateSids 需要删除的收入证明ID
     * @return 结果
     */
    public int deleteHrIncomeCertificateByIds(List<Long> incomeCertificateSids);

    /**
     * 更改确认状态
     *
     * @param hrIncomeCertificate
     * @return
     */
    int check(HrIncomeCertificate hrIncomeCertificate);

    /**
     * 纸质合同签收
     */
    int signHrIncomeCertificateById(HrIncomeCertificate hrIncomeCertificate);
}

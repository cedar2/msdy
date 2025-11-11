package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.HrLaborContract;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 劳动合同Service接口
 *
 * @author xfzz
 * @date 2024/5/8
 */
public interface IHrLaborContractService  extends IService<HrLaborContract> {
    /**
     * 查询劳动合同
     *
     * @param laborContractSid 劳动合同ID
     * @return 劳动合同
     */
    public HrLaborContract selectHrLaborContractById(Long laborContractSid);

    /**
     * 查询劳动合同列表
     *
     * @param hrLaborContract 劳动合同
     * @return 劳动合同集合
     */
    public List<HrLaborContract> selectHrLaborContractList(HrLaborContract hrLaborContract);

    /**
     * 新增劳动合同
     *
     * @param hrLaborContract 劳动合同
     * @return 结果
     */
    public int insertHrLaborContract(HrLaborContract hrLaborContract);

    /**
     * 变更劳动合同
     *
     * @param hrLaborContract 劳动合同
     * @return 结果
     */
    public int changeHrLaborContract(HrLaborContract hrLaborContract);

    /**
     * 更改确认状态
     *
     * @param hrLaborContract
     * @return
     */
    int check(HrLaborContract hrLaborContract);

    /**
     * 纸质合同签收
     */
    int signHrLaborContractById(HrLaborContract hrLaborContract);

    /**
     * 设置履约状态
     */
    int setLvyueStatusById(HrLaborContract hrLaborContract);

    /**
     * 终止履约状态
     */
    int endLvyueStatusById(HrLaborContract hrLaborContract);


    /**
     * 导入
     */
    EmsResultEntity importRecord(MultipartFile file);

}

package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasLaboratoryAddr;

import java.util.List;

/**
 * 实验室-联系方式信息Service接口
 *
 * @author c
 * @date 2022-03-31
 */
public interface IBasLaboratoryAddrService extends IService<BasLaboratoryAddr> {
    /**
     * 查询实验室-联系方式信息
     *
     * @param laboratoryContactSid 实验室-联系方式信息ID
     * @return 实验室-联系方式信息
     */
    public BasLaboratoryAddr selectBasLaboratoryAddrById(Long laboratoryContactSid);

    /**
     * 查询实验室-联系方式信息列表
     *
     * @param basLaboratoryAddr 实验室-联系方式信息
     * @return 实验室-联系方式信息集合
     */
    public List<BasLaboratoryAddr> selectBasLaboratoryAddrList(BasLaboratoryAddr basLaboratoryAddr);

    /**
     * 新增实验室-联系方式信息
     *
     * @param basLaboratoryAddr 实验室-联系方式信息
     * @return 结果
     */
    public int insertBasLaboratoryAddr(BasLaboratoryAddr basLaboratoryAddr);

    /**
     * 修改实验室-联系方式信息
     *
     * @param basLaboratoryAddr 实验室-联系方式信息
     * @return 结果
     */
    public int updateBasLaboratoryAddr(BasLaboratoryAddr basLaboratoryAddr);

    /**
     * 变更实验室-联系方式信息
     *
     * @param basLaboratoryAddr 实验室-联系方式信息
     * @return 结果
     */
    public int changeBasLaboratoryAddr(BasLaboratoryAddr basLaboratoryAddr);

    /**
     * 批量删除实验室-联系方式信息
     *
     * @param laboratoryContactSids 需要删除的实验室-联系方式信息ID
     * @return 结果
     */
    public int deleteBasLaboratoryAddrByIds(List<Long> laboratoryContactSids);

}

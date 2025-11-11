package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurPurchaseContractPayMethod;

/**
 * 采购合同信息-支付方式Service接口
 *
 * @author chenkw
 * @date 2022-05-17
 */
public interface IPurPurchaseContractPayMethodService extends IService<PurPurchaseContractPayMethod> {
    /**
     * 查询采购合同信息-支付方式
     *
     * @param contractPayMethodSid 采购合同信息-支付方式ID
     * @return 采购合同信息-支付方式
     */
    public PurPurchaseContractPayMethod selectPurPurchaseContractPayMethodById(Long contractPayMethodSid);

    /**
     * 查询采购合同信息-支付方式列表
     *
     * @param purPurchaseContractPayMethod 采购合同信息-支付方式
     * @return 采购合同信息-支付方式集合
     */
    public List<PurPurchaseContractPayMethod> selectPurPurchaseContractPayMethodList(PurPurchaseContractPayMethod purPurchaseContractPayMethod);

    /**
     * 新增采购合同信息-支付方式
     *
     * @param purPurchaseContractPayMethod 采购合同信息-支付方式
     * @return 结果
     */
    public int insertPurPurchaseContractPayMethod(PurPurchaseContractPayMethod purPurchaseContractPayMethod);

    /**
     * 修改采购合同信息-支付方式
     *
     * @param purPurchaseContractPayMethod 采购合同信息-支付方式
     * @return 结果
     */
    public int updatePurPurchaseContractPayMethod(PurPurchaseContractPayMethod purPurchaseContractPayMethod);

    /**
     * 变更采购合同信息-支付方式
     *
     * @param purPurchaseContractPayMethod 采购合同信息-支付方式
     * @return 结果
     */
    public int changePurPurchaseContractPayMethod(PurPurchaseContractPayMethod purPurchaseContractPayMethod);

    /**
     * 批量删除采购合同信息-支付方式
     *
     * @param contractPayMethodSids 需要删除的采购合同信息-支付方式ID
     * @return 结果
     */
    public int deletePurPurchaseContractPayMethodByIds(List<Long> contractPayMethodSids);

    /**
     * 通过合同查询采购合同信息-支付方式列表
     *
     * @param contractSid 采购合同信息
     * @return 采购合同信息-支付方式集合
     */
    public List<PurPurchaseContractPayMethod> selectPurPurchaseContractPayMethodListByContract(Long contractSid);

    /**
     * 批量新增采购合同信息-支付方式
     *
     * @param list 采购合同信息-支付方式
     * @return 结果
     */
    public int insertPurPurchaseContractPayMethodList(Long contractSid, List<PurPurchaseContractPayMethod> list);

    /**
     * 通过合同批量删除销售合同信息-支付方式
     *
     * @param contractSidList 需要删除的销售合同信息ID
     * @return 结果
     */
    public int deletePurPurchaseContractPayMethodByContract(List<Long> contractSidList);

    /**
     * 通过合同批量修改采购合同信息-支付方式
     *
     * @param list 采购合同信息-支付方式
     * @return 结果
     */
    public void updatePurPurchaseContractPayMethodList(Long contractSid, List<PurPurchaseContractPayMethod> list);

    /**
     * 合同提交前校验-支付方式占比只能等于1,没有则跳过
     *
     * @param contractSid 采购合同信息SID
     * @return 结果
     */
    public String submitVerifyById(Long contractSid);

    /**
     * 合同提交前校验-支付方式占比只能等于1,没有则跳过
     *
     * @param list 采购合同信息-支付方式
     * @return 结果
     */
    public String submitVerify(List<PurPurchaseContractPayMethod> list, String category);
    public String submitVerify2(List<PurPurchaseContractPayMethod> list, String category);

}

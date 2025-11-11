package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SalSaleContractPayMethod;

/**
 * 销售合同信息-支付方式Service接口
 *
 * @author chenkw
 * @date 2022-05-17
 */
public interface ISalSaleContractPayMethodService extends IService<SalSaleContractPayMethod> {
    /**
     * 查询销售合同信息-支付方式
     *
     * @param contractPayMethodSid 销售合同信息-支付方式ID
     * @return 销售合同信息-支付方式
     */
    public SalSaleContractPayMethod selectSalSaleContractPayMethodById(Long contractPayMethodSid);

    /**
     * 查询销售合同信息-支付方式列表
     *
     * @param salSaleContractPayMethod 销售合同信息-支付方式
     * @return 销售合同信息-支付方式集合
     */
    public List<SalSaleContractPayMethod> selectSalSaleContractPayMethodList(SalSaleContractPayMethod salSaleContractPayMethod);

    /**
     * 新增销售合同信息-支付方式
     *
     * @param salSaleContractPayMethod 销售合同信息-支付方式
     * @return 结果
     */
    public int insertSalSaleContractPayMethod(SalSaleContractPayMethod salSaleContractPayMethod);

    /**
     * 修改销售合同信息-支付方式
     *
     * @param salSaleContractPayMethod 销售合同信息-支付方式
     * @return 结果
     */
    public int updateSalSaleContractPayMethod(SalSaleContractPayMethod salSaleContractPayMethod);

    /**
     * 变更销售合同信息-支付方式
     *
     * @param salSaleContractPayMethod 销售合同信息-支付方式
     * @return 结果
     */
    public int changeSalSaleContractPayMethod(SalSaleContractPayMethod salSaleContractPayMethod);

    /**
     * 批量删除销售合同信息-支付方式
     *
     * @param contractPayMethodSids 需要删除的销售合同信息-支付方式ID
     * @return 结果
     */
    public int deleteSalSaleContractPayMethodByIds(List<Long> contractPayMethodSids);

    /**
     * 通过合同查询销售合同信息-支付方式列表
     *
     * @param contractSid 销售合同信息
     * @return 销售合同信息-支付方式集合
     */
    public List<SalSaleContractPayMethod> selectSalSaleContractPayMethodListByContract(Long contractSid);

    /**
     * 批量新增销售合同信息-支付方式
     *
     * @param list 销售合同信息-支付方式
     * @return 结果
     */
    public int insertSalSaleContractPayMethodList(Long contractSid, List<SalSaleContractPayMethod> list);

    /**
     * 通过合同批量删除销售合同信息-支付方式
     *
     * @param contractSidList 需要删除的销售合同信息ID
     * @return 结果
     */
    public int deleteSalSaleContractPayMethodByContract(List<Long> contractSidList);

    /**
     * 通过合同批量修改销售合同信息-支付方式
     *
     * @param list 销售合同信息-支付方式
     * @return 结果
     */
    public void updateSalSaleContractPayMethodList(Long contractSid, List<SalSaleContractPayMethod> list);

    /**
     * 合同提交前校验-支付方式占比只能等于1,没有则跳过
     *
     * @param contractSid 销售合同信息SID
     * @return 结果
     */
    public String submitVerifyById(Long contractSid);

    /**
     * 合同提交前校验-支付方式占比只能等于1,没有则跳过
     *
     * @param list) 销售合同信息-支付方式
     * @return 结果
     */
    public String submitVerify(List<SalSaleContractPayMethod> list, String category);
    public String submitVerify2(List<SalSaleContractPayMethod> list, String category);

}

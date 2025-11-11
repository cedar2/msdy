package com.platform.ems.mapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.base.ContractTemplateAttach;
import com.platform.ems.domain.dto.response.form.PurPurchaseContractFormResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurPurchaseContract;

/**
 * 采购合同信息Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface PurPurchaseContractMapper  extends BaseMapper<PurPurchaseContract> {


    PurPurchaseContract selectPurPurchaseContractById(Long purchaseContractSid);

    List<PurPurchaseContract> selectPurPurchaseContractList(PurPurchaseContract purPurchaseContract);

    /**
     * 添加多个
     * @param list List PurPurchaseContract
     * @return int
     */
    int inserts(@Param("list") List<PurPurchaseContract> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurPurchaseContract
    * @return int
    */
    int updateAllById(PurPurchaseContract entity);

    /**
     * 更新多个
     * @param list List PurPurchaseContract
     * @return int
     */
    int updatesAllById(@Param("list") List<PurPurchaseContract> list);


    int countByDomain(PurPurchaseContract params);

    PurPurchaseContract getName(Long purchaseContractSid);

    /**
     * 合同下拉框接口
     */
    List<PurPurchaseContract> getPurPurchaseContractList();

    /**
     * 合同下拉框接口(带参数)
     */
    List<PurPurchaseContract> getPurchaseContractList(PurPurchaseContract purPurchaseContract);

    /**
     * 原合同号下拉框接口
     */
    List<PurPurchaseContract> getOriginalContractList(PurPurchaseContract purPurchaseContract);

    /**
     * 查询即将到期的合同
     * offset : 设置到期前的天数才算即将到期
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PurPurchaseContract> getToexpireBusiness(@Param("offset") int offset);

    /**
     * 查询已逾期的合同
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PurPurchaseContract> getOverdueBusiness();

    /**
     * 查询出所有处理状态为“已确认”且签收状态(纸质协议)为“未签收”的采购框架协议，
     * @param purPurchaseContract
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PurPurchaseContract> selectPurchaseContractKuangjia(PurPurchaseContract purPurchaseContract);

    /**
     * 查询采购合同统计报表
     * @param purPurchaseContract
     * @return
     */
    List<PurPurchaseContractFormResponse> getCountForm(PurPurchaseContract purPurchaseContract);

    /**
     * 查询采购合同统计报表明细
     * @param purPurchaseContract
     * @return
     */
    List<PurPurchaseContractFormResponse> getCountFormItem(PurPurchaseContract purPurchaseContract);

    /**
     * 查询合同模板列表
     *
     * @param request 请求
     * @return 合同模板列表
     */
    public List<ContractTemplateAttach> selectContractTemplateList(ContractTemplateAttach request);
}

package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurPurchaseContract;
import com.platform.ems.domain.PurPurchaseOrderItem;
import com.platform.ems.domain.base.ContractTemplateAttach;
import com.platform.ems.domain.dto.response.form.PurPurchaseContractFormResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 采购合同信息Service接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IPurPurchaseContractService extends IService<PurPurchaseContract>{
    /**
     * 查询采购合同信息
     *
     * @param purchaseContractSid 采购合同信息ID
     * @return 采购合同信息
     */
    public PurPurchaseContract selectPurPurchaseContractById(Long purchaseContractSid);

    /**
     * 复制采购合同信息
     *
     * @param purchaseContractSid 采购合同信息ID
     * @return 采购合同信息
     */
    public PurPurchaseContract copyPurPurchaseContractById(Long purchaseContractSid);

    /**
     * 查询采购合同信息列表
     *
     * @param purPurchaseContract 采购合同信息
     * @return 采购合同信息集合
     */
    public List<PurPurchaseContract> selectPurPurchaseContractList(PurPurchaseContract purPurchaseContract);

    /**
     * 校验销售合同号是否已存在
     *
     * @param purPurchaseContract 采购合同信息
     * @return 结果
     */
    public void checkCode(PurPurchaseContract purPurchaseContract);

    /**
     * 新增采购合同信息
     *
     * @param purPurchaseContract 采购合同信息
     * @return 结果
     */
    public Long insertPurPurchaseContract(PurPurchaseContract purPurchaseContract);

    /**
     * 修改采购合同信息
     *
     * @param purPurchaseContract 采购合同信息
     * @return 结果
     */
    public int updatePurPurchaseContract(PurPurchaseContract purPurchaseContract);

    /**
     * 变更采购合同信息
     *
     * @param purPurchaseContract 采购合同信息
     * @return 结果
     */
    public int changePurPurchaseContract(PurPurchaseContract purPurchaseContract);

    /**
     * 批量删除采购合同信息
     *
     * @param purchaseContractSids 需要删除的采购合同信息ID
     * @return 结果
     */
    public int deletePurPurchaseContractByIds(List<Long> purchaseContractSids);

    /**
     * 更改确认状态
     * @param purPurchaseContract
     * @return
     */
    int check(PurPurchaseContract purPurchaseContract);

    List<PurPurchaseContract> getPurPurchaseContractList();

    List<PurPurchaseContract> getPurchaseContractList(PurPurchaseContract purPurchaseContract);

    /**
     * 生成财务流水
     * @param purPurchaseContract
     */
    public void advancePayment(PurPurchaseContract purPurchaseContract);

    /**
     * 导入
     * @param file
     * @return
     */
    Object importData(MultipartFile file);

    /**
     * 原合同号下拉框接口
     */
    List<PurPurchaseContract> getOriginalContractList(PurPurchaseContract purPurchaseContract);

    /**
     * 作废采购合同信息
     */
    int cancellationPurPurchaseContractById(PurPurchaseContract purPurchaseContract);

    /**
     * 结案采购合同信息
     */
    int closingPurPurchaseContractById(Long purchaseContractSid);

    /**
     * 纸质合同签收
     */
    int signPurPurchaseContractById(PurPurchaseContract purPurchaseContract);

    /**
     * 根据模板和合同数据自动生成电子合同
     *
     * @return 采购合同信息
     */
    MultipartFile autoGenContract(String filePath, String pre, Long purchaseContractSid);

    /**
     * 设置即将到期提醒天数
     * @param purPurchaseContract
     * @return
     */
    public int setToexpireDays(PurPurchaseContract purPurchaseContract);

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
     * 按“商品编码+合同交期”汇总“订单明细”页签的数据
     *
     * @param purPurchaseContract 合同信息
     * @return 合同信息集合
     */
    public List<PurPurchaseOrderItem> groupPurchaseOrderItemList(PurPurchaseContract purPurchaseContract);

    /**
     * 查询合同模板列表
     *
     * @param request 请求
     * @return 合同模板列表
     */
    public List<ContractTemplateAttach> selectContractTemplateList(ContractTemplateAttach request);

}

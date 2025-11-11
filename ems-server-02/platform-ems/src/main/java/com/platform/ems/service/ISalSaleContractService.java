package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SalSaleContract;
import com.platform.ems.domain.SalSalesOrder;
import com.platform.ems.domain.SalSalesOrderItem;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.form.SalSaleContractFormResponse;
import com.platform.ems.eSignApp.domain.SignFlowCallback;
import org.springframework.web.multipart.MultipartFile;

/**
 * 销售合同信息Service接口
 *
 * @author linhongwei
 * @date 2021-05-18
 */
public interface ISalSaleContractService extends IService<SalSaleContract>{
    /**
     * 查询销售合同信息
     *
     * @param saleContractSid 销售合同信息ID
     * @return 销售合同信息
     */
    public SalSaleContract selectSalSaleContractById(Long saleContractSid);

    /**
     * 复制销售合同信息
     *
     * @param saleContractSid 销售合同信息ID
     * @return 销售合同信息
     */
    public SalSaleContract copySalSaleContractById(Long saleContractSid);

    /**
     * 按“商品编码+合同交期”汇总“订单明细”页签的数据
     *
     * @param salSaleContract 销售合同信息
     * @return 销售合同信息集合
     */
    public List<SalSalesOrderItem> groupSaleOrderItemList(SalSaleContract salSaleContract);

    /**
     * 合同的订单明细页签调用接口
     */
    public SalSaleContract saleOrderItemList(SalSaleContract salSaleContract);

    /**
     * 订单号+商品编码+合同交期 汇总订单量和销售金额含税
     *
     * @param salSaleContract 销售合同信息
     * @return 销售合同信息集合
     */
    public List<SalSalesOrder> selectGroupSaleOrderItemList(SalSaleContract salSaleContract);

    /**
     * 添加后，将选中销售订单的合同号改成此合同号，并刷新“订单明细”页签。
     *
     * @param salSaleContract 销售合同信息
     * @return 销售合同信息集合
     */
    public EmsResultEntity changeSaleOrderContract(SalSaleContract salSaleContract, String isContinue);

    /**
     * 查询销售合同信息列表
     *
     * @param salSaleContract 销售合同信息
     * @return 销售合同信息集合
     */
    public List<SalSaleContract> selectSalSaleContractList(SalSaleContract salSaleContract);

    /**
     * 校验销售合同号是否已存在
     *
     * @param salSaleContract 销售合同信息
     * @return 结果
     */
    public void checkCode(SalSaleContract salSaleContract);

    /**
     * 新增销售合同信息
     *
     * @param salSaleContract 销售合同信息
     * @return 结果
     */
    public Long insertSalSaleContract(SalSaleContract salSaleContract);

    /**
     * 修改销售合同信息
     *
     * @param salSaleContract 销售合同信息
     * @return 结果
     */
    public int updateSalSaleContract(SalSaleContract salSaleContract);

    /**
     * 变更销售合同信息
     *
     * @param salSaleContract 销售合同信息
     * @return 结果
     */
    public int changeSalSaleContract(SalSaleContract salSaleContract);

    /**
     * 批量删除销售合同信息
     *
     * @param saleContractSids 需要删除的销售合同信息ID
     * @return 结果
     */
    public int deleteSalSaleContractByIds(List<Long> saleContractSids);

    /**
     * 更改确认状态
     * @param salSaleContract
     * @return
     */
    int check(SalSaleContract salSaleContract);

    List<SalSaleContract> getSalSaleContractList();

    List<SalSaleContract> getSaleContractList(SalSaleContract salSaleContractr);

    /**
     * 导入
     * @param file
     * @return
     */
    Object importData(MultipartFile file);

    /**
     * 原合同号下拉框接口
     */
    List<SalSaleContract> getOriginalContractList(SalSaleContract salSaleContractr);

    /**
     * 作废销售合同信息
     */
    int cancellationSalSaleContractById(SalSaleContract salSaleContractr);

    /**
     * 结案销售合同信息
     */
    int closingSalSaleContractById(Long saleContractSid);

    /**
     * 纸质合同签收
     */
    int signSalSaleContractById(SalSaleContract salSaleContractr);

    /**
     * 生成流水
     */
    void advanceReceipt(SalSaleContract salSaleContract);

    /**
     * 根据模板和合同数据自动生成电子合同
     *
     * @param saleContractSid 销售合同信息ID
     * @return 销售合同信息
     */
    MultipartFile autoGenContract(String filePath, Long saleContractSid);

    /**
     * 设置即将到期提醒天数
     * @param salSaleContract
     * @return
     */
    public int setToexpireDays(SalSaleContract salSaleContract);

    /**
     * 查询销售合同统计报表
     * @param salSaleContract
     * @return
     */
    List<SalSaleContractFormResponse> getCountForm(SalSaleContract salSaleContract);

    /**
     * 查询销售合同统计报表明细
     * @param salSaleContract
     * @return
     */
    List<SalSaleContractFormResponse> getCountFormItem(SalSaleContract salSaleContract);

    /**
     * 合同审批通过后发起e签宝签署
     */
    void approvalContractToEsign(SalSaleContract salSaleContract);

    /**
     * e签宝签署流程完成后回调方法
     */
    int addContractAttachEsign(SignFlowCallback signFlowCallback);
}

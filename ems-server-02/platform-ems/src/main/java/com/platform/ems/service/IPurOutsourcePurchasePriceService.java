package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.ManManufactureOrderProcess;
import com.platform.ems.domain.PurOutsourcePurchasePrice;
import com.platform.ems.domain.PurOutsourcePurchasePriceItem;
import com.platform.ems.domain.dto.response.PurOutsourcePurchasePriceResponse;
import com.platform.ems.domain.dto.response.PurOutsourceReportResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 加工采购价主Service接口
 * 
 * @author linhongwei
 * @date 2021-05-12
 */
public interface IPurOutsourcePurchasePriceService extends IService<PurOutsourcePurchasePrice>{
    /**
     * 查询加工采购价主
     * 
     * @param outsourcePurchasePriceSid 加工采购价主ID
     * @return 加工采购价主
     */
    public PurOutsourcePurchasePrice selectPurOutsourcePurchasePriceById(Long outsourcePurchasePriceSid);
    /**
     * 查询加工采购价报表
     *
     * @param purOutsourcePurchasePrice
     * @return 加工采购价主
     */
    public List<PurOutsourceReportResponse> report(PurOutsourceReportResponse purOutsourcePurchasePrice);

    public String judgeAdd(PurOutsourcePurchasePrice purOutsourcePurchasePrice);

    List<ManManufactureOrderProcess> getPrice(PurOutsourcePurchasePriceResponse response);
    /**
     * 查询加工采购价主列表
     * 
     * @param purOutsourcePurchasePrice 加工采购价主
     * @return 加工采购价主集合
     */
    public List<PurOutsourcePurchasePrice> selectPurOutsourcePurchasePriceList(PurOutsourcePurchasePrice purOutsourcePurchasePrice);

    /**
     * 新增加工采购价主
     * 
     * @param purOutsourcePurchasePrice 加工采购价主
     * @return 结果
     */
    public int insertPurOutsourcePurchasePrice(PurOutsourcePurchasePrice purOutsourcePurchasePrice);

    /**
     * 修改加工采购价主
     * 
     * @param purOutsourcePurchasePrice 加工采购价主
     * @return 结果
     */
    public int updatePurOutsourcePurchasePrice(PurOutsourcePurchasePrice purOutsourcePurchasePrice);
    /**
     * 修改加工采购价主-新
     *
     * @param purOutsourcePurchasePrice 加工采购价主
     * @return 结果
     */
    public int updatePurOutsourcePurchasePriceNew(PurOutsourcePurchasePrice purOutsourcePurchasePrice);
    /**
     * 变更加工采购价主
     *
     * @param purOutsourcePurchasePrice 加工采购价主
     * @return 结果
     */
    public int changePurOutsourcePurchasePrice(PurOutsourcePurchasePrice purOutsourcePurchasePrice);

    /**
     * 批量删除加工采购价主
     * 
     * @param outsourcePurchasePriceSids 需要删除的加工采购价主ID
     * @return 结果
     */
    public int deletePurOutsourcePurchasePriceByIds(List<Long> outsourcePurchasePriceSids);

    /**
     * 批量删除加工采购价明细
     *
     * @param outsourcePurchasePriceSids 批量删除加工采购价明细
     * @return 结果
     */
    public int deleteItems(List<Long> outsourcePurchasePriceSids);

    /**
    * 启用/停用
    * @param purOutsourcePurchasePrice
    * @return
    */
    int changeStatus(PurOutsourcePurchasePrice purOutsourcePurchasePrice);

    /**
     * 更改确认状态
     * @param purOutsourcePurchasePrice
     * @return
     */
    int check(PurOutsourcePurchasePrice purOutsourcePurchasePrice);
    /**
     * 提交时校验
     */
    public int processCheck(List<Long> ids);
    /**
     * 获取加工采购价
     */
    PurOutsourcePurchasePriceItem getPurchasePrice(PurOutsourcePurchasePrice purOutsourcePurchasePrice);
    /**
     * 加工采购价 导入
     */
    public AjaxResult importDataOutPur(MultipartFile file);

    /**
     * 审批流修改状态
     *
     */
    public int flowHandle(PurOutsourcePurchasePriceItem item,String comment);

    public void setApprovalLog(PurOutsourcePurchasePriceItem item, String comment);
}

package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.SalSalePrice;
import com.platform.ems.domain.SalSalePriceItem;
import com.platform.ems.domain.dto.request.SalePriceActionRequest;
import com.platform.ems.domain.dto.response.SaleReportResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 销售价信息Service接口
 *
 * @author yangqize
 * @date 2021-03-07
 */
public interface ISalSalePriceService extends IService<SalSalePrice>{
    /**
     * 查询销售价信息
     *
     * @param salePriceSid 销售价信息ID
     * @return 销售价信息
     */
    public SalSalePrice selectSalSalePriceById(Long salePriceSid);

    /**
     * 查询销售价报表
     *
     * @param saleReportResponse 销售价信息
     * @return 销售价信息
     */
    public List<SaleReportResponse> saleReport(SaleReportResponse saleReportResponse);
    /**
     * 查询销售价信息列表
     *
     * @param salSalePrice 销售价信息
     * @return 销售价信息集合
     */
    public List<SalSalePrice> selectSalSalePriceList(SalSalePrice salSalePrice);
    /**
     * 按条件查询销售价信息
     *
     * @param SalSalePrice 销售价信息
     * @return 销售价信息集合
     */
    public List<SalSalePrice> getList(SalSalePrice SalSalePrice);

    /**
     * 新增销售价信息
     *
     * @param SalSalePrice 销售价信息
     * @return 结果
     */
    public AjaxResult insertSalSalePrice(SalSalePrice SalSalePrice);

    /**
     * 新增/编辑直接提交销售价信息
     *
     * @param salSalePrice 销售价信息主
     * @return 结果
     */
    public AjaxResult submit(SalSalePrice salSalePrice);

    /**
     * 修改销售价信息
     *
     * @param SalSalePrice 销售价信息
     * @return 结果
     */
    public AjaxResult updateSalSalePrice(SalSalePrice SalSalePrice);

    /**
     * 修改销售价信息-新
     *
     * @param salSalePrice 销售价信息
     * @return 结果
     */
    public AjaxResult updateSalSalePriceNew(SalSalePrice salSalePrice);

    /**
     * 查询页面更新客户与客供料方式
     */
    int updateCustomer(SalSalePrice salSalePrice);

    /**
     * 查询页面变更有效期
     *
     * @param salSalePriceItem
     * @return 结果
     */
    AjaxResult changeItemTime(SaleReportResponse salSalePriceItem);

    /**
     * 删除销售价信息信息
     *
     * @param ids 销售价信息ID
     * @return 结果
     */
    public AjaxResult deleteSalSalePriceById(List<Long> ids);
    /**
     * 删除销售价明细行
     * @param
     * @return 结果
     */
    public int deleteItem(List<Long> ids);

    /**
     * 修改处理状态（确认）
     *
     * @param salePriceActionRequest
     * @return 结果
     */
    public AjaxResult handleStatusConfirm(SalePriceActionRequest salePriceActionRequest);

    /**
     * 批量 启用/停用
     *
     * @param salePriceActionRequest
     * @return 结果
     */
    public AjaxResult status(SalePriceActionRequest salePriceActionRequest);
    /**
     * 销售价信息变更
     *
     * @param salePriceRequest
     * @return 结果
     */
    public AjaxResult change(SalSalePrice salePriceRequest);
    /**
     * 根据编码查询商品信息返回对应的sku1集合
     *
     * @param
     * @return 结果
     */
    public BasMaterial getMaterialSkus(BasMaterial material);
    /**
     * 判断是否可以新增销售价
     *
     * @param
     * @return 结果
     */
    public AjaxResult judgeAdd(SalSalePrice salSalePrice);

    /**
     * 获取销售价
     */
    SalSalePriceItem getSalePrice(SalSalePrice salSalePrice);
    //第二层校验
    public void judgeTime(SalSalePrice salSalePrice,SalSalePriceItem salSalePriceItem);
    //获取组合拉链销售价
    public SalSalePriceItem zipperPriceZH(SalSalePrice salSalePrice);
    //获取销售价 最新
    public  SalSalePriceItem getNewSalePrice(SalSalePrice salSalePrice);
    //销售价导入
    public AjaxResult importDataPur(MultipartFile file);
    /**
     * 提交时校验
     */
    public int processCheck(List<Long> salSalePriceSids);
    /**
     * 审批流修改状态
     *
     */
    public int flowHandle(SalSalePriceItem item,String comment);

    public void setApprovalLog(SalSalePriceItem item,String comment);

    /**
     * 根据订单中的“编码+客供料方式+销售模式”在销售价档案中获取销售价的处理状态为“审批中/已确认/变更审批中”且有效期（至）>=当前日期的销售价数据
     * 若查找到多笔数据，则选择有效期（至）距离当前日期最近的价格
     */
    public SalSalePriceItem getNearSalePrice(SalSalePrice salSalePrice);

}

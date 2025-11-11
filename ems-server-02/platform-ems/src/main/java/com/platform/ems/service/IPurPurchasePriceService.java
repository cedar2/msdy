package com.platform.ems.service;

import java.math.BigDecimal;
import java.util.List;

import com.platform.ems.domain.ManProcess;
import com.platform.ems.domain.dto.request.CheckUniqueCommonRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.PurPurchasePrice;
import com.platform.ems.domain.PurPurchasePriceItem;
import com.platform.ems.domain.dto.request.PurPurchasePriceActionRequest;
import com.platform.ems.domain.dto.response.PurPurchasePriceReportResponse;

/**
 * 采购价信息主Service接口
 *
 * @author ChenPinzhen
 * @date 2021-02-04
 */
public interface IPurPurchasePriceService extends IService<PurPurchasePrice>{
    /**
     * 查询采购价信息主
     *
     * @param id 采购价信息主ID
     * @return 采购价信息主
     */
    public PurPurchasePrice selectPurPurchasePriceById(Long id);

    /**
     * 查询采购价信息主列表
     *
     * @param purPurchasePrice 采购价信息主
     * @return 采购价信息主集合
     */
    public List<PurPurchasePrice> selectPurPurchasePriceList(PurPurchasePrice purPurchasePrice);

    /**
     * 修改采购价信息主-新
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    public AjaxResult updatePurPurchasePriceNew(PurPurchasePrice purPurchasePrice);

    /**
     * 查询页面变更有效期
     *
     * @param purPurchasePriceItem
     * @return 结果
     */
    AjaxResult changeItemTime(PurPurchasePriceReportResponse purPurchasePriceItem);

    /**
     * 新增采购价信息主
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    public AjaxResult insertPurPurchasePrice(PurPurchasePrice purPurchasePrice);

    /**
     * 新增/编辑直接提交采购价信息
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    public AjaxResult submit(PurPurchasePrice purPurchasePrice);

    /**
     * 修改采购价信息主
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    public AjaxResult updatePurPurchasePrice(PurPurchasePrice purPurchasePrice);

    /**
     * 变更采购价信息主
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    public AjaxResult changePurPurchasePrice(PurPurchasePrice purPurchasePrice);

    /**
     * 批量删除采购价信息主
     *
     * @param ids 需要删除的采购价信息主ID
     * @return 结果
     */
    public AjaxResult deletePurPurchasePriceByIds(List<Long> ids);
    /**
     * 批量删除采购价信息明细
     *
     * @param ids 需要删除的采购价信息主ID
     * @return 结果
     */
    public int deleteItem(List<Long> ids);

    /**
     * 确认采购价信息主信息
     *
     * @param purPurchasePriceActionRequset 采购价信息主ID
     * @return 结果
     */
    public AjaxResult confirm(PurPurchasePriceActionRequest purPurchasePriceActionRequset);

    /**
     * 启用/停用 采购价信息主信息
     *
     * @param purPurchasePriceActionRequset 采购价信息主ID
     * @return 结果
     */
    public AjaxResult status(PurPurchasePriceActionRequest purPurchasePriceActionRequset);
    public AjaxResult judgeAdd(PurPurchasePrice purPurchasePrice);

    /**
     *  采购价信息报表主信息
     *
     * @param response 采购价信息报表主信息
     * @return 结果
     */
    public List<PurPurchasePriceReportResponse> report(PurPurchasePriceReportResponse response);

    /**
     * 获取采购价
     */
    PurPurchasePriceItem getPurchasePrice(PurPurchasePrice purPurchasePrice);
    //审批后价格回写
    public  void orderUpdate(PurPurchasePrice purPurchasePrice,PurPurchasePriceItem purchasePriceItem);
    //第二层校验
    public void judgeTime(PurPurchasePrice purPurchasePrice,PurPurchasePriceItem purPurchasePriceItem);
    //获取组合拉链的采购价
    public PurPurchasePriceItem getZipperPurchase(PurPurchasePrice purPurchasePrice);
    //获取整条拉链采购价
    public PurPurchasePriceItem zipperPriceZT(PurPurchasePrice purPurchasePrice,PurPurchasePriceItem item);
    /**
     * 采购价 导入
     */
    public AjaxResult importDataPur(MultipartFile file);

    /**
     * 导入提示信息后继续导入， 复制来源于导入功能结尾的执行代码
     * @param PurPurchasePriceList
     * @return
     */
    AjaxResult insertImport(List<PurPurchasePrice> PurPurchasePriceList);

    /**
     * 获取采购价最新
     */
    public PurPurchasePriceItem getNewPurchase(PurPurchasePrice purPurchasePrice);
    /**
     * 提交时校验
     */
    public int processCheck(List<Long> purchasePriceSid);
    /**
     * 审批流修改状态
     *
     */
    public int flowHandle(PurPurchasePriceItem item,String comment);

    public void setApprovalLog(PurPurchasePriceItem item,String comment);

    /**
     * 校验同一时刻只允许存在一笔采购价进行中
     * @author
     * @return
     */
    public void checkUnique(CheckUniqueCommonRequest request);

    /**
     *3、新增列：价格(元)
     *若订单中有价格，就显示订单价格；
     *若没有，进行如下操作：
     *1）根据订单中的“编码+供应商+甲供料方式+采购模式”在采购价档案中获取采购价的处理状态为“审批中/已确认/变更审批中”且有效期（至）>=当前日期的采购价数据
     *若1）查找到多笔数据，则选择有效期（至）距离当前日期最近的价格
     */
    public PurPurchasePriceItem getNearPurchase(PurPurchasePrice purPurchasePrice);
}

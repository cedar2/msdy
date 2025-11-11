package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.BasMaterialBarcode;
import com.platform.ems.domain.BasMaterialSaleStation;
import com.platform.ems.domain.BasMaterialSku;

import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.MaterialAddRequest;
import com.platform.ems.domain.dto.request.material.BasMaterialSkuRequest;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.form.BasMaterialSaleStationCategoryForm;

import java.util.List;

/**
 * 物料&商品&服务档案Service接口
 *
 * @author linhongwei
 * @date 2021-03-12
 */
public interface IBasMaterialService extends IService<BasMaterial> {
    /**
     * 查询物料&商品&服务档案
     *
     * @param materialSid 物料&商品&服务档案ID
     * @return 物料&商品&服务档案
     */
    BasMaterial selectBasMaterialById(Long materialSid);

    /**
     * 查询物料&商品&服务档案 详细档案
     */
    BasMaterialPicture selectBasMaterialPicture(Long materialSid);

    /**
     * 查询物料&商品&服务档案列表
     *
     * @param basMaterial 物料&商品&服务档案
     * @return 物料&商品&服务档案集合
     */
    List<BasMaterial> selectBasMaterialList(BasMaterial basMaterial);

    /**
     * 下拉框接口
     */
    List<BasMaterialDropDown> selectMaterialList(BasMaterial basMaterial);

    /**
     * 物料/商品编码重复校验 在输入完编码就调用
     *
     * @param basMaterial 物料&商品&服务档案
     * @return
     */
    String checkCode(BasMaterial basMaterial);

    /**
     * 供应商+供方编码重复校验
     *
     * @param basMaterial 物料&商品&服务档案
     * @return
     */
    AjaxResult checkVendor(BasMaterial basMaterial);

    /**
     * 商品我司样衣号重复校验
     *
     * @param basMaterial 物料&商品&服务档案
     * @return
     */
    void checkSelfCode(BasMaterial basMaterial);

    /**
     * 点击“新增行”按钮时，判断此商品的BOM处理状态是否是“审批中”，如是，则提示错误信息：此商品BOM正在审批中，不允许加色，请先将此商品的BOM驳回。
     * 点击“启用/停用”时，判断此商品的BOM处理状态是否是“审批中”，如是，则提示错误信息：此商品BOM正在审批中，不允许启用/停用颜色，请先将此商品的BOM驳回。
     *
     * @param materialSid 物料&商品&服务档案
     * @return
     */
    void checkBomApproval(Long materialSid);

    /**
     * 得到商品条码标签信息
     *
     * @param barcode 商品条码
     * @return 得到标签信息
     */
    BasMaterialBarcode getBarcodeLabelInfo(Long barcode);

    /**
     * 得到标签信息
     *
     * @param materialSid 物料&商品&服务档案 sid
     * @return 得到标签信息
     */
    BasMaterial getLabelInfo(Long materialSid);

    /**
     * 查询商品条码列表 (偏完整版)
     *
     * @param basMaterialBarcode
     * @return 商品条码集合
     */
    List<BasMaterialBarcode> selectBasMaterialBarcodeList(BasMaterialBarcode basMaterialBarcode);

    /**
     * 其他业务模块查询商品条码 (简化版)
     *
     * @param basMaterialBarcode
     * @return 商品条码集合
     */
    List<BasMaterialBarcode> getBasMaterialBarcodeList(BasMaterialBarcode basMaterialBarcode);

    /**
     * 查询物料&商品&服务档案下的sku列表
     *
     * @param basMaterial 物料&商品&服务档案
     * @return 物料&商品&服务档案集合
     */
    List<BasMaterial> selectBasMaterialSkuList(BasMaterial basMaterial);

    /**
     * 新增物料&商品&服务档案
     *
     * @param basMaterial 物料&商品&服务档案
     * @return 结果
     */
    int insertBasMaterial(BasMaterial basMaterial);

    /**
     * 修改物料&商品&服务档案
     *
     * @param basMaterial 物料&商品&服务档案
     * @return 结果
     */
    int updateBasMaterial(BasMaterial basMaterial);

    /**
     * 创建bom时 校验
     *
     */
    public BasMaterial judgeBomCreate(Long materialSid);

    /**
     * 批量删除物料&商品&服务档案
     *
     * @param materialSids 需要删除的物料&商品&服务档案ID
     * @return 结果
     */
    int deleteBasMaterialByIds(Long[] materialSids);

    /**
     * 查询物料&商品&服务档案
     *
     * @param materialCode 物料&商品&服务档案编码
     * @return 物料&商品&服务档案
     */
    BasMaterial selectBasMaterialByCode(String materialCode, String businessType);

    /**
     * 样品档案查询页面确认前校验
     */
    EmsResultEntity confirmCheck(BasMaterial basMaterial);

    /**
     * 物料&商品&服务档案确认
     */
    int confirm(BasMaterial basMaterial);

    /**
     * 物料&商品&服务档案变更
     */
    String change(BasMaterial basMaterial);

    /**
     * 批量启用/停用物料&商品&服务档案
     */
    int status(BasMaterial basMaterial);

    /**
     * 设置未排产提醒天数 物料&商品&服务档案
     */
    int setWpcRemindDays(BasMaterial basMaterial);

    /**
     * 查询商品档案下的sku列表
     * @param request
     */
    List<BasMaterialSku> getBasMaterialSku(BasMaterialSkuRequest request);

    /**
     * 变更前调用的校验
     * @param basMaterial
     */
    EmsResultEntity changeVerify(BasMaterial basMaterial);

    /**
     * 生成商品条码 如果没有就新建，有就修改商品条码的启停状态
     */
    int insertBarcode(List<Long> materialSids);

    /**
     * 批量启用/停用商品条码
     */
    int barcodeStatus(BasMaterialBarcode basMaterialBarcode);

    /**
     * 批量修改是否创建商品线用量
     */
    int changeIsLine(List<Long> materialSids);

    /**
     * 批量修改是否创建BOM状态
     */
    int changeIsBom(List<Long> materialSids);

    /**
     * 批量修改是否创建产前成本核算状态
     */
    int changeIsCost(List<Long> materialSids);

    /**
     * 批量修改是否上传工艺单
     */
    int changeIsUploadGyd(List<Long> materialSids);

    /**
     * 批量修改是否档案类别
     */
    int changeCategory(BasMaterial basMaterial);

    /**
     * 邮件发送和动态通知
     */
    int sent(BasMaterial basMaterial);

    /**
     * 复制物料&商品&服务档案
     *
     * @param materialSid 物料&商品&服务档案ID
     * @return 物料&商品&服务档案
     */
    BasMaterial copyBasMaterialById(Long materialSid);

    /**
     * 设置快反款
     * @param basMaterial
     * @return
     */
    public int setKuaiFan(BasMaterial basMaterial);

    /**
     * 设置我方跟单员
     * @param basMaterial
     * @return
     */
    public int setOperator(BasMaterial basMaterial);

    /**
     * 设置供方业务员
     * @param basMaterial
     * @return
     */
    public int setOperatorVendor(BasMaterial basMaterial);

    /**
     * 设置客方业务--员
     * @param basMaterial
     * @return
     */
    public int setOperatorCustomer(BasMaterial basMaterial);

    /**
     * 设置商品编码(款号)
     * @param basMaterial
     * @return
     */
    public int setMaterialCode(BasMaterial basMaterial);

    /**
     * 设置负责生产工厂sid(默认)
     * @param basMaterial
     * @return
     */
    public int setProducePlant(BasMaterial basMaterial);

    /**
     * 设置物料/商品分类
     * @param basMaterial
     * @return
     */
    public int setMaterialClass(BasMaterial basMaterial);

    /**
     * 设置库存价核算方法
     * @param basMaterial
     * @return
     */
    public int setInventoryMethod(BasMaterial basMaterial);

    /**
     * 设置图片上传
     * @param basMaterial
     * @return
     */
    public int setPicture(BasMaterial basMaterial);

    /**
     * 设置多图片上传
     * @param basMaterial
     * @return
     */
    public int setPictures(BasMaterial basMaterial);

    /**
     * 按款添加明细 查询
     *
     */
    public MaterialAddResponse addBodyItem(MaterialAddRequest request);

    /**
     * 按款添加明细 转换成对应的明细信息
     */
    public List<OrderItemFunResponse> getItem(MaterialAddResponse data);

    public List<BasMaterialBarcode> sortBarcode(List<BasMaterialBarcode> salSalesOrderItemList);

    /**
     * 商品停用校验
     */
    public List<BasMaterialDisabledResponse> judgeDisable(List<BasMaterial> list);

    /**
     * 报表中心类目明细报表
     *
     * @param request BasMaterialSaleStationCategoryForm
     * @return 报表中心类目明细报表
     */
    List<BasMaterialSaleStationCategoryForm> selectBasMaterialSaleStationCategoryFormList(BasMaterialSaleStationCategoryForm request);

    /**
     * 报表中心类目明细报表 查看详情
     *
     * @param request BasMaterialSaleStation
     * @return BasMaterialSaleStation
     */
    List<BasMaterialSaleStation> selectBasMaterialSaleStationList(BasMaterialSaleStation request);

}

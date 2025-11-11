package com.platform.ems.mapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.dto.response.external.BasMaterialBarcodeExternal;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasMaterialBarcode;

/**
 * 商品条码Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-23
 */
public interface BasMaterialBarcodeMapper  extends BaseMapper<BasMaterialBarcode> {

    /**
     * 查询物料&商品&服务档案--外部打印产用
     *
     * @param materialSid 物料&商品&服务档案ID
     * @return 物料&商品&服务档案
     */
    List<BasMaterialBarcodeExternal> selectForExternalById(Long materialSid);

    /**
     * 查询商品条码信息 根据 商品条码查询
     * （外采样 颜色 尺码 未处理）
     * @param barcode 商品条码
     * @return 商品条码
     */
    BasMaterialBarcode selectBasMaterialBarcodeByCode(Long barcode);

    /**
     * 查询商品条码信息 根据 商品条码SID查询
     * （外采样 颜色 尺码 未处理）
     * @param barcodeSid 商品条码SID
     * @return 商品条码
     */
    BasMaterialBarcode selectBasMaterialBarcodeBySid(Long barcodeSid);

    /**
     * 查询商品条码信息 根据 商品条码SID查询
     * （外采样 颜色 尺码 有处理）
     * @param materialBarcodeSid 商品条码SID
     * @return 物料/商品/样品/外采样
     */
    BasMaterial selectBasMaterialBarcodeById(Long materialBarcodeSid);

    List<BasMaterialBarcode> getBasMaterialSkuName(@Param("barcodeSidList") List<Long> barcodeSidList);

    List<BasMaterialBarcode> selectBasMaterialBarcodeList(BasMaterialBarcode basMaterialBarcode);

    /**
     * 查询的排序
     * @param basMaterialBarcode
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<BasMaterialBarcode> selectBasMaterialBarcodeListSortCount(BasMaterialBarcode basMaterialBarcode);

    /**
     * 查询的排序
     * @param basMaterialBarcode
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<BasMaterialBarcode> selectBasMaterialBarcodeListSort(BasMaterialBarcode basMaterialBarcode);

    /**
     * 库存明细导入文件形式查找商品条码  精确查找
     * @param basMaterialBarcode 条件 商品编码 sku1名称 sku2名称
     * @return 列表
     */
    BasMaterialBarcode selectBasMaterialBarcodeListByInvImport(BasMaterialBarcode basMaterialBarcode);

    List<BasMaterialBarcode> getBasMaterialBarcodeList(BasMaterialBarcode basMaterialBarcode);

    BasMaterialBarcode selectBasMaterialBarcode(BasMaterialBarcode basMaterialBarcode);

    List<BasMaterial> getBasMaterialSkuList(BasMaterial basMaterial);

    /**
     * 按sku查找商品条码
     * @param
     * @return int
     */
    List<Long> selectSku(@Param("materialSid")Long materialSid,@Param("sku1Sid") Long sku1Sid,@Param("sku2Sid") Long sku2Sid);

    /**
     * 添加多个
     * @param list List BasMaterialBarcode
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialBarcode> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasMaterialBarcode
    * @return int
    */
    int updateAllById(BasMaterialBarcode entity);

    /**
     * 更新多个
     * @param list List BasMaterialBarcode
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialBarcode> list);

    /**
     * 只更新商品条形码
     * @param list List BasMaterialBarcode
     * @return int
     */
    int updatesShapeCodeById(@Param("list") List<BasMaterialBarcode> list);

    /**
     *
     */
    List<BasMaterialBarcode> selectBasMaterialBarcodePrecise(BasMaterialBarcode basMaterialBarcode);
}

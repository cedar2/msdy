package com.platform.ems.service;

import java.util.HashMap;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasMaterialBarcode;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.external.BasMaterialBarcodeExternal;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品条码Service接口
 *
 * @author linhongwei
 * @date 2021-04-23
 */
public interface IBasMaterialBarcodeService extends IService<BasMaterialBarcode>{

    /**
     * 查询物料&商品&服务档案--外部打印产用
     *
     * @param materialSid 物料&商品&服务档案ID
     * @return 物料&商品&服务档案
     */
    List<BasMaterialBarcodeExternal> selectForExternalById(Long materialSid);

    /**
     * 查询商品条码列表
     *
     * @param basMaterialBarcode 商品条码
     * @return 商品条码集合
     */
    public List<BasMaterialBarcode> selectBasMaterialBarcodeList(BasMaterialBarcode basMaterialBarcode);

    /**
     * 新增商品条码
     *
     * @param basMaterialBarcode 商品条码
     * @return 结果
     */
    public int insertBasMaterialBarcode(BasMaterialBarcode basMaterialBarcode);

    /**
     * 修改商品条码
     *
     * @param basMaterialBarcode 商品条码
     * @return 结果
     */
    public int updateBasMaterialBarcode(BasMaterialBarcode basMaterialBarcode);

    /**
     * 批量删除商品条码
     *
     * @param materialBarcodeSids 需要删除的商品条码ID
     * @return 结果
     */
    public int deleteBasMaterialBarcodeByIds(List<Long> materialBarcodeSids);

    /**
     * 设置产品级别
     *
     * @param basMaterialBarcode 设置产品级别
     * @return 结果
     */
    public int setProductLevel(BasMaterialBarcode basMaterialBarcode);

    /**
     * 设置商品SKU编码(ERP)
     *
     * @param basMaterialBarcode 设置产品级别
     * @return 结果
     */
    public int setErpCode(BasMaterialBarcode basMaterialBarcode);

    /**
     * 文件导入更新商品SKU编码(ERP)
     * @param file
     * @return
     */
    EmsResultEntity importErpCode(MultipartFile file);

    /**
     * 根据materialCode生成商品条码
     *
     * @author chenkw
     * @date 2022-05-18
     */
    HashMap<String, Object> importData(MultipartFile file, String materialCategory);

    /**
     * 商品条码导入商品条形码
     * @param file
     * @return
     */
    EmsResultEntity importBarcodeShapeCode(MultipartFile file);
}
